package com.scaneat.back.config;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 패스키(WebAuthn) 등록/로그인 검증에 필요한 공용 빈들. rp-id는 로그인 화면이 실제로 뜨는 도메인의
// 등록가능 도메인(registrable domain)과 일치해야 하고, origin은 그 화면의 정확한 scheme+host여야 한다.
@Getter
@Configuration
public class WebAuthnConfig {

	@Value("${app.webauthn.rp-id}")
	private String rpId;

	@Value("${app.webauthn.rp-name}")
	private String rpName;

	@Value("${app.webauthn.origin}")
	private String origin;

	@Bean
	public ObjectConverter objectConverter() {
		return new ObjectConverter();
	}

	@Bean
	public AttestedCredentialDataConverter attestedCredentialDataConverter(ObjectConverter objectConverter) {
		return new AttestedCredentialDataConverter(objectConverter);
	}

	@Bean
	public WebAuthnManager webAuthnManager(ObjectConverter objectConverter) {
		return WebAuthnManager.createNonStrictWebAuthnManager(objectConverter);
	}
}
