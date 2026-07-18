package com.scaneat.back.dto.biz;

public record BizRsvnStdRequest(
		String useYn,
		Integer timeUnitMin,
		Integer minPartySize,
		Integer maxPartySize,
		Integer maxAdvanceDays,
		Integer minAdvanceHours
) {
}
