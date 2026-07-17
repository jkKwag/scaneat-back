package com.scaneat.back.dto.order;

import com.scaneat.back.entity.UsrOrderItem;
import java.math.BigDecimal;
import java.util.List;

public record OrderItemResponse(
		Integer orderSeq,
		String menuCd,
		String menuNm,
		BigDecimal price,
		Integer qty,
		List<OrderItemOptionResponse> options
) {
	public static OrderItemResponse from(UsrOrderItem item, List<OrderItemOptionResponse> options) {
		return new OrderItemResponse(
				item.getId().getOrderSeq(),
				item.getMenuCd(),
				item.getMenuNm(),
				item.getPrice(),
				item.getQty(),
				options
		);
	}
}
