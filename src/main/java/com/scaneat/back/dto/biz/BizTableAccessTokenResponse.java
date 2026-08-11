package com.scaneat.back.dto.biz;

import java.time.LocalDateTime;

public record BizTableAccessTokenResponse(
		String token,
		LocalDateTime expiresAt
) {
}
