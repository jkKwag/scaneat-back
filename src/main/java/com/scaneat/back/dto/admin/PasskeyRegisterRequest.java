package com.scaneat.back.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record PasskeyRegisterRequest(
		// navigator.credentials.create()의 결과를 JSON.stringify()한 문자열 그대로.
		@NotBlank(message = "credential이 없습니다.") String credentialJson,
		// 'WEB' | 'IOS' | 'ANDROID' — 검증엔 안 쓰이고 기기 목록 표시용
		@NotBlank(message = "platform이 없습니다.") String platform,
		String deviceLabel
) {
}
