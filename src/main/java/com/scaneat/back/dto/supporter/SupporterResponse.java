package com.scaneat.back.dto.supporter;

import com.scaneat.back.entity.Supporter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupporterResponse(
		Long id,
		String name,
		BigDecimal amount,
		String message,
		LocalDateTime createdAt
) {
	public static SupporterResponse from(Supporter supporter) {
		return new SupporterResponse(
				supporter.getId(),
				supporter.getName(),
				supporter.getAmount(),
				supporter.getMessage(),
				supporter.getCreatedAt()
		);
	}
}
