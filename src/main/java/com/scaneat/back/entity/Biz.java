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
@Table(name = "tb_biz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Biz {

	@Id
	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@Column(name = "biz_nm", length = 100, nullable = false)
	private String bizNm;

	@Column(name = "tel_no", length = 20)
	private String telNo;

	@Column(name = "ind_cd", length = 20)
	private String indCd;

	@Column(name = "rep_nm", length = 50, nullable = false)
	private String repNm;

	@Column(name = "opr_stt_cd", length = 1)
	private String bizStatus;

	@Column(name = "addr", length = 200)
	private String addr;

	@Column(name = "addr_dtl", length = 200)
	private String addrDtl;
}
