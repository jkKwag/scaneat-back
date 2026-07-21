package com.scaneat.back.dto.industry;

import com.scaneat.back.entity.IndCls;

public record IndClsResponse(
		String indCd,
		String prntCd,
		String indNm,
		Integer clsLvl,
		Integer sortOrd,
		String useYn
) {
	public static IndClsResponse from(IndCls indCls) {
		return new IndClsResponse(
				indCls.getIndCd(),
				indCls.getPrntCd(),
				indCls.getIndNm(),
				indCls.getClsLvl(),
				indCls.getSortOrd(),
				indCls.getUseYn()
		);
	}
}
