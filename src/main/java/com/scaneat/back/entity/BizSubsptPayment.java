package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 구독료 결제 내역 — 회차(매월)별로 1행씩 쌓인다. 성공/실패 상관없이 결제 시도할 때마다 기록.
@Entity
@Table(name = "tb_biz_subspt_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizSubsptPayment {

	@Id
	@Column(name = "payment_key", length = 200)
	private String paymentKey;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "plan_cd", length = 20, nullable = false)
	private String planCd;

	@Column(name = "billing_key", length = 200)
	private String billingKey;

	@Column(name = "billing_period", length = 7, nullable = false)
	private String billingPeriod;

	@Column(name = "supplied_amount", nullable = false)
	private BigDecimal suppliedAmount;

	@Column(name = "vat", nullable = false)
	private BigDecimal vat;

	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "requested_dt", nullable = false)
	private LocalDateTime requestedDt;

	@Column(name = "approved_dt")
	private LocalDateTime approvedDt;

	@Column(name = "fail_reason", length = 200)
	private String failReason;

	@Column(name = "receipt_url", length = 500)
	private String receiptUrl;

	@Column(name = "reg_usr_id", length = 50)
	private String regUsrId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "reg_ip", length = 50)
	private String regIp;
}
