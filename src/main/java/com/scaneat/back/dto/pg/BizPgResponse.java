package com.scaneat.back.dto.pg;

import com.scaneat.back.entity.BizPg;
import java.time.LocalDateTime;

// 시크릿키 원본은 등록 후 다시 노출하지 않는다 — 등록 여부만 hasSecretKey로 알려준다
// (tb_biz_subspt의 hasBillingKey와 동일한 방식).
public record BizPgResponse(
		String pgProvider,
		boolean hasSecretKey,
		String clientKey,
		String status,
		LocalDateTime verifiedDt,
		LocalDateTime regDt
) {
	public static BizPgResponse from(BizPg bizPg) {
		return new BizPgResponse(
				bizPg.getId().getPgProvider(),
				bizPg.getSecretKeyEnc() != null,
				bizPg.getClientKey(),
				bizPg.getStatus(),
				bizPg.getVerifiedDt(),
				bizPg.getRegDt()
		);
	}
}
