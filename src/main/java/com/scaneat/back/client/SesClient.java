package com.scaneat.back.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.security.AwsSigV4Signer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SesClient {

	private static final String SERVICE = "ses";
	private static final DateTimeFormatter AMZ_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
	private static final String PATH = "/v2/email/outbound-emails";

	private final RestClient sesRestClient;
	private final ObjectMapper objectMapper;
	private final String accessKey;
	private final String secretKey;
	private final String region;
	private final String fromAddress;
	private final String host;

	public SesClient(RestClient sesRestClient, ObjectMapper objectMapper,
			@Value("${aws.ses.access-key}") String accessKey,
			@Value("${aws.ses.secret-key}") String secretKey,
			@Value("${aws.ses.region}") String region,
			@Value("${aws.ses.from-address}") String fromAddress) {
		this.sesRestClient = sesRestClient;
		this.objectMapper = objectMapper;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;
		this.fromAddress = fromAddress;
		this.host = "email." + region + ".amazonaws.com";
	}

	public void sendVerificationCode(String toEmail, String code) {
		String subject = "[Scaneat] 이메일 인증코드";
		String body = "인증코드: " + code + "\n5분 이내에 입력해주세요.";
		sendEmail(toEmail, subject, body);
	}

	private void sendEmail(String toEmail, String subject, String bodyText) {
		try {
			Map<String, Object> payloadMap = Map.of(
					"FromEmailAddress", fromAddress,
					"Destination", Map.of("ToAddresses", List.of(toEmail)),
					"Content", Map.of("Simple", Map.of(
							"Subject", Map.of("Data", subject, "Charset", "UTF-8"),
							"Body", Map.of("Text", Map.of("Data", bodyText, "Charset", "UTF-8")))));
			String payload = objectMapper.writeValueAsString(payloadMap);
			String amzDate = ZonedDateTime.now(ZoneOffset.UTC).format(AMZ_DATE_FORMAT);
			String authorization = AwsSigV4Signer.authorizationHeader(
					accessKey, secretKey, region, SERVICE, "POST", host, PATH, payload, amzDate);

			sesRestClient.post()
					.uri(PATH)
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-Amz-Date", amzDate)
					.header("Authorization", authorization)
					.body(payload)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.BAD_GATEWAY, "이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
		} catch (Exception ex) {
			throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송 중 오류가 발생했습니다.");
		}
	}
}
