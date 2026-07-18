package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizHourStd;
import java.time.LocalTime;

public record BizHourResponse(
		String dayOfWeek,
		LocalTime openTime,
		LocalTime closeTime,
		String isClosed,
		LocalTime breakStartTime,
		LocalTime breakEndTime,
		LocalTime lastOrderTime
) {
	public static BizHourResponse from(BizHourStd hour) {
		return new BizHourResponse(
				hour.getId().getDayOfWeek(),
				hour.getOpenTime(),
				hour.getCloseTime(),
				hour.getIsClosed(),
				hour.getBreakStartTime(),
				hour.getBreakEndTime(),
				hour.getLastOrderTime()
		);
	}
}
