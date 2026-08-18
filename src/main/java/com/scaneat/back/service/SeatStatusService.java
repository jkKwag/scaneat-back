package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.biz.SeatStatusResponse;
import com.scaneat.back.entity.BizSeat;
import com.scaneat.back.entity.BizSeatId;
import com.scaneat.back.entity.OrderStatus;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.repository.BizSeatRepository;
import com.scaneat.back.repository.UsrOrderRepository;
import com.scaneat.back.repository.UsrPaymentOrderRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 좌석 점유현황 화면용 조회 전용 서비스.
// "착석"은 직원이 좌석카드에서 수동으로 누르는 토글(tb_biz_seat.seat_status_cd)로 관리한다 —
// QR 스캔 여부로 자동 추정하면, QR 없이 바로 결제하는 손님(대부분의 경우)의 착석을 알 방법이 없어서
// 신뢰할 수 없었다. 주문/결제 여부는 토글과 무관하게 항상 실제 주문 데이터로 자동 판단한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatStatusService {

	private static final String STATUS_SEATED = "SEATED";
	private static final String STATUS_EMPTY = "EMPTY";
	private static final Set<String> VALID_STATUSES = Set.of(STATUS_SEATED, STATUS_EMPTY);

	private static final int WARN_AFTER_MIN = 30;
	// 직원이 착석/해제를 누른 적 없는 좌석은 이 시간 이내의 주문까지만 "지금 이 세션"으로 간주한다.
	private static final int ORDER_LOOKBACK_HOURS = 4;
	private static final String PAYMENT_DONE_STATUS = "DONE";

	private final BizSeatRepository bizSeatRepository;
	private final UsrOrderRepository usrOrderRepository;
	private final UsrPaymentOrderRepository usrPaymentOrderRepository;
	private final UsrPaymentRepository usrPaymentRepository;

	public List<SeatStatusResponse> getSeatStatus(String bizRegNo) {
		LocalDateTime now = LocalDateTime.now();
		List<BizSeat> seats = bizSeatRepository.findById_BizRegNoAndUseYnOrderBySortOrdAsc(bizRegNo, "Y");
		return seats.stream().map(seat -> buildStatus(seat, now)).toList();
	}

	@Transactional
	public SeatStatusResponse setManualStatus(String bizRegNo, String seatCd, String status) {
		if (!VALID_STATUSES.contains(status)) {
			throw new BusinessException("status는 SEATED 또는 EMPTY여야 합니다.");
		}
		BizSeat seat = bizSeatRepository.findById(new BizSeatId(bizRegNo, seatCd))
				.orElseThrow(() -> new ResourceNotFoundException("좌석을 찾을 수 없습니다: " + seatCd));
		seat.setSeatStatusCd(status);
		seat.setSeatStatusAt(LocalDateTime.now());
		bizSeatRepository.save(seat);
		return buildStatus(seat, LocalDateTime.now());
	}

	private SeatStatusResponse buildStatus(BizSeat seat, LocalDateTime now) {
		String seatCd = seat.getId().getSeatCd();
		LocalDateTime windowStart = seat.getSeatStatusAt() != null ? seat.getSeatStatusAt() : now.minusHours(ORDER_LOOKBACK_HOURS);

		List<UsrOrder> orders = usrOrderRepository
				.findByBizRegNoAndSeatNoAndRegDtGreaterThanEqualOrderByRegDtDesc(seat.getId().getBizRegNo(), seatCd, windowStart).stream()
				.filter(o -> o.getStatus() != OrderStatus.CANCELED)
				.toList();

		if (orders.isEmpty()) {
			if (!STATUS_SEATED.equals(seat.getSeatStatusCd())) {
				return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
						"empty", null, null, null, false);
			}
			int minutes = (int) Duration.between(seat.getSeatStatusAt(), now).toMinutes();
			return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
					"seated", minutes, null, null, minutes >= WARN_AFTER_MIN);
		}

		BigDecimal paidAmount = sumAmount(orders, true);
		BigDecimal unpaidAmount = sumAmount(orders, false);
		int minutes = (int) Duration.between(orders.get(0).getRegDt(), now).toMinutes();
		String state = unpaidAmount == null ? "paid" : "ordered";
		return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
				state, minutes, paidAmount, unpaidAmount, false);
	}

	// 결제완료분/미결제분을 나눠서 합산 — 둘 다 없으면(=0원) null로 내려서 프론트에서 안 보이게 한다.
	private BigDecimal sumAmount(List<UsrOrder> orders, boolean paid) {
		BigDecimal sum = orders.stream()
				.filter(o -> isPaid(o) == paid)
				.map(UsrOrder::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return sum.signum() > 0 ? sum : null;
	}

	private boolean isPaid(UsrOrder order) {
		return usrPaymentOrderRepository.findById_OrderNo(order.getOrderNo())
				.flatMap(po -> usrPaymentRepository.findById(po.getId().getPaymentKey()))
				.map(payment -> PAYMENT_DONE_STATUS.equals(payment.getStatus()))
				.orElse(false);
	}
}
