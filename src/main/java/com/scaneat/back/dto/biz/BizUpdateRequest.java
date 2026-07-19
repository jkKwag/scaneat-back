package com.scaneat.back.dto.biz;

public record BizUpdateRequest(
		String bizNm,
		String telNo,
		String emailAddr,
		String indCd,
		String addr,
		String addrDtl
) {
}
