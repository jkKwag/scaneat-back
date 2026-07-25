package com.scaneat.back.dto.reservation;

import com.scaneat.back.entity.UsrRsvn;
import java.time.LocalDateTime;

// 손님이 예약 가능한 시간을 확인할 때 쓰는 공개용 응답 — 좌석/시간/상태만 담고
// guestName·guestPhone 같은 개인정보는 포함하지 않는다. (ReservationResponse와 분리)
public record ReservationAvailabilityResponse(
		String seatCd,
		LocalDateTime rsvnDt,
		String rsvnStatus
) {
	public static ReservationAvailabilityResponse from(UsrRsvn rsvn) {
		return new ReservationAvailabilityResponse(rsvn.getSeatCd(), rsvn.getRsvnDt(), rsvn.getRsvnStatus().name());
	}
}
