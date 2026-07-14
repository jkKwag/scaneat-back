package com.scaneat.back.dto.ai;

import java.util.List;
import java.util.Map;

public record AiChatResponse(
		String reply,
		String action,
		Map<String, Object> actionResult,
		List<CartItemDto> cart
) {
}
