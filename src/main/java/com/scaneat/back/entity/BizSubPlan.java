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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_biz_sub_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizSubPlan {

	@Id
	@Column(name = "plan_cd", length = 20)
	private String planCd;

	@Column(name = "plan_nm", length = 50, nullable = false)
	private String planNm;

	@Column(name = "supplied_amount", nullable = false)
	private BigDecimal suppliedAmount;

	@Column(name = "vat", nullable = false)
	private BigDecimal vat;

	@ColumnDefault("'N'")
	@Column(name = "use_dine_in_yn", length = 1, nullable = false)
	private String useDineInYn;

	@ColumnDefault("'N'")
	@Column(name = "use_takeout_yn", length = 1, nullable = false)
	private String useTakeoutYn;

	@ColumnDefault("'N'")
	@Column(name = "use_delivery_yn", length = 1, nullable = false)
	private String useDeliveryYn;

	@Column(name = "sort_ord")
	private Integer sortOrd;

	@ColumnDefault("'Y'")
	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

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
