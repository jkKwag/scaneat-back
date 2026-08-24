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

// 브라우저가 웹 푸시(직원호출 알림)를 받기 위해 등록한 채널 하나. 사업장 관리자가 알림을
// 허용하면 브라우저가 발급하는 endpoint/공개키/인증키를 저장해두고, 손님이 직원호출을
// 누르면 이 정보로 해당 사업장의 등록된 모든 브라우저에 푸시를 발송한다.
@Entity
@Table(name = "tb_push_reg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushReg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "push_id")
	private Long pushId;

	@Column(name = "biz_reg_no", length = 10, nullable = false)
	private String bizRegNo;

	@Column(name = "br_endpt_url", columnDefinition = "TEXT", nullable = false)
	private String brEndptUrl;

	@Column(name = "push_pub_key", columnDefinition = "TEXT", nullable = false)
	private String pushPubKey;

	@Column(name = "push_auth_key", columnDefinition = "TEXT", nullable = false)
	private String pushAuthKey;

	@Column(name = "device_type", length = 20, nullable = false)
	private String deviceType;

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@Column(name = "reg_usr_id", length = 50, nullable = false)
	private String regUsrId;

	@Column(name = "reg_dt", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "reg_ip", length = 45)
	private String regIp;

	@Column(name = "upd_usr_id", length = 50)
	private String updUsrId;

	@Column(name = "upd_dt")
	private LocalDateTime updDt;

	@Column(name = "upd_ip", length = 45)
	private String updIp;
}
