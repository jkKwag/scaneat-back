package com.scaneat.back.dto.reservation;

import jakarta.validation.constraints.NotBlank;

public record ReservationStatusUpdateRequest(
		@NotBlank(message = "rsvnStatus는 필수입니다.") String rsvnStatus,
		String rejectRsn
) {
}
