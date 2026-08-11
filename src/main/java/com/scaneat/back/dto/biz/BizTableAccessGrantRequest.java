package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;

public record BizTableAccessGrantRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "token은 필수입니다.") String token
) {
}
