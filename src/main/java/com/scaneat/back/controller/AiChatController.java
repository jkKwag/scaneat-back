package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.ai.AiChatRequest;
import com.scaneat.back.dto.ai.AiChatResponse;
import com.scaneat.back.service.AiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

	private final AiChatService aiChatService;

	@PostMapping("/chat")
	public ApiResponse<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
		return ApiResponse.ok(aiChatService.chat(request));
	}
}
