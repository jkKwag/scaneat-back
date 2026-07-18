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

	@Id
	@Column(name = "admin_id", length = 50)
	private String adminId;

	@Column(name = "password_hash", length = 200, nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "admin_role", length = 20, nullable = false)
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
