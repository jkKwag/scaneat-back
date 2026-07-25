package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;

public record BizRejectRequest(
		@NotBlank(message = "거부 사유를 입력해주세요.") String reason
) {
}
