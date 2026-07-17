package com.scaneat.back.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record OrderItemRequest(
		@NotBlank(message = "menuCd는 필수입니다.") String menuCd,
		@NotBlank(message = "menuNm은 필수입니다.") String menuNm,
		@NotNull(message = "price는 필수입니다.") BigDecimal price,
		@NotNull(message = "qty는 필수입니다.") @Positive(message = "qty는 0보다 커야 합니다.") Integer qty,
		List<OrderItemOptionRequest> options
) {
}
