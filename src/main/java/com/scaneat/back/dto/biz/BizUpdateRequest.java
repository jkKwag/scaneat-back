package com.scaneat.back.dto.biz;

public record BizUpdateRequest(
		String bizNm,
		String repNm,
		String telNo,
		String mobileTel,
		String emailAddr,
		String indCd,
		String addr,
		String addrDtl
) {
}
