package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.reservation.ReservationRequest;
import com.scaneat.back.dto.reservation.ReservationResponse;
import com.scaneat.back.dto.reservation.ReservationUpdateRequest;
import com.scaneat.back.entity.ReservationStatus;
import com.scaneat.back.entity.UsrRsvn;
import com.scaneat.back.repository.UsrRsvnRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

	private static final DateTimeFormatter RSVN_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private final UsrRsvnRepository usrRsvnRepository;

	public ReservationResponse getReservation(String rsvnNo) {
		return ReservationResponse.from(findReservation(rsvnNo));
	}

	public List<ReservationResponse> getReservationsByUuid(String uuid) {
		return usrRsvnRepository.findByUuidOrderByRsvnDtDesc(uuid).stream()
				.map(ReservationResponse::from)
				.toList();
	}

	@Transactional
	public ReservationResponse createReservation(ReservationRequest request) {
		UsrRsvn reservation = UsrRsvn.builder()
				.rsvnNo(generateRsvnNo())
				.uuid(request.uuid())
				.bizRegNo(request.bizRegNo())
				.guestName(request.guestName())
				.guestTel(request.guestTel())
				.rsvnDt(request.rsvnDt())
				.seatNo(request.seatNo())
				.partySize(request.partySize())
				.memo(request.memo())
				.status(ReservationStatus.PENDING)
				.createdAt(LocalDateTime.now())
				.build();
		return ReservationResponse.from(usrRsvnRepository.save(reservation));
	}

	@Transactional
	public ReservationResponse updateReservation(String rsvnNo, ReservationUpdateRequest request) {
		UsrRsvn reservation = findReservation(rsvnNo);

		if (request.guestName() != null) {
			reservation.setGuestName(request.guestName());
		}
		if (request.guestTel() != null) {
			reservation.setGuestTel(request.guestTel());
		}
		if (request.rsvnDt() != null) {
			reservation.setRsvnDt(request.rsvnDt());
		}
		if (request.seatNo() != null) {
			reservation.setSeatNo(request.seatNo());
		}
		if (request.partySize() != null) {
			reservation.setPartySize(request.partySize());
		}
		if (request.memo() != null) {
			reservation.setMemo(request.memo());
		}
		if (request.status() != null) {
			reservation.setStatus(ReservationStatus.valueOf(request.status().toUpperCase()));
		}

		return ReservationResponse.from(reservation);
	}

	@Transactional
	public void deleteReservation(String rsvnNo) {
		UsrRsvn reservation = findReservation(rsvnNo);
		usrRsvnRepository.delete(reservation);
	}

	private UsrRsvn findReservation(String rsvnNo) {
		return usrRsvnRepository.findById(rsvnNo)
				.orElseThrow(() -> new ResourceNotFoundException("예약을 찾을 수 없습니다: " + rsvnNo));
	}

	private String generateRsvnNo() {
		String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
		return "R" + LocalDateTime.now().format(RSVN_NO_FORMAT) + suffix;
	}
}
