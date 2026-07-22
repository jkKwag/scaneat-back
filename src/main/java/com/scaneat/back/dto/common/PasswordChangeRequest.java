package com.scaneat.back.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
		@NotBlank(message = "새 비밀번호를 입력해주세요.")
		@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
		String newPassword
) {
}
