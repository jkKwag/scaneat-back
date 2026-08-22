package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 업종(IndCls)별로 미리 등록해둔 표준 메뉴 카테고리 — 사업장이 카테고리 등록할 때
// 자기 업종에 맞는 카테고리를 검색해서 고를 수 있도록 참조용으로 제공한다.
@Entity
@Table(name = "tb_ind_cat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndCat {

	@Id
	@Column(name = "cat_cd", length = 10)
	private String catCd;

	@Column(name = "ind_cd", length = 10, nullable = false)
	private String indCd;

	@Column(name = "cat_nm", length = 50, nullable = false)
	private String catNm;

	@Column(name = "sort_ord", nullable = false)
	private Integer sortOrd;

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@Column(name = "rmrk", length = 500)
	private String rmrk;

	@Column(name = "reg_usr_id", length = 50, nullable = false)
	private String regUsrId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "reg_ip", length = 45)
	private String regIp;

	@Column(name = "upd_usr_id", length = 50)
	private String updUsrId;

	@Column(name = "upd_dt")
	private LocalDateTime updDt;

	@Column(name = "upd_ip", length = 45)
	private String updIp;
}
