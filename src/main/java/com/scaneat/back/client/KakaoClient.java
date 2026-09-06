package com.scaneat.back.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaneat.back.common.exception.BusinessException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

// 카카오 로그인 인가코드 -> 액세스 토큰 교환, 그 토큰으로 사용자 정보(닉네임/이메일) 조회,
// 그리고 가입 완료 직후 "나에게 보내기"로 웰컴 메시지 발송까지 담당한다.
@Component
public class KakaoClient {

	private static final Logger log = LoggerFactory.getLogger(KakaoClient.class);

	private final RestClient kakaoAuthRestClient;
	private final RestClient kakaoApiRestClient;
	private final ObjectMapper objectMapper;
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final String serviceUrl;

	public KakaoClient(
			RestClient kakaoAuthRestClient, RestClient kakaoApiRestClient, ObjectMapper objectMapper,
			@Value("${kakao.client-id}") String clientId,
			@Value("${kakao.client-secret}") String clientSecret,
			@Value("${kakao.redirect-uri}") String redirectUri,
			@Value("${kakao.service-url}") String serviceUrl) {
		this.kakaoAuthRestClient = kakaoAuthRestClient;
		this.kakaoApiRestClient = kakaoApiRestClient;
		this.objectMapper = objectMapper;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.serviceUrl = serviceUrl;
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

	// 가입 완료 직후 그 사용자 본인의 카카오톡으로 "나에게 보내기" 웰컴 메시지를 보낸다.
	// 이 동의항목(talk_message)은 선택 동의라 사용자가 동의 안 했을 수 있는데, 그 경우 카카오가
	// 에러를 주더라도 가입 자체는 이미 끝난 뒤라 실패를 조용히 무시해도 된다 (호출부에서 처리).
	public void sendWelcomeMessage(String accessToken, String nickname) {
		String name = (nickname == null || nickname.isBlank()) ? "회원" : nickname;
		Map<String, Object> template = Map.of(
				"object_type", "text",
				"text", "[JK Scaneat] 가입완료\n" + name + "님, JK Scaneat 사업자 가입이 완료되었습니다.\n로그인 후 사업장 정보를 등록하시면 바로 이용하실 수 있어요.",
				"link", Map.of("web_url", serviceUrl, "mobile_web_url", serviceUrl),
				"button_title", "바로가기"
		);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		try {
			body.add("template_object", objectMapper.writeValueAsString(template));
		} catch (Exception e) {
			log.error("[카카오] 웰컴 메시지 템플릿 직렬화 실패", e);
			return;
		}

		try {
			kakaoApiRestClient.post()
					.uri("/v2/api/talk/memo/default/send")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(body)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException ex) {
			// 사용자가 talk_message 동의를 안 했거나 토큰이 만료된 경우가 대부분 — 가입은 이미 끝났으니 로그만 남긴다.
			log.warn("[카카오] 웰컴 메시지 발송 실패 (가입 자체는 정상 처리됨): {}", ex.getResponseBodyAsString());
		}
	}
}
