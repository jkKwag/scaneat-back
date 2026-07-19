package com.scaneat.back.dto.biz;

public record BizCreateRequest(
		String bizRegNo,
		String bizNm,
		String repNm,
		String telNo,
		String emailAddr,
		String indCd,
		String addr,
		String addrDtl
) {
}
