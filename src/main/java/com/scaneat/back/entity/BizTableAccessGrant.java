package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_biz_table_access_grant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizTableAccessGrant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "seat_cd", length = 20, nullable = false)
	private String seatCd;

	@Column(name = "uuid", length = 36, nullable = false)
	private String uuid;

	@Column(name = "granted_at", nullable = false)
	private LocalDateTime grantedAt;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;
}
