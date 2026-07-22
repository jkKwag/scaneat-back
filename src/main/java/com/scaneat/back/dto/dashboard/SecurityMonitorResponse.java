package com.scaneat.back.dto.dashboard;

public record SecurityMonitorResponse(
		long totalAdminCount,
		long superAdminCount,
		long bizAdminCount,
		long inactiveAdminCount,
		long totalEmployeeCount,
		long activeEmployeeCount,
		long inactiveEmployeeCount
) {
}
