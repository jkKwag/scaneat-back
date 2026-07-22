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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_biz_cat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizCat {

	@Id
	@Column(name = "biz_cat_cd", length = 10)
	private String bizCatCd;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "cat_cd", length = 10)
	private String catCd;

	@Column(name = "biz_cat_nm", length = 50, nullable = false)
	private String bizCatNm;

	@ColumnDefault("1")
	@Column(name = "sort_ord", nullable = false)
	private Integer sortOrd;

	@ColumnDefault("'Y'")
	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@Column(name = "rmrk", length = 500)
	private String rmrk;

	@ColumnDefault("'SYSTEM'")
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
