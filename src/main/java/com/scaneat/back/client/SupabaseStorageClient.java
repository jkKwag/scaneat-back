package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
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
}
