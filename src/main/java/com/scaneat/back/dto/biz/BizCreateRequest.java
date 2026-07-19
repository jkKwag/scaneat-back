package com.scaneat.back.dto.biz;

public record BizCreateRequest(
		String bizRegNo,
		String bizNm,
		String telNo,
		String indCd,
		String addr,
		String addrDtl
) {
}
