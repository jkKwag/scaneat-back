package com.scaneat.back.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
		@NotBlank(message = "현재 비밀번호를 입력해주세요.")
		String currentPassword,
		@NotBlank(message = "새 비밀번호를 입력해주세요.")
		@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
		String newPassword,
		@NotBlank(message = "요청자 정보가 없습니다.")
		String requesterId,
		@NotBlank(message = "요청자 권한 정보가 없습니다.")
		String requesterRole
) {
}
