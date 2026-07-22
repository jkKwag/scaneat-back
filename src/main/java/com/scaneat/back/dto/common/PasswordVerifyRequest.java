package com.scaneat.back.dto.common;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerifyRequest(
		@NotBlank(message = "비밀번호를 입력해주세요.")
		String password,
		@NotBlank(message = "요청자 정보가 없습니다.")
		String requesterId,
		@NotBlank(message = "요청자 권한 정보가 없습니다.")
		String requesterRole
) {
}
