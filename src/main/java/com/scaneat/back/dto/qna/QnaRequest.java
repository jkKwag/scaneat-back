package com.scaneat.back.dto.qna;

import jakarta.validation.constraints.NotBlank;

public record QnaRequest(
		@NotBlank(message = "question은 필수입니다.") String question,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		Boolean isPublic
) {
}
