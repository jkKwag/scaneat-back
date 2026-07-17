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
@Table(name = "tb_biz_menu_opt_grp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizMenuOptGrp {

	@Id
	@Column(name = "opt_grp_cd", length = 10)
	private String optGrpCd;

	@Column(name = "opt_grp_nm", length = 50, nullable = false)
	private String optGrpNm;

	@Column(name = "opt_type", length = 1, nullable = false)
	private String optType;

	@Column(name = "required_yn", length = 1, nullable = false)
	private String requiredYn;

	@Column(name = "sort_ord", nullable = false)
	private Integer sortOrd;

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@Column(name = "rmrk", length = 500)
	private String rmrk;

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

	@Column(name = "max_sel_cnt")
	private Integer maxSelCnt;

	@Column(name = "min_sel_cnt")
	private Integer minSelCnt;
}
