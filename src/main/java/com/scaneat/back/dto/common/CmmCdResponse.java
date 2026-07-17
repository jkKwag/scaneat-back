package com.scaneat.back.dto.common;

import com.scaneat.back.entity.CmmCd;

public record CmmCdResponse(
		String cd,
		String cdNm
) {
	public static CmmCdResponse from(CmmCd cmmCd) {
		return new CmmCdResponse(cmmCd.getId().getCd(), cmmCd.getCdNm());
	}
}
