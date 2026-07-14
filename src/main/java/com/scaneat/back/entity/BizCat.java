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
@Table(name = "tb_biz_cat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizCat {

	@Id
	@Column(name = "biz_cat_cd", length = 20)
	private String bizCatCd;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "biz_cat_nm", length = 100, nullable = false)
	private String bizCatNm;

	@Column(name = "sort_ord")
	private Integer sortOrd;

	@Column(name = "use_yn", length = 1)
	private String useYn;
}
