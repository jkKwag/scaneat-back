package com.scaneat.back.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		String seatNo,
		@NotEmpty(message = "주문 항목이 최소 1개 필요합니다.") @Valid List<OrderItemRequest> items
) {
}
