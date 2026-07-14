package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.chat.ChatMessageRequest;
import com.scaneat.back.dto.chat.ChatMessageResponse;
import com.scaneat.back.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@GetMapping("/{rsvnNo}/messages")
	public ApiResponse<List<ChatMessageResponse>> getMessages(@PathVariable String rsvnNo) {
		return ApiResponse.ok(chatService.getMessages(rsvnNo));
	}

	@PostMapping("/{rsvnNo}/messages")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ChatMessageResponse> createMessage(
			@PathVariable String rsvnNo, @Valid @RequestBody ChatMessageRequest request) {
		return ApiResponse.ok(chatService.createMessage(rsvnNo, request));
	}
}
