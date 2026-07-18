package com.scaneat.back.dto.reservation;

import com.scaneat.back.entity.UsrRsvn;
import java.time.LocalDateTime;

public record ReservationResponse(
		String rsvnNo,
		String uuid,
		String bizRegNo,
		String guestName,
		String guestPhone,
		LocalDateTime rsvnDt,
		String seatCd,
		Integer partySize,
		String memo,
		String rsvnStatus,
		String rejectRsn,
		LocalDateTime regDt
) {
	public static ReservationResponse from(UsrRsvn rsvn) {
		return new ReservationResponse(
				rsvn.getRsvnNo(),
				rsvn.getUuid(),
				rsvn.getBizRegNo(),
				rsvn.getGuestName(),
				rsvn.getGuestPhone(),
				rsvn.getRsvnDt(),
				rsvn.getSeatCd(),
				rsvn.getPartySize(),
				rsvn.getMemo(),
				rsvn.getRsvnStatus().name(),
				rsvn.getRejectRsn(),
				rsvn.getRegDt()
		);
	}
}
