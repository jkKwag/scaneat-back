package com.scaneat.back.dto.reservation;

import java.time.LocalDateTime;

public record ReservationUpdateRequest(
		String guestName,
		String guestTel,
		LocalDateTime rsvnDt,
		String seatCd,
		Integer partySize,
		String memo,
		String status
) {
}
