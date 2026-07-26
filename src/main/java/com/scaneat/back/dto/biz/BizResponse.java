package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.Biz;

public record BizResponse(
		String bizRegNo,
		String bizNm,
		String telNo,
		String mobileTel,
		String emailAddr,
		String indCd,
		String repNm,
		String bizStatus,
		String addr,
		String addrDtl
) {
	public static BizResponse from(Biz biz) {
		return new BizResponse(
				biz.getBizRegNo(),
				biz.getBizNm(),
				biz.getTelNo(),
				biz.getMobileTel(),
				biz.getEmailAddr(),
				biz.getIndCd(),
				biz.getRepNm(),
				biz.getBizStatus(),
				biz.getAddr(),
				biz.getAddrDtl()
		);
	}
}
