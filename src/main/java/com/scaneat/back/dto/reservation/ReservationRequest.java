package com.scaneat.back.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		@NotBlank(message = "guestName은 필수입니다.") String guestName,
		String guestTel,
		@NotNull(message = "rsvnDt는 필수입니다.") LocalDateTime rsvnDt,
		String seatCd,
		Integer partySize,
		String memo
) {
}
