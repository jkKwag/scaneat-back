package com.scaneat.back.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentConfirmRequest(
		@NotBlank(message = "paymentKey는 필수입니다.") String paymentKey,
		@NotBlank(message = "orderId는 필수입니다.") String orderId,
		@NotNull(message = "amount는 필수입니다.") BigDecimal amount
) {
}
