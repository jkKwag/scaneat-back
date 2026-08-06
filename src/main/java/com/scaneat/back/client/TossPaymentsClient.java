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
		try {
			return tossRestClient.post()
					.uri("/v1/payments/confirm")
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth())
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

	public Map<String, Object> cancelPayment(String paymentKey, String cancelReason) {
		try {
			return tossRestClient.post()
					.uri("/v1/payments/{paymentKey}/cancel", paymentKey)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth())
					.body(Map.of("cancelReason", cancelReason))
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"결제 취소에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}

	// 구독 카드 등록(빌링 인증) 위젯 완료 후 받은 authKey를 실제 자동결제용 billingKey로 교환한다.
	public Map<String, Object> issueBillingKey(String authKey, String customerKey) {
		try {
			return tossRestClient.post()
					.uri("/v1/billing/authorizations/issue")
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth())
					.body(Map.of(
							"authKey", authKey,
							"customerKey", customerKey
					))
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"빌링키 발급에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}

	// 등록된 billingKey로 카드 재인증 없이 서버 간 호출만으로 청구한다 (정기결제).
	public Map<String, Object> chargeBilling(
			String billingKey, String customerKey, String orderId, String orderName, BigDecimal amount) {
		try {
			return tossRestClient.post()
					.uri("/v1/billing/{billingKey}", billingKey)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth())
					.body(Map.of(
							"customerKey", customerKey,
							"amount", amount,
							"orderId", orderId,
							"orderName", orderName
					))
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"정기결제 청구에 실패했습니다: " + ex.getResponseBodyAsString());
		}
	}

	private String encodedAuth() {
		return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}
}
