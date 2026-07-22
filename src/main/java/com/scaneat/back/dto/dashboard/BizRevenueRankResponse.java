package com.scaneat.back.dto.dashboard;

import java.math.BigDecimal;

public record BizRevenueRankResponse(
		String bizRegNo,
		String bizNm,
		BigDecimal totalAmount,
		long paymentCount
) {
}
