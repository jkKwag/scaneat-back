package com.scaneat.back.dto.admin;

import com.scaneat.back.entity.AdminUsr;
import java.time.LocalDateTime;

public record AdminUsrResponse(
		String adminId,
		String adminNm,
		String adminRole,
		String bizRegNo,
		String mobileTel,
		String tel,
		String useYn,
		LocalDateTime regDt
) {
	public static AdminUsrResponse from(AdminUsr admin) {
		return new AdminUsrResponse(
				admin.getAdminId(),
				admin.getAdminNm(),
				admin.getAdminRole().name(),
				admin.getBizRegNo(),
				admin.getMobileTel(),
				admin.getTel(),
				admin.getUseYn(),
				admin.getRegDt()
		);
	}
}
