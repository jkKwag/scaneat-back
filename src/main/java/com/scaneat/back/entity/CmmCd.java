package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_cmm_cd")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmmCd {

	@EmbeddedId
	private CmmCdId id;

	@Column(name = "cd_nm", length = 100, nullable = false)
	private String cdNm;

	@Column(name = "cd_nm_en", length = 100)
	private String cdNmEn;

	@Column(name = "sort_ord")
	private Integer sortOrd;

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@Column(name = "rmrk", length = 200)
	private String rmrk;

	@Column(name = "rmrk1", length = 200)
	private String rmrk1;

	@Column(name = "rmrk2", length = 200)
	private String rmrk2;

	@Column(name = "rmrk3", length = 200)
	private String rmrk3;

	@Column(name = "rmrk4", length = 200)
	private String rmrk4;

	@Column(name = "rmrk5", length = 200)
	private String rmrk5;

	@Column(name = "rmrk6", length = 200)
	private String rmrk6;

	@Column(name = "rmrk7", length = 200)
	private String rmrk7;

	@Column(name = "rmrk8", length = 200)
	private String rmrk8;

	@Column(name = "rmrk9", length = 200)
	private String rmrk9;

	@Column(name = "rmrk10", length = 200)
	private String rmrk10;

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
