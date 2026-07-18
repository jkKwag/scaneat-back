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

@Entity
@Table(name = "tb_usr_rsvn")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrRsvn {

	@Id
	@Column(name = "rsvn_no", length = 30)
	private String rsvnNo;

	@Column(name = "uuid", length = 100, nullable = false)
	private String uuid;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "guest_name", length = 50, nullable = false)
	private String guestName;

	@Column(name = "guest_tel", length = 20)
	private String guestTel;

	@Column(name = "rsvn_dt", nullable = false)
	private LocalDateTime rsvnDt;

	@Column(name = "seat_cd", length = 20)
	private String seatCd;

	@Column(name = "party_size")
	private Integer partySize;

	@Column(name = "memo", length = 500)
	private String memo;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20, nullable = false)
	private ReservationStatus status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
