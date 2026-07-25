package com.scaneat.back.dto.common;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerifyRequest(
		@NotBlank(message = "비밀번호를 입력해주세요.")
		String password
) {
}
