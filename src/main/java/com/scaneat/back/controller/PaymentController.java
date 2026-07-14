package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.payment.PaymentConfirmRequest;
import com.scaneat.back.dto.payment.PaymentConfirmResponse;
import com.scaneat.back.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public ApiResponse<PaymentConfirmResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
		return ApiResponse.ok(paymentService.confirmPayment(request));
	}
}
