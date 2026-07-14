package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.qna.QnaAnswerRequest;
import com.scaneat.back.dto.qna.QnaRequest;
import com.scaneat.back.dto.qna.QnaResponse;
import com.scaneat.back.service.QnaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

	private final QnaService qnaService;

	@GetMapping
	public ApiResponse<List<QnaResponse>> getQnas() {
		return ApiResponse.ok(qnaService.getQnas());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<QnaResponse> createQna(@Valid @RequestBody QnaRequest request) {
		return ApiResponse.ok(qnaService.createQna(request));
	}

	@PutMapping("/{id}/answer")
	public ApiResponse<QnaResponse> answerQna(
			@PathVariable Long id, @Valid @RequestBody QnaAnswerRequest request) {
		return ApiResponse.ok(qnaService.answerQna(id, request));
	}
}
