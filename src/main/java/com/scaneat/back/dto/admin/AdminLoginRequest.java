package com.scaneat.back.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
		@NotBlank(message = "아이디를 입력해주세요.") String adminId,
		@NotBlank(message = "비밀번호를 입력해주세요.") String password,
		// TOTP를 등록한 SUPER 계정만 필요 — 없으면 서버가 TOTP_REQUIRED로 응답한다.
		String totpCode
) {
}
