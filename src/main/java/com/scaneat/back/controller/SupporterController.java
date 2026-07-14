package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.supporter.SupporterRequest;
import com.scaneat.back.dto.supporter.SupporterResponse;
import com.scaneat.back.service.SupporterService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supporters")
@RequiredArgsConstructor
public class SupporterController {

	private final SupporterService supporterService;

	@GetMapping
	public ApiResponse<List<SupporterResponse>> getSupporters() {
		return ApiResponse.ok(supporterService.getSupporters());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<SupporterResponse> createSupporter(@Valid @RequestBody SupporterRequest request) {
		return ApiResponse.ok(supporterService.createSupporter(request));
	}
}
