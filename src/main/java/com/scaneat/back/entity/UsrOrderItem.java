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
@Table(name = "tb_usr_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrOrderItem {

	@EmbeddedId
	private UsrOrderItemId id;

	@Column(name = "menu_cd", length = 10, nullable = false)
	private String menuCd;

	@Column(name = "menu_nm", length = 50, nullable = false)
	private String menuNm;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "qty", nullable = false)
	private Integer qty;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;
}
