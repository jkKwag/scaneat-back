package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

// 카카오 로그인 인가코드 -> 액세스 토큰 교환, 그리고 그 토큰으로 사용자 정보(닉네임/이메일) 조회.
@Component
public class KakaoClient {

	private final RestClient kakaoAuthRestClient;
	private final RestClient kakaoApiRestClient;
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public KakaoClient(
			RestClient kakaoAuthRestClient, RestClient kakaoApiRestClient,
			@Value("${kakao.client-id}") String clientId,
			@Value("${kakao.client-secret}") String clientSecret,
			@Value("${kakao.redirect-uri}") String redirectUri) {
		this.kakaoAuthRestClient = kakaoAuthRestClient;
		this.kakaoApiRestClient = kakaoApiRestClient;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
	}

	@SuppressWarnings("unchecked")
	public String exchangeCodeForAccessToken(String code) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);

		Map<String, Object> response;
		try {
			response = kakaoAuthRestClient.post()
					.uri("/oauth/token")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(body)
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "카카오 인증에 실패했습니다: " + ex.getResponseBodyAsString());
		}
		String accessToken = response == null ? null : (String) response.get("access_token");
		if (accessToken == null) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "카카오 액세스 토큰을 발급받지 못했습니다.");
		}
		return accessToken;
	}

	public record KakaoUserInfo(String providerUserId, String nickname, String email) {
	}

	@SuppressWarnings("unchecked")
	public KakaoUserInfo getUserInfo(String accessToken) {
		Map<String, Object> response;
		try {
			response = kakaoApiRestClient.get()
					.uri("/v2/user/me")
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "카카오 사용자 정보 조회에 실패했습니다: " + ex.getResponseBodyAsString());
		}
		if (response == null || response.get("id") == null) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "카카오 사용자 정보를 가져오지 못했습니다.");
		}
		String providerUserId = String.valueOf(response.get("id"));

		Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
		String nickname = null;
		String email = null;
		if (kakaoAccount != null) {
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
			if (profile != null) {
				nickname = (String) profile.get("nickname");
			}
			// 이메일 동의를 선택 항목으로 뒀기 때문에, 사용자가 동의 안 했으면 이 필드 자체가 없다.
			email = (String) kakaoAccount.get("email");
		}
		return new KakaoUserInfo(providerUserId, nickname, email);
	}
}
