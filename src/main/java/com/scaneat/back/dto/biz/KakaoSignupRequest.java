package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 카카오 인증(신원 확인)까지 끝난 뒤 사업자 정보만 마저 입력받는다 — adminId/password가 없다
// (로그인 수단이 카카오 자체라서 이메일/비밀번호 자체가 없는 계정으로 생성됨).
public record KakaoSignupRequest(
		@NotBlank(message = "카카오 인증 정보가 없습니다.") String signupToken,
		@NotBlank(message = "사업자등록번호를 입력해주세요.")
		@Pattern(regexp = "^[0-9]{10}$", message = "사업자등록번호는 숫자 10자리여야 합니다.")
		String bizRegNo,
		String bizNm,
		String repNm,
		@Pattern(regexp = "^[0-9]{9,11}$", message = "전화번호 형식이 올바르지 않습니다.")
		String telNo,
		@NotBlank(message = "휴대폰번호를 입력해주세요.")
		@Pattern(regexp = "^01[0-9]{8,9}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
		String mobileTel,
		String emailAddr,
		String indCd,
		String addr,
		String addrDtl
) {
}
