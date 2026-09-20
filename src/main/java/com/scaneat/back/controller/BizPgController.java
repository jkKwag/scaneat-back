package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.pg.BizPgRegisterRequest;
import com.scaneat.back.dto.pg.BizPgResponse;
import com.scaneat.back.service.BizPgService;
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

// 사업장별 PG(결제대행사) 연동 관리. AdminAuthInterceptor가 bizno를 세션의 bizRegNo와
// 대조해서 SUPER가 아닌 한 본인 매장 것만 접근 가능하도록 이미 막아준다.
@RestController
@RequestMapping("/api/biz")
@RequiredArgsConstructor
public class BizPgController {

	private final BizPgService bizPgService;

	@GetMapping("/{bizno}/pg")
	public ApiResponse<List<BizPgResponse>> getConnections(@PathVariable String bizno) {
		return ApiResponse.ok(bizPgService.getConnections(bizno));
	}

	@PostMapping("/{bizno}/pg")
	public ApiResponse<BizPgResponse> register(
			@PathVariable String bizno, @Valid @RequestBody BizPgRegisterRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(bizPgService.register(bizno, request, currentAdmin(httpRequest).adminNo()));
	}

	@PutMapping("/{bizno}/pg/{provider}/deactivate")
	public ApiResponse<BizPgResponse> deactivate(
			@PathVariable String bizno, @PathVariable String provider, HttpServletRequest httpRequest) {
		return ApiResponse.ok(bizPgService.setStatus(bizno, provider, "INACTIVE", currentAdmin(httpRequest).adminNo()));
	}

	@PutMapping("/{bizno}/pg/{provider}/activate")
	public ApiResponse<BizPgResponse> activate(
			@PathVariable String bizno, @PathVariable String provider, HttpServletRequest httpRequest) {
		return ApiResponse.ok(bizPgService.setStatus(bizno, provider, "ACTIVE", currentAdmin(httpRequest).adminNo()));
	}

	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
