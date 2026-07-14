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
@Table(name = "tb_usr_scan_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrScanLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", length = 100, nullable = false)
	private String uuid;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "scan_dt", nullable = false)
	private LocalDateTime scanDt;
}
