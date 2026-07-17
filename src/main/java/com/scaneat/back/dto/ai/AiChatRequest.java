package com.scaneat.back.dto.ai;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record AiChatRequest(
		String uuid,
		String bizRegNo,
		@NotBlank(message = "message는 필수입니다.") String message,
		String rsvnNo,
		List<CartItemDto> cart
) {
}
