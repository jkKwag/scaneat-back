package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizCat;

public record BizCatResponse(
		String bizCatCd,
		String bizCatNm,
		Integer sortOrd,
		String useYn
) {
	public static BizCatResponse from(BizCat bizCat) {
		return new BizCatResponse(
				bizCat.getBizCatCd(),
				bizCat.getBizCatNm(),
				bizCat.getSortOrd(),
				bizCat.getUseYn()
		);
	}
}
