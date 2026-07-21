package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.payment.PaymentCancelRequest;
import com.scaneat.back.dto.payment.PaymentConfirmRequest;
import com.scaneat.back.dto.payment.PaymentResponse;
import com.scaneat.back.service.PaymentService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public ApiResponse<PaymentResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
		return ApiResponse.ok(paymentService.confirmPayment(request));
	}

	@GetMapping("/{paymentKey}")
	public ApiResponse<PaymentResponse> getPayment(@PathVariable String paymentKey) {
		return ApiResponse.ok(paymentService.getPayment(paymentKey));
	}

	@PostMapping("/{paymentKey}/cancel")
	public ApiResponse<PaymentResponse> cancelPayment(
			@PathVariable String paymentKey,
			@Valid @RequestBody PaymentCancelRequest request) {
		return ApiResponse.ok(paymentService.cancelPayment(paymentKey, request));
	}

	@GetMapping
	public ApiResponse<List<PaymentResponse>> getPayments(@RequestParam String uuid) {
		return ApiResponse.ok(paymentService.getPaymentsByUuid(uuid));
	}

	@GetMapping("/biz/{bizRegNo}")
	public ApiResponse<List<PaymentResponse>> getPaymentsByBiz(
			@PathVariable String bizRegNo,
			@RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to) {
		if (from != null && to != null) {
			return ApiResponse.ok(paymentService.getPaymentsByBiz(bizRegNo, from, to));
		}
		return ApiResponse.ok(paymentService.getPaymentsByBiz(bizRegNo));
	}
}
