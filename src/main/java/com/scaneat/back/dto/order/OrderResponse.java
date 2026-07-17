package com.scaneat.back.dto.order;

import com.scaneat.back.entity.UsrOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
		String orderNo,
		String uuid,
		String bizRegNo,
		String seatNo,
		String orderTypCd,
		BigDecimal totalAmount,
		String status,
		LocalDateTime regDt,
		List<OrderItemResponse> items
) {
	public static OrderResponse from(UsrOrder order, List<OrderItemResponse> items) {
		return new OrderResponse(
				order.getOrderNo(),
				order.getUuid(),
				order.getBizRegNo(),
				order.getSeatNo(),
				order.getOrderTypCd(),
				order.getTotalAmount(),
				order.getStatus().name(),
				order.getRegDt(),
				items
		);
	}
}
