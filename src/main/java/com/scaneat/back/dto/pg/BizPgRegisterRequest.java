package com.scaneat.back.dto.pg;

import jakarta.validation.constraints.NotBlank;

public record BizPgRegisterRequest(
		// 지금은 TOSS만 지원하지만 다른 PG사 확장을 대비해 값을 받아둔다 — 비워서 보내면 서비스단에서 TOSS로 처리.
		String pgProvider,
		@NotBlank(message = "시크릿키는 필수입니다.") String secretKey,
		String clientKey
) {
}
