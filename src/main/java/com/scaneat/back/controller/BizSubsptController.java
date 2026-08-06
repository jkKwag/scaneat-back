package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.subspt.BizSubPlanResponse;
import com.scaneat.back.dto.subspt.BizSubsptPaymentResponse;
import com.scaneat.back.dto.subspt.BizSubsptResponse;
import com.scaneat.back.dto.subspt.BizSubsptStartRequest;
import com.scaneat.back.service.BizSubsptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biz")
@RequiredArgsConstructor
public class BizSubsptController {

	private final BizSubsptService bizSubsptService;

	// 요금제 목록 조회는 가입 전에도 볼 수 있어야 해서 공개 API로 둔다 (AdminAuthInterceptor에서 별도 처리 불필요).
	@GetMapping("/sub-plans")
	public ApiResponse<List<BizSubPlanResponse>> getPlans() {
		return ApiResponse.ok(bizSubsptService.getPlans());
	}

	@GetMapping("/{bizno}/subscription")
	public ApiResponse<BizSubsptResponse> getSubscription(@PathVariable String bizno) {
		return ApiResponse.ok(bizSubsptService.getSubscription(bizno));
	}

	@PostMapping("/{bizno}/subscription")
	public ApiResponse<BizSubsptResponse> startSubscription(
			@PathVariable String bizno, @Valid @RequestBody BizSubsptStartRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(bizSubsptService.startSubscription(bizno, request, currentAdmin(httpRequest).adminId()));
	}

	@PutMapping("/{bizno}/subscription/cancel")
	public ApiResponse<Void> cancelSubscription(@PathVariable String bizno, HttpServletRequest httpRequest) {
		bizSubsptService.cancelSubscription(bizno, currentAdmin(httpRequest).adminId());
		return ApiResponse.ok(null);
	}

	@GetMapping("/{bizno}/subscription/payments")
	public ApiResponse<List<BizSubsptPaymentResponse>> getPayments(@PathVariable String bizno) {
		return ApiResponse.ok(bizSubsptService.getPayments(bizno));
	}

	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
