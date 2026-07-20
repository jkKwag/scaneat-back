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
		String pickupNo,
		BigDecimal totalAmount,
		String status,
		String paymentStatus,
		LocalDateTime regDt,
		LocalDateTime updDt,
		List<OrderItemResponse> items
) {
	public static OrderResponse from(UsrOrder order, List<OrderItemResponse> items, String paymentStatus) {
		return new OrderResponse(
				order.getOrderNo(),
				order.getUuid(),
				order.getBizRegNo(),
				order.getSeatNo(),
				order.getOrderTypCd(),
				order.getPickupNo(),
				order.getTotalAmount(),
				order.getStatus().name(),
				paymentStatus,
				order.getRegDt(),
				order.getUpdDt(),
				items
		);
	}
}
