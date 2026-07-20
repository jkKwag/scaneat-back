package com.scaneat.back.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateRequest(
		@NotBlank(message = "status는 필수입니다.") String status
) {
}
