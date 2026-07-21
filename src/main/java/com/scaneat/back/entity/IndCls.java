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

@Entity
@Table(name = "tb_ind_cls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndCls {

	@Id
	@Column(name = "ind_cd", length = 10)
	private String indCd;

	@Column(name = "prnt_cd", length = 10)
	private String prntCd;

	@Column(name = "ind_nm", length = 200, nullable = false)
	private String indNm;

	@Column(name = "cls_lvl", nullable = false)
	private Integer clsLvl;

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
