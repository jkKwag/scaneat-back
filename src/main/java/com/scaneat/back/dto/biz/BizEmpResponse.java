package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizEmp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BizEmpResponse(
		String empId,
		String bizRegNo,
		String empNm,
		String positionNm,
		String deptNm,
		String mobileTel,
		String email,
		LocalDate hireDt,
		LocalDate resignDt,
		String useYn,
		String rmrk,
		LocalDateTime regDt
) {
	public static BizEmpResponse from(BizEmp emp) {
		return new BizEmpResponse(
				emp.getEmpId(),
				emp.getBizRegNo(),
				emp.getEmpNm(),
				emp.getPositionNm(),
				emp.getDeptNm(),
				emp.getMobileTel(),
				emp.getEmail(),
				emp.getHireDt(),
				emp.getResignDt(),
				emp.getUseYn(),
				emp.getRmrk(),
				emp.getRegDt()
		);
	}
}
