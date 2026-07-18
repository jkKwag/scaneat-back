package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tb_usr_rsvn")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrRsvn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "rsvn_no", length = 13)
	private String rsvnNo;

	@Column(name = "uuid", length = 36)
	private String uuid;

	@Column(name = "biz_reg_no", length = 10)
	private String bizRegNo;

	@Column(name = "guest_name", length = 50)
	private String guestName;

	@Column(name = "guest_phone")
	private String guestPhone;

	@Column(name = "rsvn_dt")
	private LocalDateTime rsvnDt;

	@Column(name = "seat_cd", length = 20)
	private String seatCd;

	@Column(name = "party_size")
	private Integer partySize;

	@Column(name = "memo", length = 500)
	private String memo;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20)
	private ReservationStatus status;

	@Column(name = "reg_usr_id", length = 50)
	private String regUsrId;

	@Column(name = "reg_dt")
	private LocalDateTime regDt;
}
