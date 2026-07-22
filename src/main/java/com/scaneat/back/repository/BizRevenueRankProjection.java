package com.scaneat.back.repository;

import java.math.BigDecimal;

public interface BizRevenueRankProjection {
	String getBizRegNo();

	String getBizNm();

	BigDecimal getTotalAmount();

	Long getPaymentCount();
}
