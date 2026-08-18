package com.scaneat.back.dto.biz;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SeatOrderResponse(
		String orderNo,
		LocalDateTime regDt,
		BigDecimal amount,
		boolean paid,
		String status, // RECEIVED | PREPARING | READY | CANCELED — 주문관리와 동일한 조리진행상태
		List<SeatOrderItemResponse> items
) {
}
