package com.scaneat.back.dto.biz;

public record BizSeatRequest(
		String seatNm,
		Integer capacity,
		String seatDesc,
		String imgUrl,
		Integer sortOrd,
		String useYn
) {
}
