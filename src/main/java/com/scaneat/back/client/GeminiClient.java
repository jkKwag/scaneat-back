package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GeminiClient {

	private final RestClient geminiRestClient;
	private final String apiKey;
	private final String model;

	private static final List<Map<String, Object>> FUNCTION_DECLARATIONS = List.of(
			Map.of(
					"name", "recommend_item",
					"description", "메뉴 카테고리나 취향에 맞는 메뉴를 추천한다.",
					"parameters", Map.of(
							"type", "OBJECT",
							"properties", Map.of(
									"category", Map.of("type", "STRING", "description", "추천받고 싶은 메뉴 카테고리"),
									"keyword", Map.of("type", "STRING", "description", "맛, 재료 등 추천 키워드")
							)
					)
			),
			Map.of(
					"name", "remove_item",
					"description", "장바구니에서 특정 메뉴를 제거한다.",
					"parameters", Map.of(
							"type", "OBJECT",
							"properties", Map.of(
									"menuNm", Map.of("type", "STRING", "description", "제거할 메뉴명")
							),
							"required", List.of("menuNm")
					)
			),
			Map.of(
					"name", "clear_cart",
					"description", "장바구니를 비운다.",
					"parameters", Map.of("type", "OBJECT", "properties", Map.of())
			),
			Map.of(
					"name", "request_checkout",
					"description", "장바구니 결제를 요청한다.",
					"parameters", Map.of("type", "OBJECT", "properties", Map.of())
			),
			Map.of(
					"name", "request_reservation",
					"description", "신규 예약을 요청한다.",
					"parameters", Map.of(
							"type", "OBJECT",
							"properties", Map.of(
									"guestName", Map.of("type", "STRING", "description", "예약자 이름"),
									"guestTel", Map.of("type", "STRING", "description", "예약자 연락처"),
									"rsvnDt", Map.of("type", "STRING", "description", "예약 일시 (ISO-8601, yyyy-MM-ddTHH:mm:ss)"),
									"partySize", Map.of("type", "INTEGER", "description", "인원 수"),
									"seatNo", Map.of("type", "STRING", "description", "희망 좌석 번호"),
									"memo", Map.of("type", "STRING", "description", "요청 사항")
							),
							"required", List.of("guestName", "rsvnDt")
					)
			),
			Map.of(
					"name", "modify_reservation",
					"description", "기존 예약 내용을 수정한다.",
					"parameters", Map.of(
							"type", "OBJECT",
							"properties", Map.of(
									"rsvnNo", Map.of("type", "STRING", "description", "수정할 예약 번호"),
									"guestName", Map.of("type", "STRING", "description", "예약자 이름"),
									"guestTel", Map.of("type", "STRING", "description", "예약자 연락처"),
									"rsvnDt", Map.of("type", "STRING", "description", "예약 일시 (ISO-8601)"),
									"partySize", Map.of("type", "INTEGER", "description", "인원 수"),
									"seatNo", Map.of("type", "STRING", "description", "희망 좌석 번호"),
									"memo", Map.of("type", "STRING", "description", "요청 사항"),
									"status", Map.of("type", "STRING", "description", "예약 상태 (PENDING, CONFIRMED, CANCELLED, COMPLETED)")
							),
							"required", List.of("rsvnNo")
					)
			)
	);

	public GeminiClient(RestClient geminiRestClient,
			@Value("${gemini.api-key}") String apiKey,
			@Value("${gemini.model}") String model) {
		this.geminiRestClient = geminiRestClient;
		this.apiKey = apiKey;
		this.model = model;
	}

	@SuppressWarnings("unchecked")
	public GeminiResult generateContent(String systemInstruction, String userMessage) {
		Map<String, Object> requestBody = Map.of(
				"systemInstruction", Map.of(
						"parts", List.of(Map.of("text", systemInstruction))
				),
				"contents", List.of(
						Map.of("role", "user", "parts", List.of(Map.of("text", userMessage)))
				),
				"tools", List.of(Map.of("functionDeclarations", FUNCTION_DECLARATIONS))
		);

		Map<String, Object> response;
		try {
			response = geminiRestClient.post()
					.uri("/models/{model}:generateContent?key={apiKey}", model, apiKey)
					.body(requestBody)
					.retrieve()
					.body(Map.class);
		} catch (RestClientResponseException ex) {
			throw new BusinessException(HttpStatus.BAD_GATEWAY,
					"AI 응답 생성에 실패했습니다: " + ex.getResponseBodyAsString());
		}

		if (response == null) {
			throw new BusinessException(HttpStatus.BAD_GATEWAY, "AI 응답이 비어있습니다.");
		}

		List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
		if (candidates == null || candidates.isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_GATEWAY, "AI 응답 후보가 없습니다.");
		}

		Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
		List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

		String text = null;
		GeminiFunctionCall functionCall = null;
		for (Map<String, Object> part : parts) {
			if (part.get("text") != null) {
				text = (String) part.get("text");
			} else if (part.get("functionCall") != null) {
				Map<String, Object> fc = (Map<String, Object>) part.get("functionCall");
				functionCall = new GeminiFunctionCall((String) fc.get("name"), (Map<String, Object>) fc.get("args"));
			}
		}

		return new GeminiResult(text, functionCall);
	}
}
