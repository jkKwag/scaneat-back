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

// 사업장별 PG(결제대행사) 연동 정보 — 사업장당 1행. 손님이 가게에 결제하는 주문결제에서만 쓰고,
// 사업장이 Scaneat에 내는 구독료 결제(BizSubspt)는 여기와 무관하게 항상 플랫폼 공용 키를 쓴다.
// secretKeyEnc는 조회 시점에 복호화해서 토스 호출에 써야 하므로(단방향 해시 불가) AES로 암호화해서 저장한다.
@Entity
@Table(name = "tb_biz_pg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizPg {

	@Id
	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@ColumnDefault("'TOSS'")
	@Column(name = "pg_provider", length = 20, nullable = false)
	private String pgProvider;

	@Column(name = "secret_key_enc", columnDefinition = "TEXT", nullable = false)
	private String secretKeyEnc;

	@Column(name = "client_key", length = 200)
	private String clientKey;

	@ColumnDefault("'ACTIVE'")
	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "verified_dt")
	private LocalDateTime verifiedDt;

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
