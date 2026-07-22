package com.scaneat.back.dto.dashboard;

import java.util.List;

public record DashboardOverviewResponse(
		long totalBizCount,
		List<DashboardCodeCount> statusBreakdown,
		List<DashboardCodeCount> industryBreakdown
) {
}
