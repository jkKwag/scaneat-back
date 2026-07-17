package com.scaneat.back.dto.menu;

import com.scaneat.back.entity.BizMenuOptCd;
import java.math.BigDecimal;

public record MenuOptionResponse(
		String optCd,
		String optNm,
		BigDecimal addPrice,
		Integer sortOrd,
		String useYn
) {
	public static MenuOptionResponse from(BizMenuOptCd optCd) {
		return new MenuOptionResponse(
				optCd.getOptCd(),
				optCd.getOptNm(),
				optCd.getAddPrice(),
				optCd.getSortOrd(),
				optCd.getUseYn()
		);
	}
}
