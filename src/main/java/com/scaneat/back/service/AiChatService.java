package com.scaneat.back.service;

import com.scaneat.back.client.GeminiClient;
import com.scaneat.back.client.GeminiFunctionCall;
import com.scaneat.back.client.GeminiResult;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.dto.ai.AiChatRequest;
import com.scaneat.back.dto.ai.AiChatResponse;
import com.scaneat.back.dto.ai.CartItemDto;
import com.scaneat.back.dto.reservation.ReservationRequest;
import com.scaneat.back.dto.reservation.ReservationResponse;
import com.scaneat.back.dto.reservation.ReservationUpdateRequest;
import com.scaneat.back.entity.BizMenu;
import com.scaneat.back.repository.BizMenuRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiChatService {

	private static final String SYSTEM_INSTRUCTION_TEMPLATE = """
			너는 스캔잇(ScanEat) 매장의 주문/예약을 돕는 AI 어시스턴트다.
			손님의 요청에 따라 필요하면 제공된 함수를 호출하고, 그렇지 않으면 친절한 한국어로 답변해라.
			현재 장바구니: %s
			""";

	private final GeminiClient geminiClient;
	private final BizMenuRepository bizMenuRepository;
	private final ReservationService reservationService;

	public AiChatResponse chat(AiChatRequest request) {
		List<CartItemDto> cart = request.cart() != null ? new ArrayList<>(request.cart()) : new ArrayList<>();
		String systemInstruction = SYSTEM_INSTRUCTION_TEMPLATE.formatted(describeCart(cart));

		GeminiResult result = geminiClient.generateContent(systemInstruction, request.message());
		GeminiFunctionCall functionCall = result.functionCall();

		if (functionCall == null) {
			return new AiChatResponse(result.text(), null, null, cart);
		}

		return switch (functionCall.name()) {
			case "recommend_item" -> handleRecommendItem(request, functionCall, cart);
			case "remove_item" -> handleRemoveItem(functionCall, cart);
			case "clear_cart" -> handleClearCart(cart);
			case "request_checkout" -> handleRequestCheckout(cart);
			case "request_reservation" -> handleRequestReservation(request, functionCall, cart);
			case "modify_reservation" -> handleModifyReservation(functionCall, cart);
			default -> new AiChatResponse(result.text(), functionCall.name(), null, cart);
		};
	}

	private AiChatResponse handleRecommendItem(AiChatRequest request, GeminiFunctionCall call, List<CartItemDto> cart) {
		Map<String, Object> args = call.args() != null ? call.args() : Map.of();
		String keyword = (String) args.getOrDefault("keyword", args.get("category"));

		List<BizMenu> menus = keyword != null && !keyword.isBlank()
				? bizMenuRepository.findByBizRegNoAndMenuNmContaining(request.bizRegNo(), keyword)
				: bizMenuRepository.findByBizRegNoOrderBySortOrdAsc(request.bizRegNo());

		List<Map<String, Object>> recommendations = menus.stream()
				.limit(3)
				.map(menu -> Map.<String, Object>of(
						"menuCd", menu.getMenuCd(),
						"menuNm", menu.getMenuNm(),
						"price", menu.getPrice()))
				.toList();

		String reply = recommendations.isEmpty()
				? "조건에 맞는 메뉴를 찾지 못했어요."
				: "이런 메뉴는 어떠세요? " + recommendations.stream().map(m -> m.get("menuNm")).toList();

		return new AiChatResponse(reply, "recommend_item", Map.of("recommendations", recommendations), cart);
	}

	private AiChatResponse handleRemoveItem(GeminiFunctionCall call, List<CartItemDto> cart) {
		String menuNm = call.args() != null ? (String) call.args().get("menuNm") : null;
		if (menuNm == null) {
			throw new BusinessException("제거할 메뉴명이 필요합니다.");
		}

		List<CartItemDto> updatedCart = cart.stream()
				.filter(item -> !item.menuNm().equalsIgnoreCase(menuNm))
				.toList();
		boolean removed = updatedCart.size() != cart.size();

		String reply = removed ? menuNm + " 메뉴를 장바구니에서 제거했어요." : menuNm + " 메뉴를 장바구니에서 찾지 못했어요.";
		return new AiChatResponse(reply, "remove_item", Map.of("removed", removed), updatedCart);
	}

	private AiChatResponse handleClearCart(List<CartItemDto> cart) {
		return new AiChatResponse("장바구니를 비웠어요.", "clear_cart", Map.of("cleared", true), List.of());
	}

	private AiChatResponse handleRequestCheckout(List<CartItemDto> cart) {
		BigDecimal total = cart.stream()
				.map(item -> item.price().multiply(BigDecimal.valueOf(item.qty())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		String reply = cart.isEmpty()
				? "장바구니가 비어있어 결제를 진행할 수 없어요."
				: "결제를 도와드릴게요. 총 결제 금액은 " + total + "원입니다.";

		return new AiChatResponse(reply, "request_checkout", Map.of("totalAmount", total), cart);
	}

	private AiChatResponse handleRequestReservation(AiChatRequest request, GeminiFunctionCall call, List<CartItemDto> cart) {
		Map<String, Object> args = call.args() != null ? call.args() : Map.of();
		String guestName = (String) args.get("guestName");
		String rsvnDtRaw = (String) args.get("rsvnDt");
		if (guestName == null || rsvnDtRaw == null) {
			throw new BusinessException("예약자명과 예약 일시가 필요합니다.");
		}

		ReservationRequest reservationRequest = new ReservationRequest(
				request.uuid(),
				request.bizRegNo(),
				guestName,
				(String) args.get("guestTel"),
				parseDateTime(rsvnDtRaw),
				(String) args.get("seatNo"),
				args.get("partySize") != null ? ((Number) args.get("partySize")).intValue() : null,
				(String) args.get("memo")
		);

		ReservationResponse reservation = reservationService.createReservation(reservationRequest);
		String reply = "%s님, %s에 예약이 접수되었어요. 예약번호는 %s입니다.".formatted(
				guestName, reservation.rsvnDt(), reservation.rsvnNo());

		return new AiChatResponse(reply, "request_reservation", Map.of("reservation", reservation), cart);
	}

	private AiChatResponse handleModifyReservation(GeminiFunctionCall call, List<CartItemDto> cart) {
		Map<String, Object> args = call.args() != null ? call.args() : Map.of();
		String rsvnNo = (String) args.get("rsvnNo");
		if (rsvnNo == null) {
			throw new BusinessException("수정할 예약 번호가 필요합니다.");
		}

		ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(
				(String) args.get("guestName"),
				(String) args.get("guestTel"),
				args.get("rsvnDt") != null ? parseDateTime((String) args.get("rsvnDt")) : null,
				(String) args.get("seatNo"),
				args.get("partySize") != null ? ((Number) args.get("partySize")).intValue() : null,
				(String) args.get("memo"),
				(String) args.get("status")
		);

		ReservationResponse reservation = reservationService.updateReservation(rsvnNo, updateRequest);
		String reply = "예약번호 %s의 예약 내용을 수정했어요.".formatted(rsvnNo);

		return new AiChatResponse(reply, "modify_reservation", Map.of("reservation", reservation), cart);
	}

	private LocalDateTime parseDateTime(String raw) {
		try {
			return LocalDateTime.parse(raw);
		} catch (DateTimeParseException ex) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "예약 일시 형식이 올바르지 않습니다: " + raw);
		}
	}

	private String describeCart(List<CartItemDto> cart) {
		if (cart.isEmpty()) {
			return "비어있음";
		}
		return cart.stream()
				.map(item -> "%s x%d(%s원)".formatted(item.menuNm(), item.qty(), item.price()))
				.reduce((a, b) -> a + ", " + b)
				.orElse("비어있음");
	}
}
