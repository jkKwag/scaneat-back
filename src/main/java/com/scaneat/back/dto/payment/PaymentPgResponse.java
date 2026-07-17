package com.scaneat.back.dto.payment;

import com.scaneat.back.entity.UsrPaymentPg;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentPgResponse(
		String lastTransactionKey,
		LocalDateTime requestedDt,
		String receiptUrl,
		String issuerCode,
		String cardNo,
		String approveNo,
		Integer installmentMonths,
		BigDecimal suppliedAmount,
		BigDecimal vat,
		String orderName
) {
	public static PaymentPgResponse from(UsrPaymentPg pg) {
		if (pg == null) {
			return null;
		}
		return new PaymentPgResponse(
				pg.getLastTransactionKey(),
				pg.getRequestedDt(),
				pg.getReceiptUrl(),
				pg.getIssuerCode(),
				pg.getCardNo(),
				pg.getApproveNo(),
				pg.getInstallmentMonths(),
				pg.getSuppliedAmount(),
				pg.getVat(),
				pg.getOrderName()
		);
	}
}
