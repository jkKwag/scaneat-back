package com.scaneat.back.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentCancelRequest(
		@NotBlank(message = "cancelReason은 필수입니다.") String cancelReason
) {
}
