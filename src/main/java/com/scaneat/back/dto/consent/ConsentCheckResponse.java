package com.scaneat.back.dto.consent;

import java.time.LocalDateTime;

public record ConsentCheckResponse(
		boolean consented,
		LocalDateTime consentAt
) {
}
