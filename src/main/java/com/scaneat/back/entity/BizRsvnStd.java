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
@Table(name = "tb_biz_rsvn_std")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizRsvnStd {

	@Id
	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@ColumnDefault("'Y'")
	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@ColumnDefault("30")
	@Column(name = "time_unit_min", nullable = false)
	private Integer timeUnitMin;

	@Column(name = "min_party_size")
	private Integer minPartySize;

	@Column(name = "max_party_size")
	private Integer maxPartySize;

	@Column(name = "max_advance_days")
	private Integer maxAdvanceDays;

	@Column(name = "min_advance_hours")
	private Integer minAdvanceHours;

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
