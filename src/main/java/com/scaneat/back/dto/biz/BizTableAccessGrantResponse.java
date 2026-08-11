package com.scaneat.back.dto.biz;

import java.time.LocalDateTime;

public record BizTableAccessGrantResponse(
		boolean granted,
		LocalDateTime expiresAt
) {
}
