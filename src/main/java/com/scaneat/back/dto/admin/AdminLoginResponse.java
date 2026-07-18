package com.scaneat.back.dto.admin;

import com.scaneat.back.entity.AdminUsr;

public record AdminLoginResponse(
		String adminId,
		String adminNm,
		String adminRole,
		String bizRegNo
) {
	public static AdminLoginResponse from(AdminUsr admin) {
		return new AdminLoginResponse(
				admin.getAdminId(),
				admin.getAdminNm(),
				admin.getAdminRole().name(),
				admin.getBizRegNo()
		);
	}
}
