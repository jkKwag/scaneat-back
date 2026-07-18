package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizSeat;

public record BizSeatResponse(
		String seatCd,
		String seatNm,
		Integer capacity,
		String seatDesc,
		String imgUrl
) {
	public static BizSeatResponse from(BizSeat seat) {
		return new BizSeatResponse(
				seat.getId().getSeatCd(),
				seat.getSeatNm(),
				seat.getCapacity(),
				seat.getSeatDesc(),
				seat.getImgUrl()
		);
	}
}
