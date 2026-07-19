package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.reservation.ReservationRequest;
import com.scaneat.back.dto.reservation.ReservationResponse;
import com.scaneat.back.dto.reservation.ReservationStatusUpdateRequest;
import com.scaneat.back.dto.reservation.ReservationUpdateRequest;
import com.scaneat.back.service.ReservationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@GetMapping
	public ApiResponse<List<ReservationResponse>> getReservations(@RequestParam String uuid) {
		return ApiResponse.ok(reservationService.getReservationsByUuid(uuid));
	}

	@GetMapping("/biz/{bizRegNo}")
	public ApiResponse<List<ReservationResponse>> getReservationsByBiz(
			@PathVariable String bizRegNo,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to) {
		if (from != null && to != null) {
			return ApiResponse.ok(reservationService.getReservationsByBiz(bizRegNo, from, to));
		}
		return ApiResponse.ok(reservationService.getReservationsByBiz(bizRegNo, date));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
		return ApiResponse.ok(reservationService.createReservation(request));
	}

	@GetMapping("/{rsvnNo}")
	public ApiResponse<ReservationResponse> getReservation(@PathVariable String rsvnNo) {
		return ApiResponse.ok(reservationService.getReservation(rsvnNo));
	}

	@PutMapping("/{rsvnNo}")
	public ApiResponse<ReservationResponse> updateReservation(
			@PathVariable String rsvnNo, @RequestBody ReservationUpdateRequest request) {
		return ApiResponse.ok(reservationService.updateReservation(rsvnNo, request));
	}

	@PutMapping("/{rsvnNo}/status")
	public ApiResponse<ReservationResponse> updateStatus(
			@PathVariable String rsvnNo, @Valid @RequestBody ReservationStatusUpdateRequest request) {
		return ApiResponse.ok(reservationService.updateStatus(rsvnNo, request));
	}

	@DeleteMapping("/{rsvnNo}")
	public ApiResponse<Void> deleteReservation(@PathVariable String rsvnNo) {
		reservationService.deleteReservation(rsvnNo);
		return ApiResponse.ok(null, "예약이 취소되었습니다.");
	}
}
