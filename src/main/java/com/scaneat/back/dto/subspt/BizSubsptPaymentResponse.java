package com.scaneat.back.dto.subspt;

import com.scaneat.back.entity.BizSubsptPayment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BizSubsptPaymentResponse(
		String paymentKey,
		String planCd,
		String billingPeriod,
		BigDecimal suppliedAmount,
		BigDecimal vat,
		BigDecimal totalAmount,
		String status,
		LocalDateTime approvedDt,
		String receiptUrl,
		String failReason,
		BigDecimal refundAmount,
		LocalDateTime refundedDt
) {
	public static BizSubsptPaymentResponse from(BizSubsptPayment payment) {
		return new BizSubsptPaymentResponse(
				payment.getPaymentKey(),
				payment.getPlanCd(),
				payment.getBillingPeriod(),
				payment.getSuppliedAmount(),
				payment.getVat(),
				payment.getSuppliedAmount().add(payment.getVat()),
				payment.getStatus(),
				payment.getApprovedDt(),
				payment.getReceiptUrl(),
				payment.getFailReason(),
				payment.getRefundAmount(),
				payment.getRefundedDt()
		);
	}
}
