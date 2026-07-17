package com.scaneat.back.dto.common;

import com.scaneat.back.entity.CmmGrpCd;

public record CmmGrpCdResponse(
		String grpCd,
		String grpNm
) {
	public static CmmGrpCdResponse from(CmmGrpCd grpCd) {
		return new CmmGrpCdResponse(grpCd.getGrpCd(), grpCd.getGrpNm());
	}
}
