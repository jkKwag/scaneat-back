package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizCat;

public record BizCatResponse(
		String bizCatCd,
		String catCd,
		String bizCatNm,
		Integer sortOrd,
		String useYn,
		String rmrk
) {
	public static BizCatResponse from(BizCat bizCat) {
		return new BizCatResponse(
				bizCat.getBizCatCd(),
				bizCat.getCatCd(),
				bizCat.getBizCatNm(),
				bizCat.getSortOrd(),
				bizCat.getUseYn(),
				bizCat.getRmrk()
		);
	}
}
