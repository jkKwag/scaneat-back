package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tb_admin_usr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUsr {

	// 발급 즉시 고정되는 대체키(UUID) — 카카오 등 소셜 로그인 전용 계정은 이메일이
	// 없을 수 있어, 더 이상 이메일(admin_id)을 기본키로 쓰지 않는다.
	@Id
	@Column(name = "admin_no", length = 36)
	private String adminNo;

	// 이메일 로그인 계정만 값이 있다 (소셜 로그인 전용 계정은 null 가능).
	@Column(name = "admin_id", length = 255)
	private String adminId;

	// 이메일 로그인 계정만 값이 있다 (소셜 로그인 전용 계정은 null).
	@Column(name = "password_hash", length = 200)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_type", length = 20, nullable = false)
	@ColumnDefault("'EMAIL'")
	private LoginType loginType;

	// TOTP(구글 OTP 등) 2단계 인증용 비밀키 — 등록 전에는 null이며, 등록 전까지는
	// 비밀번호만으로 로그인된다. SUPER 계정만 등록할 수 있다.
	@Column(name = "totp_secret", length = 64)
	private String totpSecret;

	@Enumerated(EnumType.STRING)
	@Column(name = "admin_role_cd", length = 20, nullable = false)
	private AdminRole adminRole;

	@Column(name = "biz_reg_no", length = 10)
	private String bizRegNo;

	@Column(name = "admin_nm", length = 50)
	private String adminNm;

	@Column(name = "mobile_tel", length = 20)
	private String mobileTel;

	@Column(name = "tel", length = 20)
	private String tel;

	@ColumnDefault("'Y'")
	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

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
