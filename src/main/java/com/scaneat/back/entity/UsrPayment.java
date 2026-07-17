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

@Entity
@Table(name = "tb_usr_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrPayment {

	@Id
	@Column(name = "payment_key", length = 200)
	private String paymentKey;

	@Column(name = "uuid", length = 36, nullable = false)
	private String uuid;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "total_amount", nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "method", length = 20)
	private String method;

	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "approved_dt")
	private LocalDateTime approvedDt;

	@Column(name = "reg_usr_id", length = 50)
	private String regUsrId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "reg_ip", length = 50)
	private String regIp;
}
