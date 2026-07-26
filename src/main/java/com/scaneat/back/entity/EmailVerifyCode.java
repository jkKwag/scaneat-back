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

// 이메일 하나당 코드 1개만 유지한다 — 재발송 시 이 row를 덮어쓴다.
@Entity
@Table(name = "tb_email_verify_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerifyCode {

	@Id
	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "code", length = 6, nullable = false)
	private String code;

	@Column(name = "expires_dt", nullable = false)
	private LocalDateTime expiresDt;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "verified_yn", length = 1, nullable = false)
	private String verifiedYn;
}
