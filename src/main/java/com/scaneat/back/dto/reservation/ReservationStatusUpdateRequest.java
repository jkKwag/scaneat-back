package com.scaneat.back.dto.reservation;

import jakarta.validation.constraints.NotBlank;

public record ReservationStatusUpdateRequest(
		@NotBlank(message = "status는 필수입니다.") String status
) {
}
