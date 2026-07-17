package com.scaneat.back.dto.payment;

import com.scaneat.back.entity.UsrPayment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentResponse(
		String paymentKey,
		String uuid,
		String bizRegNo,
		BigDecimal totalAmount,
		String method,
		String status,
		LocalDateTime approvedDt,
		List<String> orderNos,
		PaymentPgResponse pg
) {
	public static PaymentResponse from(UsrPayment payment, List<String> orderNos, PaymentPgResponse pg) {
		return new PaymentResponse(
				payment.getPaymentKey(),
				payment.getUuid(),
				payment.getBizRegNo(),
				payment.getTotalAmount(),
				payment.getMethod(),
				payment.getStatus(),
				payment.getApprovedDt(),
				orderNos,
				pg
		);
	}
}
