package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class TossPaymentsClient {

	private final RestClient tossRestClient;
	private final String secretKey;

	public TossPaymentsClient(RestClient tossRestClient, @Value("${toss.secret-key}") String secretKey) {
		this.tossRestClient = tossRestClient;
		this.secretKey = secretKey;
	}

	public Map<String, Object> confirmPayment(String paymentKey, String orderId, BigDecimal amount) {
		String encodedAuth = Base64.getEncoder()
				.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

		try {
			return tossRestClient.post()
					.uri("/v1/payments/confirm")
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
					.body(Map.of(
							"paymentKey", paymentKey,
							"orderId", orderId,
							"amount", amount
					))
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"결제 승인에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}
}
