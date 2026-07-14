package com.scaneat.back.dto.ai;

import java.math.BigDecimal;
import java.util.List;

public record CartItemDto(
		String menuCd,
		String menuNm,
		BigDecimal price,
		int qty,
		List<String> options
) {
}
