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

// 관리자/직원 계정과 소셜 로그인(카카오 등) 연동 정보. 한 계정이 provider별로 하나씩만
// 연결 가능(PK에 provider 포함), 같은 소셜 계정이 여러 관리자에 연결되는 것은
// uq_provider_user 유니크 제약으로 막는다.
@Entity
@Table(name = "tb_admin_oauth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOauth {

	@EmbeddedId
	private AdminOauthId id;

	@Column(name = "provider_user_id", length = 100, nullable = false)
	private String providerUserId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;
}
