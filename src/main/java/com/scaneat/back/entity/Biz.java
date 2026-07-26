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

	@Column(name = "mobile_tel", length = 20)
	private String mobileTel;

	@Column(name = "email_addr", length = 100)
	private String emailAddr;

	@Column(name = "ind_cd", length = 20)
	private String indCd;

	// 셀프 가입 시에는 대표자명을 받지 않고 나중에 사업자등록증 이미지 인식으로 채울 예정이라 nullable.
	// 관리자가 수동 등록(createBiz)할 때는 여전히 필수로 검증한다.
	@Column(name = "rep_nm", length = 50)
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

	// 국세청 상태조회 결과 코드(01=계속사업자, 02=휴업자, 03=폐업자, 공통코드 NTS_STT_CD로 관리) — 조회 실패/미확인이면 null
	@Column(name = "nts_status_cd", length = 20)
	private String ntsStatusCd;

	// API 자체가 실패해서 확인을 못 한 경우의 안내 문구 — 정상 조회(성공/미확인)면 null
	@Column(name = "nts_status_msg", length = 200)
	private String ntsStatusMsg;

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
