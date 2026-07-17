package com.scaneat.back.dto.payment;

import com.scaneat.back.entity.UsrPaymentPg;
import java.time.LocalDateTime;

public record PaymentPgResponse(
		String lastTransactionKey,
		LocalDateTime requestedDt,
		String receiptUrl,
		String cardCompany,
		String cardNo
) {
	public static PaymentPgResponse from(UsrPaymentPg pg) {
		if (pg == null) {
			return null;
		}
		return new PaymentPgResponse(
				pg.getLastTransactionKey(),
				pg.getRequestedDt(),
				pg.getReceiptUrl(),
				pg.getCardCompany(),
				pg.getCardNo()
		);
	}
}
