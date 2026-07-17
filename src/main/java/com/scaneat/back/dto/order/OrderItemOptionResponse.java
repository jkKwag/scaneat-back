package com.scaneat.back.dto.order;

import com.scaneat.back.entity.UsrOrderItemOpt;
import java.math.BigDecimal;

public record OrderItemOptionResponse(
		String optCd,
		String optNm,
		BigDecimal addPrice
) {
	public static OrderItemOptionResponse from(UsrOrderItemOpt opt) {
		return new OrderItemOptionResponse(opt.getId().getOptCd(), opt.getOptNm(), opt.getAddPrice());
	}
}
