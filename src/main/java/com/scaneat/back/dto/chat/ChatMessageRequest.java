package com.scaneat.back.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
		@NotBlank(message = "sender는 필수입니다.") String sender,
		@NotBlank(message = "message는 필수입니다.") String message
) {
}
