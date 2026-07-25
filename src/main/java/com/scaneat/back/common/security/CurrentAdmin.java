package com.scaneat.back.common.security;

// AdminAuthInterceptor가 유효한 세션 토큰을 확인한 뒤 요청 속성(REQUEST_ATTR)에 담아두는
// 인증된 관리자/직원 정보. adminRole은 AdminRole(super/admin) 또는 "EMPLOYEE".
public record CurrentAdmin(String adminId, String adminRole, String bizRegNo) {

	public static final String REQUEST_ATTR = "currentAdmin";

	public boolean isSuper() {
		return "SUPER".equals(adminRole);
	}
}
