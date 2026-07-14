package com.scaneat.back.config;

import org.springframework.beans.factory.annotation.Value;
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
}
