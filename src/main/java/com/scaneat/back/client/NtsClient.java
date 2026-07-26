package com.scaneat.back.client;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

// 국세청 사업자등록 상태조회 오픈API(공공데이터포털) 클라이언트.
// 가입 시점의 자동 확인용이라, 호출 자체가 실패해도 가입을 막지 않고 결과만 null로 둔다
// (실제 소유권 확인은 사업자등록증 이미지를 관리자가 육안으로 대조하는 게 최종 관문).
@Component
public class NtsClient {

	private static final Logger log = LoggerFactory.getLogger(NtsClient.class);
	// 정상 조회(성공/미확인)와 구분되는 값 — API 서버 자체 장애로 확인을 못 한 경우에 반환한다.
	private static final String API_ERROR_STATUS = "국세청 API 서버 에러로 인증하지 못하였습니다.";

	private final RestClient ntsRestClient;
	private final String serviceKey;

	public NtsClient(RestClient ntsRestClient, @Value("${nts.service-key}") String serviceKey) {
		this.ntsRestClient = ntsRestClient;
		this.serviceKey = serviceKey;
	}

	// 반환값: 국세청에 등록된 상태 문자열(예: "계속사업자", "휴업자", "폐업자") — 조회 실패/미등록이면 null
	@SuppressWarnings("unchecked")
	public String checkStatus(String bizRegNo) {
		try {
			String cleaned = bizRegNo.replaceAll("[^0-9]", "");
			Map<String, Object> response = ntsRestClient.post()
					.uri(uriBuilder -> uriBuilder.path("/status").queryParam("serviceKey", serviceKey).build())
					.body(Map.of("b_no", List.of(cleaned)))
					.retrieve()
					.body(Map.class);
			if (response == null) {
				log.warn("NTS 상태조회 응답이 비어있음: bizRegNo={}", cleaned);
				return null;
			}
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
			if (data == null || data.isEmpty()) {
				log.warn("NTS 상태조회 결과 없음: bizRegNo={}, response={}", cleaned, response);
				return null;
			}
			String bStt = (String) data.get(0).get("b_stt");
			if (bStt == null || bStt.isBlank()) {
				log.warn("NTS 상태조회 b_stt 없음: bizRegNo={}, data={}", cleaned, data.get(0));
				return null;
			}
			return bStt;
		} catch (RestClientResponseException ex) {
			log.error("NTS 상태조회 실패: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
			return API_ERROR_STATUS;
		} catch (RuntimeException ex) {
			log.error("NTS 상태조회 중 오류", ex);
			return API_ERROR_STATUS;
		}
	}
}
