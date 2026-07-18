package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizHourRequest;
import com.scaneat.back.dto.biz.BizHourResponse;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.dto.biz.BizRsvnStdRequest;
import com.scaneat.back.dto.biz.BizRsvnStdResponse;
import com.scaneat.back.dto.biz.BizSeatResponse;
import com.scaneat.back.entity.BizHourStd;
import com.scaneat.back.entity.BizHourStdId;
import com.scaneat.back.entity.BizRsvnStd;
import com.scaneat.back.repository.BizCatRepository;
import com.scaneat.back.repository.BizHourStdRepository;
import com.scaneat.back.repository.BizMenuRepository;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.BizRsvnStdRepository;
import com.scaneat.back.repository.BizSeatRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BizService {

	private final BizRepository bizRepository;
	private final BizCatRepository bizCatRepository;
	private final BizMenuRepository bizMenuRepository;
	private final BizHourStdRepository bizHourStdRepository;
	private final BizRsvnStdRepository bizRsvnStdRepository;
	private final BizSeatRepository bizSeatRepository;

	public BizResponse getBiz(String bizRegNo) {
		return bizRepository.findById(bizRegNo)
				.map(BizResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("사업자를 찾을 수 없습니다: " + bizRegNo));
	}

	public List<BizCatResponse> getCategories(String bizRegNo) {
		return bizCatRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo).stream()
				.map(BizCatResponse::from)
				.toList();
	}

	public List<BizMenuResponse> getMenus(String bizRegNo) {
		return bizMenuRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo).stream()
				.map(BizMenuResponse::from)
				.toList();
	}

	public List<BizHourResponse> getHours(String bizRegNo) {
		return bizHourStdRepository.findById_BizRegNo(bizRegNo).stream()
				.map(BizHourResponse::from)
				.toList();
	}

	@Transactional
	public List<BizHourResponse> saveHours(String bizRegNo, BizHourRequest request) {
		LocalDateTime now = LocalDateTime.now();
		List<BizHourStd> saved = request.hours().stream()
				.map(item -> {
					BizHourStdId id = new BizHourStdId(bizRegNo, item.dayOfWeek());
					BizHourStd existing = bizHourStdRepository.findById(id).orElse(null);
					return BizHourStd.builder()
							.id(id)
							.openTime(item.openTime())
							.closeTime(item.closeTime())
							.isClosed(item.isClosed() != null ? item.isClosed() : "N")
							.breakStartTime(item.breakStartTime())
							.breakEndTime(item.breakEndTime())
							.lastOrderTime(item.lastOrderTime())
							.regUsrId(existing != null ? existing.getRegUsrId() : "guest")
							.regDt(existing != null ? existing.getRegDt() : now)
							.updUsrId(existing != null ? "guest" : null)
							.updDt(existing != null ? now : null)
							.build();
				})
				.toList();
		return bizHourStdRepository.saveAll(saved).stream().map(BizHourResponse::from).toList();
	}

	public BizRsvnStdResponse getRsvnStd(String bizRegNo) {
		return bizRsvnStdRepository.findById(bizRegNo)
				.map(BizRsvnStdResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("예약기준을 찾을 수 없습니다: " + bizRegNo));
	}

	@Transactional
	public BizRsvnStdResponse saveRsvnStd(String bizRegNo, BizRsvnStdRequest request) {
		BizRsvnStd existing = bizRsvnStdRepository.findById(bizRegNo).orElse(null);
		LocalDateTime now = LocalDateTime.now();
		BizRsvnStd std = BizRsvnStd.builder()
				.bizRegNo(bizRegNo)
				.useYn(request.useYn() != null ? request.useYn() : "Y")
				.timeUnitMin(request.timeUnitMin() != null ? request.timeUnitMin() : 30)
				.minPartySize(request.minPartySize())
				.maxPartySize(request.maxPartySize())
				.maxAdvanceDays(request.maxAdvanceDays())
				.minAdvanceHours(request.minAdvanceHours())
				.regUsrId(existing != null ? existing.getRegUsrId() : "guest")
				.regDt(existing != null ? existing.getRegDt() : now)
				.updUsrId(existing != null ? "guest" : null)
				.updDt(existing != null ? now : null)
				.build();
		return BizRsvnStdResponse.from(bizRsvnStdRepository.save(std));
	}

	public List<BizSeatResponse> getSeats(String bizRegNo) {
		return bizSeatRepository.findById_BizRegNoAndUseYnOrderBySortOrdAsc(bizRegNo, "Y").stream()
				.map(BizSeatResponse::from)
				.toList();
	}
}
