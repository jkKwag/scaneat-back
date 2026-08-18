package com.scaneat.back.dto.biz;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeatOrderResponse(
		String orderNo,
		LocalDateTime regDt,
		BigDecimal amount,
		boolean paid
) {
}
