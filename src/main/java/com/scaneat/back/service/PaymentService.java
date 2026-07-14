package com.scaneat.back.service;

import com.scaneat.back.client.TossPaymentsClient;
import com.scaneat.back.dto.payment.PaymentConfirmRequest;
import com.scaneat.back.dto.payment.PaymentConfirmResponse;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final TossPaymentsClient tossPaymentsClient;

	public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
		Map<String, Object> result = tossPaymentsClient.confirmPayment(
				request.paymentKey(), request.orderId(), request.amount());

		return new PaymentConfirmResponse(
				(String) result.get("paymentKey"),
				(String) result.get("orderId"),
				(String) result.get("orderName"),
				result.get("totalAmount") != null ? new BigDecimal(result.get("totalAmount").toString()) : null,
				(String) result.get("status"),
				(String) result.get("method"),
				(String) result.get("approvedAt"),
				result.get("receipt") instanceof Map<?, ?> receipt ? (String) receipt.get("url") : null
		);
	}
}
