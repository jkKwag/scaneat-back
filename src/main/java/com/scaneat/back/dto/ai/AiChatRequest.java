package com.scaneat.back.dto.ai;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record AiChatRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		@NotBlank(message = "message는 필수입니다.") String message,
		String rsvnNo,
		List<CartItemDto> cart
) {
}
