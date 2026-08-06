package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

// 사업장 구독 현황 — 사업장당 1행, 현재 상태만 관리한다. 요금제 변경 이력은 별도로 안 남기고
// tb_biz_subspt_payment(결제내역)에 회차별로 찍힌 plan_cd를 보면 언제 바뀌었는지 알 수 있다.
@Entity
@Table(name = "tb_biz_subspt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizSubspt {

	@Id
	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@Column(name = "plan_cd", length = 20, nullable = false)
	private String planCd;

	@Column(name = "supplied_amount", nullable = false)
	private BigDecimal suppliedAmount;

	@Column(name = "vat", nullable = false)
	private BigDecimal vat;

	@Column(name = "billing_key", length = 200)
	private String billingKey;

	@Column(name = "billing_day")
	private Integer billingDay;

	@Column(name = "next_billing_dt")
	private LocalDate nextBillingDt;

	// 요금제 변경을 예약만 해둔 상태 — 당장 청구/환불 없이 다음 결제일에 배치가 이 값으로 plan_cd를 갈아끼우고 청구한다.
	@Column(name = "pending_plan_cd", length = 20)
	private String pendingPlanCd;

	@ColumnDefault("'ACTIVE'")
	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "started_dt", nullable = false)
	private LocalDateTime startedDt;

	@Column(name = "canceled_dt")
	private LocalDateTime canceledDt;

	@Column(name = "reg_usr_id", length = 50)
	private String regUsrId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "reg_ip", length = 50)
	private String regIp;

	@Column(name = "upd_usr_id", length = 50)
	private String updUsrId;

	@Column(name = "upd_dt")
	private LocalDateTime updDt;

	@Column(name = "upd_ip", length = 50)
	private String updIp;
}
