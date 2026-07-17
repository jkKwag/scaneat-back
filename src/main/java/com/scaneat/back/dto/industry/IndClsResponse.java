package com.scaneat.back.dto.industry;

import com.scaneat.back.entity.IndCls;

public record IndClsResponse(
		String indCd,
		String indNm
) {
	public static IndClsResponse from(IndCls indCls) {
		return new IndClsResponse(indCls.getIndCd(), indCls.getIndNm());
	}
}
