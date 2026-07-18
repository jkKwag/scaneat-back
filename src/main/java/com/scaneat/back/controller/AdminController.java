package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
