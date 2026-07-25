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

	@Column(name = "email_addr", length = 100)
	private String emailAddr;

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

	// 셀프 가입 시에만 쓰는 승인 워크플로 필드들 — 관리자(SUPER)가 수동 등록한 사업장은
	// 처음부터 APPROVED로 만들어져 이 과정을 거치지 않는다.
	@Column(name = "approval_status", length = 20)
	private String approvalStatus;

	// 국세청 상태조회 API 결과(예: "계속사업자", "휴업자", "폐업자") — 조회 실패/미확인이면 null
	@Column(name = "nts_status", length = 20)
	private String ntsStatus;

	// Supabase Storage 안의 경로(공개 URL 아님 — 비공개 버킷이라 매번 서명된 URL로만 열람)
	@Column(name = "biz_cert_path", length = 200)
	private String bizCertPath;

	@Column(name = "reject_rsn", length = 500)
	private String rejectRsn;

	// 가입 직후 사업자등록증을 업로드할 때만 쓰는 1회성 토큰 — 이 시점엔 아직 로그인이
	// 안 되므로(승인 전) 세션 토큰 대신 이걸로 본인 가입건임을 확인한다. 승인/거부 후 null로 비운다.
	@Column(name = "signup_token", length = 64)
	private String signupToken;
}
