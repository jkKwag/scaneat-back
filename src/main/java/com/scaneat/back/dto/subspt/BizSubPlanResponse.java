package com.scaneat.back.dto.subspt;

import com.scaneat.back.entity.BizSubPlan;
import java.math.BigDecimal;

public record BizSubPlanResponse(
		String planCd,
		String planNm,
		BigDecimal suppliedAmount,
		BigDecimal vat,
		BigDecimal totalAmount,
		boolean dineIn,
		boolean takeout,
		boolean delivery
) {
	public static BizSubPlanResponse from(BizSubPlan plan) {
		return new BizSubPlanResponse(
				plan.getPlanCd(),
				plan.getPlanNm(),
				plan.getSuppliedAmount(),
				plan.getVat(),
				plan.getSuppliedAmount().add(plan.getVat()),
				"Y".equals(plan.getUseDineInYn()),
				"Y".equals(plan.getUseTakeoutYn()),
				"Y".equals(plan.getUseDeliveryYn())
		);
	}
}
