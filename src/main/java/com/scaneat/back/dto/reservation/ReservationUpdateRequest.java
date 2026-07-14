package com.scaneat.back.dto.reservation;

import java.time.LocalDateTime;

public record ReservationUpdateRequest(
		String guestName,
		String guestTel,
		LocalDateTime rsvnDt,
		String seatNo,
		Integer partySize,
		String memo,
		String status
) {
}
