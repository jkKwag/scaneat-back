package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_usr_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrOrder {

	@Id
	@Column(name = "order_no", length = 20)
	private String orderNo;

	@Column(name = "uuid", length = 36, nullable = false)
	private String uuid;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "seat_no", length = 10)
	private String seatNo;

	@ColumnDefault("'DINE_IN'")
	@Column(name = "order_typ_cd", length = 10, nullable = false)
	private String orderTypCd;

	@Column(name = "total_amount", nullable = false)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10, nullable = false)
	private OrderStatus status;

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
