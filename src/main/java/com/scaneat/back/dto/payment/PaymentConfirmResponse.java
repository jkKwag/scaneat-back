package com.scaneat.back.dto.payment;

import java.math.BigDecimal;

public record PaymentConfirmResponse(
		String paymentKey,
		String orderId,
		String orderName,
		BigDecimal totalAmount,
		String status,
		String method,
		String approvedAt,
		String receiptUrl
) {
}
