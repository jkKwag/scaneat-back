package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_biz_menu_opt_grp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizMenuOptGrp {

	@Id
	@Column(name = "opt_grp_cd", length = 20)
	private String optGrpCd;

	@Column(name = "menu_cd", length = 20, nullable = false)
	private String menuCd;

	@Column(name = "opt_grp_nm", length = 100, nullable = false)
	private String optGrpNm;

	@Column(name = "required_yn", length = 1)
	private String requiredYn;

	@Column(name = "min_sel_cnt")
	private Integer minSelCnt;

	@Column(name = "max_sel_cnt")
	private Integer maxSelCnt;

	@Column(name = "sort_ord")
	private Integer sortOrd;
}
