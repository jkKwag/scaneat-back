package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.AdminUsrResponse;
import com.scaneat.back.dto.admin.SysMenuResponse;
import com.scaneat.back.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
