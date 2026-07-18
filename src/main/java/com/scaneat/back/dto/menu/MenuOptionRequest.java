package com.scaneat.back.dto.menu;

import java.math.BigDecimal;

public record MenuOptionRequest(
		String optNm,
		BigDecimal addPrice
) {
}
