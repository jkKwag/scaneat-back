package com.scaneat.back.service;

import com.scaneat.back.dto.biz.SeatStatusResponse;
import com.scaneat.back.entity.BizSeat;
import com.scaneat.back.entity.BizTableAccessGrant;
import com.scaneat.back.entity.OrderStatus;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.repository.BizSeatRepository;
import com.scaneat.back.repository.BizTableAccessGrantRepository;
import com.scaneat.back.repository.UsrOrderRepository;
import com.scaneat.back.repository.UsrPaymentOrderRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 좌석 점유현황 화면용 조회 전용 서비스.
// "착석"은 직원 QR 스캔으로 발급된 손님 주문권한(tb_biz_table_access_grant)이 아직 만료되지 않은 것으로 판단한다.
// 같은 좌석에 여러 권한이 겹쳐 남아있을 수 있어(재스캔 등) 가장 최근에 발급된 권한을 착석 시각으로 삼는다 —
// 테이블을 명시적으로 "치웠음" 처리하는 흐름이 아직 없어서 생기는 근사치임을 감안해야 한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatStatusService {

	private static final int WARN_AFTER_MIN = 30;
	private static final String PAYMENT_DONE_STATUS = "DONE";

	private final BizSeatRepository bizSeatRepository;
	private final BizTableAccessGrantRepository grantRepository;
	private final UsrOrderRepository usrOrderRepository;
	private final UsrPaymentOrderRepository usrPaymentOrderRepository;
	private final UsrPaymentRepository usrPaymentRepository;

	public List<SeatStatusResponse> getSeatStatus(String bizRegNo) {
		LocalDateTime now = LocalDateTime.now();

		List<BizSeat> seats = bizSeatRepository.findById_BizRegNoAndUseYnOrderBySortOrdAsc(bizRegNo, "Y");
		Map<String, LocalDateTime> seatedSinceBySeatCd = grantRepository.findByBizRegNoAndExpiresAtAfter(bizRegNo, now).stream()
				.collect(Collectors.groupingBy(BizTableAccessGrant::getSeatCd,
						Collectors.collectingAndThen(
								Collectors.maxBy(Comparator.comparing(BizTableAccessGrant::getGrantedAt)),
								grant -> grant.map(BizTableAccessGrant::getGrantedAt).orElse(null))));

		return seats.stream()
				.map(seat -> buildStatus(seat, seatedSinceBySeatCd.get(seat.getId().getSeatCd()), now))
				.toList();
	}

	private SeatStatusResponse buildStatus(BizSeat seat, LocalDateTime seatedSince, LocalDateTime now) {
		String seatCd = seat.getId().getSeatCd();
		if (seatedSince == null) {
			return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
					"empty", null, null, false);
		}

		List<UsrOrder> orders = usrOrderRepository
				.findByBizRegNoAndSeatNoAndRegDtAfterOrderByRegDtDesc(seat.getId().getBizRegNo(), seatCd, seatedSince).stream()
				.filter(o -> o.getStatus() != OrderStatus.CANCELED)
				.toList();

		if (orders.isEmpty()) {
			int minutes = (int) Duration.between(seatedSince, now).toMinutes();
			return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
					"seated", minutes, null, minutes >= WARN_AFTER_MIN);
		}

		BigDecimal amount = orders.stream().map(UsrOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		int minutes = (int) Duration.between(orders.get(0).getRegDt(), now).toMinutes();
		String state = orders.stream().allMatch(this::isPaid) ? "paid" : "ordered";
		return new SeatStatusResponse(seatCd, seat.getSeatNm(), seat.getCapacity(), seat.getSeatDesc(),
				state, minutes, amount, false);
	}

	private boolean isPaid(UsrOrder order) {
		return usrPaymentOrderRepository.findById_OrderNo(order.getOrderNo())
				.flatMap(po -> usrPaymentRepository.findById(po.getId().getPaymentKey()))
				.map(payment -> PAYMENT_DONE_STATUS.equals(payment.getStatus()))
				.orElse(false);
	}
}
