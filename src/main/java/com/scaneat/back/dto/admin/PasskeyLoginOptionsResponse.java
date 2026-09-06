package com.scaneat.back.dto.admin;

import java.util.List;

// hasPasskey가 false면 나머지 필드는 전부 null — 프론트는 이때 비밀번호 로그인 화면으로 넘어간다.
// 계정이 아예 없는 이메일이어도 존재 여부를 노출하지 않기 위해 동일하게 hasPasskey:false로 응답한다.
public record PasskeyLoginOptionsResponse(
		boolean hasPasskey,
		String flowId,
		String rpId,
		String challenge,
		List<PasskeyRegisterOptionsResponse.CredentialDescriptor> allowCredentials,
		long timeoutMillis
) {
	public static PasskeyLoginOptionsResponse none() {
		return new PasskeyLoginOptionsResponse(false, null, null, null, null, 0);
	}
}
