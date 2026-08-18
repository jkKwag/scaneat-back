package com.scaneat.back.dto.biz;

import java.math.BigDecimal;
import java.util.List;

public record SeatStatusResponse(
		String seatCd,
		String seatNm,
		Integer capacity,
		String zone,
		String state, // empty | seated | ordered | paid
		Integer elapsedMin,
		BigDecimal paidAmount,
		BigDecimal unpaidAmount,
		boolean warn,
		List<SeatOrderResponse> orders
) {
}
