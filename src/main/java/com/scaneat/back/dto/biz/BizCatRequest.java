package com.scaneat.back.dto.biz;

public record BizCatRequest(
		String catCd,
		String bizCatNm,
		Integer sortOrd,
		String useYn,
		String rmrk
) {
}
