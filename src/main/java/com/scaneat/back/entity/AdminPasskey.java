package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

// 관리자/직원 계정에 등록된 패스키(지문/생체인증) 자격증명. WebAuthn/FIDO2 표준을 그대로 쓰기 때문에
// 웹 브라우저에서 등록하든 앱(추후 iOS/Android 플랫폼 패스키)에서 등록하든 이 테이블과 검증 로직을
// 그대로 공유할 수 있다. 한 계정이 여러 기기(=여러 credential)를 등록할 수 있어서 tb_admin_oauth와
// 달리 (admin_no, admin_type)을 PK로 잡을 수 없고, 인증기가 발급하는 전역 고유값인 credId를 그대로 PK로 쓴다.
@Entity
@Table(name = "tb_admin_passkey", indexes = {
		@Index(name = "idx_admin_passkey_admin", columnList = "admin_no, admin_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPasskey {

	@Id
	@Column(name = "cred_id", length = 255)
	private String credId;

	@Column(name = "admin_no", length = 36, nullable = false)
	private String adminNo;

	// AdminRole.name() 또는 "EMPLOYEE" — tb_admin_oauth와 동일한 관례
	@Column(name = "admin_type", length = 20, nullable = false)
	private String adminType;

	@Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
	private String publicKey;

	// 리플레이 공격 방지용 카운터 — 인증기가 서명할 때마다 증가시켜 보내고, 서버는 이전 값보다 큰지만 확인한다.
	@ColumnDefault("0")
	@Column(name = "sign_count", nullable = false)
	private Long signCount;

	// 'WEB' | 'IOS' | 'ANDROID' — 검증 로직엔 안 쓰이고 기기 목록 화면 표시/디버깅용
	@Column(name = "platform", length = 10, nullable = false)
	private String platform;

	@Column(name = "transports", length = 100)
	private String transports;

	// 클라우드 동기화 패스키(아이클라우드 키체인/구글 비밀번호 관리자) 여부
	@ColumnDefault("'N'")
	@Column(name = "backed_up", length = 1, nullable = false)
	private String backedUp;

	@Column(name = "device_label", length = 100)
	private String deviceLabel;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "last_used_dt")
	private LocalDateTime lastUsedDt;
}
