package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizRsvnStd;

public record BizRsvnStdResponse(
		String bizRegNo,
		String useYn,
		Integer timeUnitMin,
		Integer minPartySize,
		Integer maxPartySize,
		Integer maxAdvanceDays,
		Integer minAdvanceHours
) {
	public static BizRsvnStdResponse from(BizRsvnStd std) {
		return new BizRsvnStdResponse(
				std.getBizRegNo(),
				std.getUseYn(),
				std.getTimeUnitMin(),
				std.getMinPartySize(),
				std.getMaxPartySize(),
				std.getMaxAdvanceDays(),
				std.getMinAdvanceHours()
		);
	}
}
