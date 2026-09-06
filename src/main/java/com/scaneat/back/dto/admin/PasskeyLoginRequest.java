package com.scaneat.back.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record PasskeyLoginRequest(
		@NotBlank(message = "flowId가 없습니다.") String flowId,
		// navigator.credentials.get()의 결과를 JSON.stringify()한 문자열 그대로.
		@NotBlank(message = "credential이 없습니다.") String credentialJson
) {
}
