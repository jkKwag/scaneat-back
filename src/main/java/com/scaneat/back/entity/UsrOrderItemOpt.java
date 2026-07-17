package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_usr_order_item_opt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrOrderItemOpt {

	@EmbeddedId
	private UsrOrderItemOptId id;

	@Column(name = "opt_nm", length = 50, nullable = false)
	private String optNm;

	@Column(name = "add_price", nullable = false)
	private BigDecimal addPrice;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;
}
