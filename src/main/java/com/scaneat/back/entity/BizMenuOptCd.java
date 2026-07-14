package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_biz_menu_opt_cd")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizMenuOptCd {

	@Id
	@Column(name = "opt_cd", length = 20)
	private String optCd;

	@Column(name = "opt_grp_cd", length = 20, nullable = false)
	private String optGrpCd;

	@Column(name = "opt_nm", length = 100, nullable = false)
	private String optNm;

	@Column(name = "add_price", precision = 12, scale = 0)
	private BigDecimal addPrice;

	@Column(name = "sort_ord")
	private Integer sortOrd;
}
