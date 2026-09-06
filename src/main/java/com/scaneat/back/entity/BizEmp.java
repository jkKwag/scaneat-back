package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_biz_emp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizEmp {

	// 발급 즉시 고정되는 대체키(UUID) — admin_usr와 동일하게, 소셜 로그인 전용 계정은
	// 이메일이 없을 수 있어 더 이상 이메일(emp_id)을 기본키로 쓰지 않는다.
	@Id
	@Column(name = "emp_no", length = 36)
	private String empNo;

	// 이메일 로그인 계정만 값이 있다 (소셜 로그인 전용 계정은 null 가능).
	@Column(name = "emp_id", length = 255)
	private String empId;

	// 이메일 로그인 계정만 값이 있다 (소셜 로그인 전용 계정은 null).
	@Column(name = "password_hash", length = 200)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_type", length = 20, nullable = false)
	@ColumnDefault("'EMAIL'")
	private LoginType loginType;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "emp_nm", length = 50, nullable = false)
	private String empNm;

	@Column(name = "position_nm", length = 50)
	private String positionNm;

	@Column(name = "dept_nm", length = 50)
	private String deptNm;

	@Column(name = "mobile_tel", length = 20)
	private String mobileTel;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "hire_dt")
	private LocalDate hireDt;

	@Column(name = "resign_dt")
	private LocalDate resignDt;

	@ColumnDefault("'Y'")
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
}
