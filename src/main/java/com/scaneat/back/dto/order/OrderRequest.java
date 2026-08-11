package com.scaneat.back.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		String seatNo,
		String orderTypCd,
		String guestPhone,
		// true면 결제 없이 먼저 접수하는 매장주문(이른바 "탭 열어두기") — 결제는 항상 즉시 이뤄지므로
		// 결제 주문에는 해당 없고, 이 미결제 주문 생성에만 직원 QR 권한을 요구한다.
		Boolean payLater,
		@NotEmpty(message = "주문 항목이 최소 1개 필요합니다.") @Valid List<OrderItemRequest> items
) {
}
