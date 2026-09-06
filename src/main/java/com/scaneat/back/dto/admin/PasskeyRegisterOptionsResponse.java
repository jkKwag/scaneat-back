package com.scaneat.back.dto.admin;

import java.util.List;

// navigator.credentials.create()에 그대로 넘길 수 있는 형태 — 프론트에서 challenge/user.id/
// excludeCredentials의 id는 base64url 문자열이라 ArrayBuffer로 디코딩해서 써야 한다.
public record PasskeyRegisterOptionsResponse(
		String rpId,
		String rpName,
		String challenge,
		String userId,
		String userName,
		String userDisplayName,
		List<PubKeyCredParam> pubKeyCredParams,
		List<CredentialDescriptor> excludeCredentials,
		long timeoutMillis
) {
	public record PubKeyCredParam(String type, int alg) {
	}

	public record CredentialDescriptor(String type, String id) {
	}
}
