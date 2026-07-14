package com.scaneat.back.dto.qna;

import jakarta.validation.constraints.NotBlank;

public record QnaAnswerRequest(
		@NotBlank(message = "answer는 필수입니다.") String answer
) {
}
