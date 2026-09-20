package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
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

	// 구독 해지 시 미사용 기간만큼 일부만 환불할 때 쓴다 — cancelAmount를 안 주면 전액취소로 처리되므로 반드시 넣어야 한다.
	public Map<String, Object> cancelPaymentPartial(String paymentKey, String cancelReason, BigDecimal cancelAmount) {
		try {
			return tossRestClient.post()
					.uri("/v1/payments/{paymentKey}/cancel", paymentKey)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth())
					.body(Map.of("cancelReason", cancelReason, "cancelAmount", cancelAmount))
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.getStatusCode().value()),
					"환불 처리에 실패했습니다: " + ex.getResponseBodyAsString());
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

	// 업체가 입력한 시크릿키가 실제로 유효한지 확인할 때 쓴다. 존재할 리 없는 임의의 paymentKey로
	// 결제조회를 호출해서, 인증은 통과하되 "그런 결제건이 없다"(404)는 응답이 오면 키가 유효한 것으로 본다.
	// 키 자체가 틀렸다면 401(인증실패)이 온다. 실제 결제/청구를 발생시키지 않고 키만 검증하는 방법이라
	// 토스에 별도 "키 검증" 전용 API가 없는 상황에서 가장 부작용이 적다.
	public boolean verifySecretKey(String candidateSecretKey) {
		String fakePaymentKey = "verify-" + UUID.randomUUID();
		try {
			tossRestClient.get()
					.uri("/v1/payments/{paymentKey}", fakePaymentKey)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth(candidateSecretKey))
					.retrieve()
					.toBodilessEntity();
			return true;
		} catch (RestClientResponseException ex) {
			return ex.getStatusCode().value() == 404;
		}
	}

	private String encodedAuth() {
		return encodedAuth(secretKey);
	}

	private String encodedAuth(String key) {
		return Base64.getEncoder().encodeToString((key + ":").getBytes(StandardCharsets.UTF_8));
	}
}
