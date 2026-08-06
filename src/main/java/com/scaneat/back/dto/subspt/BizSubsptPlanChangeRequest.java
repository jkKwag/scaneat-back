package com.scaneat.back.dto.subspt;

import jakarta.validation.constraints.NotBlank;

public record BizSubsptPlanChangeRequest(
		@NotBlank(message = "planCd는 필수입니다.") String planCd
) {
}
