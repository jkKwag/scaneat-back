package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public record BizHourItemRequest(
		@NotBlank(message = "dayOfWeek는 필수입니다.") String dayOfWeek,
		LocalTime openTime,
		LocalTime closeTime,
		String isClosed,
		LocalTime breakStartTime,
		LocalTime breakEndTime,
		LocalTime lastOrderTime
) {
}
