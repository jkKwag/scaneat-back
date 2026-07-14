package com.scaneat.back.dto.supporter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SupporterRequest(
		@NotBlank(message = "name은 필수입니다.") String name,
		@NotNull(message = "amount는 필수입니다.") @Positive(message = "amount는 0보다 커야 합니다.") BigDecimal amount,
		String message
) {
}
