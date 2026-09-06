package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.PasskeyDeviceResponse;
import com.scaneat.back.dto.admin.PasskeyLoginOptionsRequest;
import com.scaneat.back.dto.admin.PasskeyLoginOptionsResponse;
import com.scaneat.back.dto.admin.PasskeyLoginRequest;
import com.scaneat.back.dto.admin.PasskeyRegisterOptionsResponse;
import com.scaneat.back.dto.admin.PasskeyRegisterRequest;
import com.scaneat.back.service.PasskeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/passkey")
@RequiredArgsConstructor
public class PasskeyController {

	private final PasskeyService passkeyService;

	// ── 등록 (로그인된 상태에서만 호출 — WebConfig에서 /api/admin/** 기본 인증 필요) ──

	@PostMapping("/register-options")
	public ApiResponse<PasskeyRegisterOptionsResponse> registerOptions(HttpServletRequest httpRequest) {
		return ApiResponse.ok(passkeyService.registerOptions(currentAdmin(httpRequest)));
	}

	@PostMapping("/register")
	public ApiResponse<PasskeyDeviceResponse> register(
			@Valid @RequestBody PasskeyRegisterRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(passkeyService.register(currentAdmin(httpRequest), request));
	}

	@GetMapping("/devices")
	public ApiResponse<List<PasskeyDeviceResponse>> listDevices(HttpServletRequest httpRequest) {
		return ApiResponse.ok(passkeyService.listDevices(currentAdmin(httpRequest)));
	}

	@DeleteMapping("/devices/{credId}")
	public ApiResponse<Void> deleteDevice(@PathVariable String credId, HttpServletRequest httpRequest) {
		passkeyService.deleteDevice(currentAdmin(httpRequest), credId);
		return ApiResponse.ok(null);
	}

	// ── 로그인 (로그인 전이라 인증 없음 — WebConfig excludePathPatterns 등록 필요) ──

	@PostMapping("/login-options")
	public ApiResponse<PasskeyLoginOptionsResponse> loginOptions(@Valid @RequestBody PasskeyLoginOptionsRequest request) {
		return ApiResponse.ok(passkeyService.loginOptions(request));
	}

	@PostMapping("/login")
	public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody PasskeyLoginRequest request) {
		return ApiResponse.ok(passkeyService.login(request));
	}

	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
