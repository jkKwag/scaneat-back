package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.consent.ConsentCheckResponse;
import com.scaneat.back.dto.consent.ConsentRequest;
import com.scaneat.back.dto.consent.ConsentResponse;
import com.scaneat.back.service.ConsentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consent")
@RequiredArgsConstructor
public class ConsentController {

	private final ConsentService consentService;

	@GetMapping
	public ApiResponse<List<ConsentResponse>> getConsents(@RequestParam String uuid) {
		return ApiResponse.ok(consentService.getConsents(uuid));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ConsentResponse> createConsent(@Valid @RequestBody ConsentRequest request) {
		return ApiResponse.ok(consentService.createConsent(request));
	}

	@PostMapping("/reservation")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ConsentResponse> createReservationConsent(@Valid @RequestBody ConsentRequest request) {
		return ApiResponse.ok(consentService.createConsent(request));
	}

	@GetMapping("/check")
	public ApiResponse<ConsentCheckResponse> checkConsent(
			@RequestParam String uuid, @RequestParam String bizRegNo) {
		return ApiResponse.ok(consentService.checkConsent(uuid, bizRegNo));
	}
}
