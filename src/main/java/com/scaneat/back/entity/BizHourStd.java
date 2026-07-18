package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_biz_std_hour")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizHourStd {

	@EmbeddedId
	private BizHourStdId id;

	@Column(name = "open_time")
	private LocalTime openTime;

	@Column(name = "close_time")
	private LocalTime closeTime;

	@ColumnDefault("'N'")
	@Column(name = "is_closed", length = 1, nullable = false)
	private String isClosed;

	@Column(name = "break_start_time")
	private LocalTime breakStartTime;

	@Column(name = "break_end_time")
	private LocalTime breakEndTime;

	@Column(name = "last_order_time")
	private LocalTime lastOrderTime;

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
