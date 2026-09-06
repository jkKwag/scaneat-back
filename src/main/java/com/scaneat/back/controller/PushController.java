package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.push.PushSubscribeRequest;
import com.scaneat.back.service.PushRegService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biz/{bizno}")
@RequiredArgsConstructor
public class PushController {

	private final PushRegService pushRegService;

	// 관리자 화면에서 알림을 허용하면 브라우저의 구독 정보를 등록한다 (관리자 인증 필요).
	@PostMapping("/push/subscribe")
	public ApiResponse<Void> subscribe(
			@PathVariable String bizno, @RequestBody PushSubscribeRequest request, HttpServletRequest httpRequest) {
		CurrentAdmin admin = (CurrentAdmin) httpRequest.getAttribute(CurrentAdmin.REQUEST_ATTR);
		pushRegService.subscribe(bizno, request, admin.adminNo());
		return ApiResponse.ok(null);
	}

	// 손님이 매장주문 화면에서 직원호출을 누르면 호출된다 — 로그인하지 않은 손님용 공개 API.
	@PostMapping("/staff-call")
	public ApiResponse<Void> callStaff(@PathVariable String bizno, @RequestParam(required = false) String seatCd) {
		pushRegService.callStaff(bizno, seatCd);
		return ApiResponse.ok(null);
	}
}
