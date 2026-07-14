package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.Biz;

public record BizResponse(
		String bizRegNo,
		String bizNm,
		String telNo,
		String indCd,
		String addr,
		String addrDtl
) {
	public static BizResponse from(Biz biz) {
		return new BizResponse(
				biz.getBizRegNo(),
				biz.getBizNm(),
				biz.getTelNo(),
				biz.getIndCd(),
				biz.getAddr(),
				biz.getAddrDtl()
		);
	}
}
