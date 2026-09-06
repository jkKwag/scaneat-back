package com.scaneat.back.dto.biz;

import com.scaneat.back.dto.admin.AdminLoginResponse;

// loggedIn=true면 이미 가입된 카카오 계정 -> login에 바로 세션 정보가 담겨있음.
// loggedIn=false면 처음 보는 카카오 계정 -> signupToken을 들고 사업자 정보 입력 화면으로 넘어가야 함.
public record KakaoExchangeResponse(
		boolean loggedIn,
		AdminLoginResponse login,
		String signupToken,
		String nickname,
		String email
) {
	public static KakaoExchangeResponse loggedIn(AdminLoginResponse login) {
		return new KakaoExchangeResponse(true, login, null, null, null);
	}

	public static KakaoExchangeResponse needsSignup(String signupToken, String nickname, String email) {
		return new KakaoExchangeResponse(false, null, signupToken, nickname, email);
	}
}
