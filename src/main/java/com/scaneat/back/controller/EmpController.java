package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.biz.EmpLoginRequest;
import com.scaneat.back.dto.biz.EmpLoginResponse;
import com.scaneat.back.service.BizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emp")
@RequiredArgsConstructor
public class EmpController {

	private final BizService bizService;

	@PostMapping("/login")
	public ApiResponse<EmpLoginResponse> login(@Valid @RequestBody EmpLoginRequest request) {
		return ApiResponse.ok(bizService.employeeLogin(request));
	}
}
