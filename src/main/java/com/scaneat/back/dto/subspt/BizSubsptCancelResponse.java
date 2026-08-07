package com.scaneat.back.dto.subspt;

import java.math.BigDecimal;

// refundAmount가 0이면 환불 대상이 없었다는 뜻(예: 이미 결제 주기가 다 지난 경우) — 이때 refundSucceeded는 의미 없음.
// refundAmount가 0보다 크면 refundSucceeded로 토스 부분취소 성공 여부를, 실패했다면 refundFailReason을 확인한다.
public record BizSubsptCancelResponse(
		BigDecimal refundAmount,
		boolean refundSucceeded,
		String refundFailReason
) {
}
