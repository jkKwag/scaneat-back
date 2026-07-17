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
@Table(name = "tb_usr_payment_pg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrPaymentPg {

	@Id
	@Column(name = "payment_key", length = 200)
	private String paymentKey;

	@Column(name = "last_transaction_key", length = 200)
	private String lastTransactionKey;

	@Column(name = "requested_dt")
	private LocalDateTime requestedDt;

	@Column(name = "receipt_url", length = 500)
	private String receiptUrl;

	@Column(name = "issuer_code", length = 10)
	private String issuerCode;

	@Column(name = "card_no", length = 30)
	private String cardNo;

	@Column(name = "approve_no", length = 30)
	private String approveNo;

	@Column(name = "installment_months")
	private Integer installmentMonths;

	@Column(name = "supplied_amount")
	private BigDecimal suppliedAmount;

	@Column(name = "vat")
	private BigDecimal vat;

	@Column(name = "order_name", length = 200)
	private String orderName;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;
}
