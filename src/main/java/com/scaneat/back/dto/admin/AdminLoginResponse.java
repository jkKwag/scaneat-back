package com.scaneat.back.dto.admin;

import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.BizEmp;

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

	// 직원(tb_biz_emp) 계정으로 로그인한 경우 - adminRole 자리에 "EMPLOYEE"를 넣어
	// 같은 로그인 화면/응답 형태를 그대로 재사용한다 (관리자 계정과 완전히 분리된 별도 테이블)
	public static AdminLoginResponse fromEmployee(BizEmp emp) {
		return new AdminLoginResponse(
				emp.getEmpId(),
				emp.getEmpNm(),
				"EMPLOYEE",
				emp.getBizRegNo()
		);
	}
}
