package com.scaneat.back.dto.biz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BizHourRequest(
		@NotEmpty(message = "hours는 최소 1개 필요합니다.") @Valid List<BizHourItemRequest> hours
) {
}
