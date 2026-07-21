package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;

public record EmpLoginRequest(
		@NotBlank(message = "아이디를 입력해주세요.") String empId,
		@NotBlank(message = "비밀번호를 입력해주세요.") String password
) {
}
