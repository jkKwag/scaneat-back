package com.scaneat.back.dto.common;

import com.scaneat.back.entity.CmmCd;

public record CmmCdResponse(
		String cd,
		String cdNm,
		String cdNmEn,
		String rmrk,
		String rmrk1,
		String rmrk2,
		String rmrk3,
		String rmrk4,
		String rmrk5,
		String rmrk6,
		String rmrk7,
		String rmrk8,
		String rmrk9,
		String rmrk10
) {
	public static CmmCdResponse from(CmmCd cmmCd) {
		return new CmmCdResponse(
				cmmCd.getId().getCd(),
				cmmCd.getCdNm(),
				cmmCd.getCdNmEn(),
				cmmCd.getRmrk(),
				cmmCd.getRmrk1(),
				cmmCd.getRmrk2(),
				cmmCd.getRmrk3(),
				cmmCd.getRmrk4(),
				cmmCd.getRmrk5(),
				cmmCd.getRmrk6(),
				cmmCd.getRmrk7(),
				cmmCd.getRmrk8(),
				cmmCd.getRmrk9(),
				cmmCd.getRmrk10()
		);
	}
}
