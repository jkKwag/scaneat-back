package com.scaneat.back.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PaymentConfirmRequest(
		@NotBlank(message = "paymentKey는 필수입니다.") String paymentKey,
		@NotBlank(message = "orderId는 필수입니다.") String orderId,
		@NotNull(message = "amount는 필수입니다.") BigDecimal amount,
		@NotEmpty(message = "orderNos는 최소 1개 필요합니다.") List<String> orderNos
) {
}
