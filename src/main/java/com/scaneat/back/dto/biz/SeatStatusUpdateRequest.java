package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;

public record SeatStatusUpdateRequest(
		@NotBlank(message = "status는 필수입니다.") String status // SEATED | EMPTY
) {
}
