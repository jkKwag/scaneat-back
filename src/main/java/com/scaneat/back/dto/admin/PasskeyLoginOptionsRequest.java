package com.scaneat.back.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record PasskeyLoginOptionsRequest(
		@NotBlank(message = "이메일을 입력해주세요.") String adminId
) {
}
