package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.order.OrderItemOptionRequest;
import com.scaneat.back.dto.order.OrderItemOptionResponse;
import com.scaneat.back.dto.order.OrderItemRequest;
import com.scaneat.back.dto.order.OrderItemResponse;
import com.scaneat.back.dto.order.OrderRequest;
import com.scaneat.back.dto.order.OrderResponse;
import com.scaneat.back.dto.order.OrderStatusUpdateRequest;
import com.scaneat.back.entity.OrderStatus;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.entity.UsrOrderItem;
import com.scaneat.back.entity.UsrOrderItemId;
import com.scaneat.back.entity.UsrOrderItemOpt;
import com.scaneat.back.entity.UsrOrderItemOptId;
import com.scaneat.back.entity.UsrPayment;
import com.scaneat.back.entity.UsrPaymentOrder;
import com.scaneat.back.repository.UsrOrderItemOptRepository;
import com.scaneat.back.repository.UsrOrderItemRepository;
import com.scaneat.back.repository.UsrOrderRepository;
import com.scaneat.back.repository.UsrPaymentOrderRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter PICKUP_NO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");

	private final UsrOrderRepository usrOrderRepository;
	private final UsrOrderItemRepository usrOrderItemRepository;
	private final UsrOrderItemOptRepository usrOrderItemOptRepository;
	private final UsrPaymentOrderRepository usrPaymentOrderRepository;
	private final UsrPaymentRepository usrPaymentRepository;
	private final OrderEventService orderEventService;

	public OrderResponse getOrder(String orderNo) {
		UsrOrder order = findOrder(orderNo);
		return buildOrderResponse(order);
	}

	public List<OrderResponse> getOrdersByUuid(String uuid) {
		return usrOrderRepository.findByUuidOrderByRegDtDesc(uuid).stream()
				.map(this::buildOrderResponse)
				.toList();
	}

	// 포장주문 시 휴대폰번호 입력란 자동입력용 — 이 손님(uuid)이 가장 최근에 남긴 번호 하나만 가볍게 조회
	public String getLastGuestPhone(String uuid) {
		return usrOrderRepository.findFirstByUuidAndGuestPhoneIsNotNullOrderByRegDtDesc(uuid)
				.map(UsrOrder::getGuestPhone)
				.orElse(null);
	}

	public List<OrderResponse> getOrdersByBiz(String bizRegNo) {
		return usrOrderRepository.findByBizRegNoOrderByRegDtDesc(bizRegNo).stream()
				.map(this::buildOrderResponse)
				.toList();
	}

	public List<OrderResponse> getOrdersByBiz(String bizRegNo, LocalDate from, LocalDate to) {
		LocalDateTime start = from.atStartOfDay();
		LocalDateTime end = to.plusDays(1).atStartOfDay();
		return usrOrderRepository.findByBizRegNoAndRegDtBetweenOrderByRegDtDesc(bizRegNo, start, end).stream()
				.map(this::buildOrderResponse)
				.toList();
	}

	@Transactional
	public OrderResponse createOrder(OrderRequest request) {
		LocalDateTime now = LocalDateTime.now();
		String orderNo = generateOrderNo();

		List<UsrOrderItem> items = new ArrayList<>();
		List<UsrOrderItemOpt> options = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;

		int seq = 1;
		for (OrderItemRequest itemRequest : request.items()) {
			UsrOrderItemId itemId = new UsrOrderItemId(orderNo, seq);
			items.add(UsrOrderItem.builder()
					.id(itemId)
					.menuCd(itemRequest.menuCd())
					.menuNm(itemRequest.menuNm())
					.price(itemRequest.price())
					.qty(itemRequest.qty())
					.regDt(now)
					.build());

			BigDecimal lineAmount = itemRequest.price();

			if (itemRequest.options() != null) {
				for (OrderItemOptionRequest optRequest : itemRequest.options()) {
					options.add(UsrOrderItemOpt.builder()
							.id(new UsrOrderItemOptId(orderNo, seq, optRequest.optCd()))
							.optNm(optRequest.optNm())
							.addPrice(optRequest.addPrice())
							.regDt(now)
							.build());
					lineAmount = lineAmount.add(optRequest.addPrice());
				}
			}

			totalAmount = totalAmount.add(lineAmount.multiply(BigDecimal.valueOf(itemRequest.qty())));
			seq++;
		}

		String orderTypCd = request.orderTypCd() != null && !request.orderTypCd().isBlank() ? request.orderTypCd() : "DINE_IN";
		String pickupNo = "TAKEOUT".equals(orderTypCd) ? generatePickupNo(request.bizRegNo()) : null;

		if ("TAKEOUT".equals(orderTypCd)
				&& (request.guestPhone() == null || !request.guestPhone().matches("\\d{11}"))) {
			throw new BusinessException("포장주문은 휴대폰번호(11자리)가 필요합니다.");
		}

		UsrOrder order = UsrOrder.builder()
				.orderNo(orderNo)
				.uuid(request.uuid())
				.bizRegNo(request.bizRegNo())
				.seatNo(request.seatNo())
				.orderTypCd(orderTypCd)
				.pickupNo(pickupNo)
				.guestPhone(request.guestPhone())
				.totalAmount(totalAmount)
				.status(OrderStatus.RECEIVED)
				.regUsrId("guest")
				.regDt(now)
				.build();

		usrOrderRepository.save(order);
		usrOrderItemRepository.saveAll(items);
		usrOrderItemOptRepository.saveAll(options);

		return buildOrderResponse(order);
	}

	@Transactional
	public OrderResponse updateStatus(String orderNo, OrderStatusUpdateRequest request) {
		UsrOrder order = findOrder(orderNo);
		OrderStatus nextStatus = OrderStatus.valueOf(request.status().toUpperCase());
		// 주문취소는 주방에서 준비를 시작하기 전(주문접수 단계)에만 허용한다.
		if (nextStatus == OrderStatus.CANCELED && order.getStatus() != OrderStatus.RECEIVED) {
			throw new BusinessException("이미 준비가 시작된 주문은 취소할 수 없습니다.");
		}
		order.setStatus(nextStatus);
		order.setUpdDt(LocalDateTime.now());
		OrderResponse response = buildOrderResponse(order);
		orderEventService.notifyOrderUpdated(order.getUuid(), response);
		return response;
	}

	public SseEmitter subscribe(String uuid) {
		return orderEventService.subscribe(uuid);
	}

	private UsrOrder findOrder(String orderNo) {
		return usrOrderRepository.findById(orderNo)
				.orElseThrow(() -> new ResourceNotFoundException("주문을 찾을 수 없습니다: " + orderNo));
	}

	private OrderResponse buildOrderResponse(UsrOrder order) {
		List<UsrOrderItem> items = usrOrderItemRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo());
		List<UsrOrderItemOpt> options = usrOrderItemOptRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo());

		Map<Integer, List<OrderItemOptionResponse>> optionsBySeq = options.stream()
				.collect(Collectors.groupingBy(o -> o.getId().getOrderSeq(),
						Collectors.mapping(OrderItemOptionResponse::from, Collectors.toList())));

		List<OrderItemResponse> itemResponses = items.stream()
				.map(item -> OrderItemResponse.from(item, optionsBySeq.getOrDefault(item.getId().getOrderSeq(), List.of())))
				.toList();

		String paymentStatus = usrPaymentOrderRepository.findById_OrderNo(order.getOrderNo())
				.flatMap(po -> usrPaymentRepository.findById(po.getId().getPaymentKey()))
				.map(UsrPayment::getStatus)
				.orElse(null);

		return OrderResponse.from(order, itemResponses, paymentStatus);
	}

	private String generateOrderNo() {
		String orderNo;
		int attempts = 0;
		do {
			String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
			orderNo = "O" + LocalDateTime.now().format(ORDER_NO_FORMAT) + suffix;
			attempts++;
		} while (usrOrderRepository.existsById(orderNo) && attempts < 5);
		return orderNo;
	}

	// 매장 + 당일 범위 안에서만 유일하면 되는 픽업번호 (전역 유일성은 불필요)
	private String generatePickupNo(String bizRegNo) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

		String pickupNo;
		int attempts = 0;
		do {
			String suffix = String.format("%05d", ThreadLocalRandom.current().nextInt(100000));
			pickupNo = today.format(PICKUP_NO_DATE_FORMAT) + "-" + suffix;
			attempts++;
		} while (usrOrderRepository.existsByBizRegNoAndPickupNoAndRegDtBetween(bizRegNo, pickupNo, startOfDay, startOfNextDay) && attempts < 5);
		return pickupNo;
	}
}
