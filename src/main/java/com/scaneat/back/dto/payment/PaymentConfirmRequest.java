package com.scaneat.back.dto.payment;

import com.scaneat.back.dto.order.OrderRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PaymentConfirmRequest(
		@NotBlank(message = "paymentKey는 필수입니다.") String paymentKey,
		@NotBlank(message = "orderId는 필수입니다.") String orderId,
		@NotNull(message = "amount는 필수입니다.") BigDecimal amount,
		// 이미 만들어둔 미결제 주문(주문만 하기)을 결제할 때 사용 — 새로 만들 주문만 있다면 비워도 된다.
		List<String> orderNos,
		// 결제 승인에 성공했을 때만 서버에서 새로 생성할 주문(장바구니) — 결제 전에는 주문이 존재하지 않는다.
		@Valid OrderRequest newOrder
) {
}
