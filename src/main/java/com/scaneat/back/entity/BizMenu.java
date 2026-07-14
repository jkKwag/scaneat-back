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
@Table(name = "tb_biz_menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizMenu {

	@Id
	@Column(name = "menu_cd", length = 20)
	private String menuCd;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "biz_cat_cd", length = 20, nullable = false)
	private String bizCatCd;

	@Column(name = "menu_nm", length = 100, nullable = false)
	private String menuNm;

	@Column(name = "menu_desc", length = 500)
	private String menuDesc;

	@Column(name = "price", precision = 12, scale = 0, nullable = false)
	private BigDecimal price;

	@Column(name = "img_url", length = 500)
	private String imgUrl;

	@Column(name = "badge", length = 20)
	private String badge;

	@Column(name = "sort_ord")
	private Integer sortOrd;

	@Column(name = "use_yn", length = 1)
	private String useYn;
}
