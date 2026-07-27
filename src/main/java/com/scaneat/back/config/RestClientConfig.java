package com.scaneat.back.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient tossRestClient(@Value("${toss.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl).build();
	}

	@Bean
	public RestClient geminiRestClient(@Value("${gemini.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl).build();
	}

	// 사업자등록증 이미지 인식 전용 — 채팅과 분리된 별도 호출이라 넉넉한 제한시간을 둬도 다른 기능에 영향 없다.
	@Bean
	public RestClient geminiVisionRestClient(@Value("${gemini.base-url}") String baseUrl) {
		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
				.withConnectTimeout(Duration.ofSeconds(5))
				.withReadTimeout(Duration.ofSeconds(25));
		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(ClientHttpRequestFactories.get(settings))
				.build();
	}

	@Bean
	public RestClient supabaseRestClient(@Value("${supabase.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl).build();
	}

	@Bean
	public RestClient ntsRestClient(@Value("${nts.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl).build();
	}

	@Bean
	public RestClient sesRestClient(@Value("${aws.ses.region}") String region) {
		return RestClient.builder().baseUrl("https://email." + region + ".amazonaws.com").build();
	}
}
