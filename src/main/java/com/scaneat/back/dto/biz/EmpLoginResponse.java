package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizEmp;

public record EmpLoginResponse(
		String empId,
		String empNm,
		String bizRegNo,
		String positionNm,
		String deptNm
) {
	public static EmpLoginResponse from(BizEmp emp) {
		return new EmpLoginResponse(
				emp.getEmpId(),
				emp.getEmpNm(),
				emp.getBizRegNo(),
				emp.getPositionNm(),
				emp.getDeptNm()
		);
	}
}
