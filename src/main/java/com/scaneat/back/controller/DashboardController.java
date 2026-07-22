package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.dashboard.BizRevenueRankResponse;
import com.scaneat.back.dto.dashboard.DashboardOverviewResponse;
import com.scaneat.back.dto.dashboard.SecurityMonitorResponse;
import com.scaneat.back.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/overview")
	public ApiResponse<DashboardOverviewResponse> getOverview() {
		return ApiResponse.ok(dashboardService.getOverview());
	}

	@GetMapping("/revenue-ranking")
	public ApiResponse<List<BizRevenueRankResponse>> getRevenueRanking(@RequestParam(defaultValue = "5") int limit) {
		return ApiResponse.ok(dashboardService.getRevenueRanking(limit));
	}

	@GetMapping("/security")
	public ApiResponse<SecurityMonitorResponse> getSecurity() {
		return ApiResponse.ok(dashboardService.getSecurityMonitor());
	}
}
