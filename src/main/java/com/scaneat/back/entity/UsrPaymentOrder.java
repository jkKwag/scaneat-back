package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_usr_payment_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrPaymentOrder {

	@EmbeddedId
	private UsrPaymentOrderId id;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;
}
