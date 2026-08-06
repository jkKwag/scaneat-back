package com.scaneat.back.dto.subspt;

import com.scaneat.back.entity.BizSubspt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BizSubsptResponse(
		String bizRegNo,
		String planCd,
		String planNm,
		BigDecimal suppliedAmount,
		BigDecimal vat,
		BigDecimal totalAmount,
		boolean hasBillingKey,
		String status,
		LocalDate nextBillingDt,
		String pendingPlanCd,
		String pendingPlanNm,
		LocalDateTime startedDt,
		LocalDateTime canceledDt
) {
	public static BizSubsptResponse from(BizSubspt subspt, String planNm, String pendingPlanNm) {
		return new BizSubsptResponse(
				subspt.getBizRegNo(),
				subspt.getPlanCd(),
				planNm,
				subspt.getSuppliedAmount(),
				subspt.getVat(),
				subspt.getSuppliedAmount().add(subspt.getVat()),
				subspt.getBillingKey() != null,
				subspt.getStatus(),
				subspt.getNextBillingDt(),
				subspt.getPendingPlanCd(),
				pendingPlanNm,
				subspt.getStartedDt(),
				subspt.getCanceledDt()
		);
	}
}
