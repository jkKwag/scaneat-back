package com.scaneat.back.dto.biz;

import java.math.BigDecimal;

public record SeatStatusResponse(
		String seatCd,
		String seatNm,
		Integer capacity,
		String zone,
		String state, // empty | seated | ordered | paid
		Integer elapsedMin,
		BigDecimal paidAmount,
		BigDecimal unpaidAmount,
		boolean warn
) {
}
