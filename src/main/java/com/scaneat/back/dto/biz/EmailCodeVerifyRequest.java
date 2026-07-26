package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCodeVerifyRequest(
		@NotBlank(message = "이메일을 입력해주세요.")
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		String email,
		@NotBlank(message = "인증코드를 입력해주세요.")
		String code
) {
}
