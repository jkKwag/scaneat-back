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
@Table(name = "tb_usr_prv_cns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrPrvCns {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", length = 36)
	private String uuid;

	@Column(name = "biz_reg_no", length = 10)
	private String bizRegNo;

	@Column(name = "guest_name", length = 50)
	private String guestName;

	@Column(name = "guest_phone", length = 20)
	private String guestPhone;

	@Column(name = "consent_at")
	private LocalDateTime consentAt;

	@Column(name = "reg_usr_id", length = 50)
	private String regUsrId;

	@Column(name = "reg_dt")
	private LocalDateTime regDt;
}
