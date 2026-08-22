package com.scaneat.back.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class R2Config {

	@Bean
	public S3Client r2S3Client(
			@Value("${r2.access-key-id}") String accessKeyId,
			@Value("${r2.secret-access-key}") String secretAccessKey,
			@Value("${r2.endpoint-url}") String endpointUrl) {
		return S3Client.builder()
				.endpointOverride(URI.create(endpointUrl))
				// R2에는 실제 AWS 리전이 없다 — SDK가 요구해서 채우는 자리값으로, Cloudflare가 공식 권장하는 값이다.
				.region(Region.of("auto"))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
				// R2는 버킷명.endpoint 형태의 virtual-hosted style을 지원하지 않고 path-style만 지원한다.
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
				.build();
	}
}
