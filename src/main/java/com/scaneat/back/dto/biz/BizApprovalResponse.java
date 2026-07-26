package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.Biz;

public record BizApprovalResponse(
		String bizRegNo,
		String bizNm,
		String repNm,
		String telNo,
		String mobileTel,
		String emailAddr,
		String addr,
		String addrDtl,
		String ntsStatus,
		String approvalStatus,
		String rejectRsn,
		String bizCertUrl
) {
	public static BizApprovalResponse from(Biz biz, String bizCertUrl, String ntsStatusDisplay) {
		return new BizApprovalResponse(
				biz.getBizRegNo(),
				biz.getBizNm(),
				biz.getRepNm(),
				biz.getTelNo(),
				biz.getMobileTel(),
				biz.getEmailAddr(),
				biz.getAddr(),
				biz.getAddrDtl(),
				ntsStatusDisplay,
				biz.getApprovalStatus(),
				biz.getRejectRsn(),
				bizCertUrl
		);
	}
}
