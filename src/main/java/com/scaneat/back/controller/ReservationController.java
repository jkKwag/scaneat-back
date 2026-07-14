package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.reservation.ReservationRequest;
import com.scaneat.back.dto.reservation.ReservationResponse;
import com.scaneat.back.dto.reservation.ReservationUpdateRequest;
import com.scaneat.back.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

	@DeleteMapping("/{rsvnNo}")
	public ApiResponse<Void> deleteReservation(@PathVariable String rsvnNo) {
		reservationService.deleteReservation(rsvnNo);
		return ApiResponse.ok(null, "예약이 취소되었습니다.");
	}
}
