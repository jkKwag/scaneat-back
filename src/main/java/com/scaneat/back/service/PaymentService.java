package com.scaneat.back.service;

import com.scaneat.back.client.TossPaymentsClient;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.payment.PaymentConfirmRequest;
import com.scaneat.back.dto.payment.PaymentPgResponse;
import com.scaneat.back.dto.payment.PaymentResponse;
import com.scaneat.back.entity.OrderStatus;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.entity.UsrPayment;
import com.scaneat.back.entity.UsrPaymentOrder;
import com.scaneat.back.entity.UsrPaymentOrderId;
import com.scaneat.back.entity.UsrPaymentPg;
import com.scaneat.back.repository.UsrOrderRepository;
import com.scaneat.back.repository.UsrPaymentOrderRepository;
import com.scaneat.back.repository.UsrPaymentPgRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

	private final TossPaymentsClient tossPaymentsClient;
	private final UsrOrderRepository usrOrderRepository;
	private final UsrPaymentRepository usrPaymentRepository;
	private final UsrPaymentPgRepository usrPaymentPgRepository;
	private final UsrPaymentOrderRepository usrPaymentOrderRepository;

	@Transactional
	public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
		List<UsrOrder> orders = usrOrderRepository.findAllById(request.orderNos());
		if (orders.size() != request.orderNos().size()) {
			throw new ResourceNotFoundException("존재하지 않는 주문번호가 포함되어 있습니다.");
		}

		Map<String, Object> result = tossPaymentsClient.confirmPayment(
				request.paymentKey(), request.orderId(), request.amount());

		LocalDateTime now = LocalDateTime.now();
		UsrOrder firstOrder = orders.get(0);

		UsrPayment payment = UsrPayment.builder()
				.paymentKey(request.paymentKey())
				.uuid(firstOrder.getUuid())
				.bizRegNo(firstOrder.getBizRegNo())
				.totalAmount(request.amount())
				.method((String) result.get("method"))
				.status((String) result.get("status"))
				.approvedDt(parseTossDateTime((String) result.get("approvedAt")))
				.regUsrId("guest")
				.regDt(now)
				.build();
		usrPaymentRepository.save(payment);

		Object cardObj = result.get("card");
		Map<?, ?> card = cardObj instanceof Map<?, ?> m ? m : null;
		Object receiptObj = result.get("receipt");
		Map<?, ?> receipt = receiptObj instanceof Map<?, ?> m ? m : null;

		UsrPaymentPg pg = UsrPaymentPg.builder()
				.paymentKey(request.paymentKey())
				.lastTransactionKey((String) result.get("lastTransactionKey"))
				.requestedDt(parseTossDateTime((String) result.get("requestedAt")))
				.receiptUrl(receipt != null ? (String) receipt.get("url") : null)
				.cardCompany(card != null ? (String) card.get("company") : null)
				.cardNo(card != null ? (String) card.get("number") : null)
				.regDt(now)
				.build();
		usrPaymentPgRepository.save(pg);

		List<UsrPaymentOrder> mappings = orders.stream()
				.map(order -> UsrPaymentOrder.builder()
						.id(new UsrPaymentOrderId(request.paymentKey(), order.getOrderNo()))
						.regDt(now)
						.build())
				.toList();
		usrPaymentOrderRepository.saveAll(mappings);

		orders.forEach(order -> order.setStatus(OrderStatus.PAID));

		return PaymentResponse.from(payment, request.orderNos(), PaymentPgResponse.from(pg));
	}

	public PaymentResponse getPayment(String paymentKey) {
		UsrPayment payment = usrPaymentRepository.findById(paymentKey)
				.orElseThrow(() -> new ResourceNotFoundException("결제 정보를 찾을 수 없습니다: " + paymentKey));
		List<String> orderNos = usrPaymentOrderRepository.findById_PaymentKey(paymentKey).stream()
				.map(po -> po.getId().getOrderNo())
				.toList();
		PaymentPgResponse pg = usrPaymentPgRepository.findById(paymentKey)
				.map(PaymentPgResponse::from)
				.orElse(null);
		return PaymentResponse.from(payment, orderNos, pg);
	}

	public List<PaymentResponse> getPaymentsByUuid(String uuid) {
		return usrPaymentRepository.findByUuidOrderByRegDtDesc(uuid).stream()
				.map(payment -> getPayment(payment.getPaymentKey()))
				.toList();
	}

	private LocalDateTime parseTossDateTime(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			return OffsetDateTime.parse(raw).toLocalDateTime();
		} catch (DateTimeParseException ex) {
			throw new BusinessException("결제 일시 형식을 해석할 수 없습니다: " + raw);
		}
	}
}
