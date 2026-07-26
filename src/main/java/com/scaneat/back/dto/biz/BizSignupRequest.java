package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BizSignupRequest(
		@NotBlank(message = "사업자등록번호를 입력해주세요.") String bizRegNo,
		@NotBlank(message = "상호명을 입력해주세요.") String bizNm,
		// 대표자명은 나중에 사업자등록증 이미지 인식으로 채울 예정이라 가입 시점엔 받지 않는다.
		String repNm,
		String telNo,
		String mobileTel,
		String emailAddr,
		String indCd,
		String addr,
		String addrDtl,
		// admin_id(PK)를 이메일로 쓰기로 해서, 관리자 아이디는 이메일 형식만 허용한다.
		@NotBlank(message = "이메일을 입력해주세요.")
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		String adminId,
		@NotBlank(message = "비밀번호를 입력해주세요.")
		@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
		String password
) {
}
