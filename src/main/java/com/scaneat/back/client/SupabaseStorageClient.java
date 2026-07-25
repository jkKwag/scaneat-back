package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

// service_role(secret) 키는 서버에만 두고, 프론트는 이 클라이언트를 거쳐 업로드한다.
@Component
public class SupabaseStorageClient {

	private final RestClient supabaseRestClient;
	private final String serviceRoleKey;
	private final String baseUrl;

	public SupabaseStorageClient(
			RestClient supabaseRestClient,
			@Value("${supabase.service-role-key}") String serviceRoleKey,
			@Value("${supabase.base-url}") String baseUrl) {
		this.supabaseRestClient = supabaseRestClient;
		this.serviceRoleKey = serviceRoleKey;
		this.baseUrl = baseUrl;
	}

	public String upload(String bucket, String path, byte[] content, String contentType) {
		try {
			supabaseRestClient.post()
					.uri("/storage/v1/object/{bucket}/{path}", bucket, path)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
					.header("apikey", serviceRoleKey)
					.header("x-upsert", "true")
					.contentType(MediaType.parseMediaType(contentType))
					.body(content)
					.retrieve()
					.toBodilessEntity();
			return baseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"이미지 업로드에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}

	// 비공개(private) 버킷의 파일을 잠시만 볼 수 있는 서명된 URL로 발급한다.
	// 사업자등록증처럼 아무나 보면 안 되는 파일은 이 방식으로만 접근을 허용한다.
	@SuppressWarnings("unchecked")
	public String createSignedUrl(String bucket, String path, int expiresInSeconds) {
		try {
			Map<String, Object> response = supabaseRestClient.post()
					.uri("/storage/v1/object/sign/{bucket}/{path}", bucket, path)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
					.header("apikey", serviceRoleKey)
					.contentType(MediaType.APPLICATION_JSON)
					.body(Map.of("expiresIn", expiresInSeconds))
					.retrieve()
					.body(Map.class);
			String signedUrl = response != null ? (String) response.get("signedURL") : null;
			if (signedUrl == null) {
				throw new BusinessException(HttpStatus.BAD_GATEWAY, "서명된 URL 발급에 실패했습니다.");
			}
			return baseUrl + "/storage/v1" + signedUrl;
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"파일 조회에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}
}
