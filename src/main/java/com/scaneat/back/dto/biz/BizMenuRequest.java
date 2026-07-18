package com.scaneat.back.dto.biz;

import java.math.BigDecimal;

public record BizMenuRequest(
		String bizCatCd,
		String menuNm,
		String menuDesc,
		BigDecimal price,
		String imgUrl,
		String badge,
		Integer sortOrd,
		String useYn
) {
}
