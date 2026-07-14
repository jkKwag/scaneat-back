package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.scanlog.ScanLogRequest;
import com.scaneat.back.dto.scanlog.ScanLogResponse;
import com.scaneat.back.service.ScanLogService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan-log")
@RequiredArgsConstructor
public class ScanLogController {

	private final ScanLogService scanLogService;

	@GetMapping
	public ApiResponse<List<ScanLogResponse>> getScanLogs(@RequestParam String uuid) {
		return ApiResponse.ok(scanLogService.getScanLogs(uuid));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ScanLogResponse> createScanLog(@Valid @RequestBody ScanLogRequest request) {
		return ApiResponse.ok(scanLogService.createScanLog(request));
	}
}
