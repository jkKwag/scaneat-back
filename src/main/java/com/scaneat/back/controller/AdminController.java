package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.AdminUsrResponse;
import com.scaneat.back.dto.admin.SysMenuResponse;
import com.scaneat.back.dto.admin.TotpConfirmRequest;
import com.scaneat.back.dto.admin.TotpSetupResponse;
import com.scaneat.back.dto.common.PasswordChangeRequest;
import com.scaneat.back.dto.common.PasswordVerifyRequest;
import com.scaneat.back.dto.common.PasswordVerifyResponse;
import com.scaneat.back.service.AdminService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PostMapping("/login")
	public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
		return ApiResponse.ok(adminService.login(request));
	}

	@GetMapping("/menu")
	public ApiResponse<List<SysMenuResponse>> getMenu(@RequestParam String role) {
		return ApiResponse.ok(adminService.getMenuTree(role));
	}

	@GetMapping("/users")
	public ApiResponse<List<AdminUsrResponse>> getUsers(@RequestParam String bizRegNo) {
		return ApiResponse.ok(adminService.getUsersByBiz(bizRegNo));
	}

	@PutMapping("/users/{adminId}/password")
	public ApiResponse<Void> changePassword(
			@PathVariable String adminId, @Valid @RequestBody PasswordChangeRequest request, HttpServletRequest httpRequest) {
		adminService.changePassword(adminId, request, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	@PutMapping("/employees/{empId}/password")
	public ApiResponse<Void> changeEmployeePassword(
			@PathVariable String empId, @Valid @RequestBody PasswordChangeRequest request, HttpServletRequest httpRequest) {
		adminService.changeEmployeePassword(empId, request, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	@PostMapping("/users/{adminId}/verify-password")
	public ApiResponse<PasswordVerifyResponse> verifyPassword(
			@PathVariable String adminId, @Valid @RequestBody PasswordVerifyRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(adminService.verifyPassword(adminId, request, currentAdmin(httpRequest)));
	}

	@PostMapping("/employees/{empId}/verify-password")
	public ApiResponse<PasswordVerifyResponse> verifyEmployeePassword(
			@PathVariable String empId, @Valid @RequestBody PasswordVerifyRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(adminService.verifyEmployeePassword(empId, request, currentAdmin(httpRequest)));
	}

	// 새 TOTP 비밀키 발급 (아직 저장 안 함) — SUPER 계정만 가능.
	@PostMapping("/totp/setup")
	public ApiResponse<TotpSetupResponse> setupTotp(HttpServletRequest httpRequest) {
		return ApiResponse.ok(adminService.setupTotp(currentAdmin(httpRequest)));
	}

	// 등록 화면에서 입력한 코드가 실제로 그 비밀키로 맞게 생성됐는지 확인한 뒤에만 저장한다.
	@PostMapping("/totp/confirm")
	public ApiResponse<Void> confirmTotp(@Valid @RequestBody TotpConfirmRequest request, HttpServletRequest httpRequest) {
		adminService.confirmTotp(currentAdmin(httpRequest), request);
		return ApiResponse.ok(null);
	}

	// AdminAuthInterceptor가 세션 토큰을 검증하며 요청 속성에 담아둔 인증된 관리자 정보를 꺼낸다.
	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
