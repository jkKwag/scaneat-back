package com.scaneat.back.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record TotpConfirmRequest(
		@NotBlank(message = "비밀키가 없습니다.") String secret,
		@NotBlank(message = "인증 코드를 입력해주세요.") String code
) {
}
