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

@Entity
@Table(name = "tb_sys_menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysMenu {

	@Id
	@Column(name = "menu_cd", length = 20)
	private String menuCd;

	@Column(name = "upper_menu_cd", length = 20)
	private String upperMenuCd;

	@Column(name = "menu_nm", length = 100, nullable = false)
	private String menuNm;

	@Column(name = "menu_url", length = 200)
	private String menuUrl;

	@Column(name = "admin_role", length = 20, nullable = false)
	private String adminRole;

	@Column(name = "sort_ord")
	private Integer sortOrd;

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
