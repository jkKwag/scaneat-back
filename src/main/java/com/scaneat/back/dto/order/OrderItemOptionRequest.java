package com.scaneat.back.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemOptionRequest(
		@NotBlank(message = "optCd는 필수입니다.") String optCd,
		@NotBlank(message = "optNm은 필수입니다.") String optNm,
		@NotNull(message = "addPrice는 필수입니다.") BigDecimal addPrice
) {
}
