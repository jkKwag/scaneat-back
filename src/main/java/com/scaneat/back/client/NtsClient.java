package com.scaneat.back.client;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// 국세청 사업자등록 상태조회 오픈API(공공데이터포털) 클라이언트.
// 가입 시점의 자동 확인용이라, 호출 자체가 실패해도 가입을 막지 않고 결과만 null로 둔다
// (실제 소유권 확인은 사업자등록증 이미지를 관리자가 육안으로 대조하는 게 최종 관문).
@Component
public class NtsClient {

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
			if (response == null) return null;
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
			if (data == null || data.isEmpty()) return null;
			String bStt = (String) data.get(0).get("b_stt");
			return (bStt == null || bStt.isBlank()) ? null : bStt;
		} catch (RuntimeException ex) {
			return null;
		}
	}
}
