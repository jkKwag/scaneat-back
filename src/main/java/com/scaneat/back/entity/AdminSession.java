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
@Table(name = "tb_admin_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSession {

	@Id
	@Column(name = "token", length = 64)
	private String token;

	// tb_admin_usr.admin_id 또는 tb_biz_emp.emp_id — 로그인 화면이 두 테이블을 함께
	// 조회하는 것과 동일하게, 세션도 하나의 테이블로 같이 관리한다.
	@Column(name = "admin_id", length = 255, nullable = false)
	private String adminId;

	@Column(name = "admin_role", length = 20, nullable = false)
	private String adminRole;

	@Column(name = "biz_reg_no", length = 10)
	private String bizRegNo;

	@Column(name = "issued_dt", nullable = false)
	private LocalDateTime issuedDt;

	@Column(name = "expires_dt", nullable = false)
	private LocalDateTime expiresDt;
}
