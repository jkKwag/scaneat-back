package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizHourRequest;
import com.scaneat.back.dto.biz.BizHourResponse;
import com.scaneat.back.dto.biz.BizMenuRequest;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizPageResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.dto.biz.BizRsvnStdRequest;
import com.scaneat.back.dto.biz.BizRsvnStdResponse;
import com.scaneat.back.dto.biz.BizSeatResponse;
import com.scaneat.back.dto.biz.BizSeatRequest;
import com.scaneat.back.entity.Biz;
import com.scaneat.back.entity.BizHourStd;
import com.scaneat.back.entity.BizHourStdId;
import com.scaneat.back.entity.BizMenu;
import com.scaneat.back.entity.BizRsvnStd;
import com.scaneat.back.entity.BizSeat;
import com.scaneat.back.entity.BizSeatId;
import com.scaneat.back.repository.BizCatRepository;
import com.scaneat.back.repository.BizHourStdRepository;
import com.scaneat.back.repository.BizMenuRepository;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.BizRsvnStdRepository;
import com.scaneat.back.repository.BizSeatRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
	private final MenuService menuService;

	public BizPageResponse getBizPage(int page, int size) {
		Page<Biz> result = bizRepository.findAll(PageRequest.of(page, size, Sort.by("bizNm")));
		List<BizResponse> items = result.getContent().stream().map(BizResponse::from).toList();
		return new BizPageResponse(items, result.hasNext());
	}

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

	@Transactional
	public BizMenuResponse createMenu(String bizRegNo, BizMenuRequest request) {
		int sortOrd = request.sortOrd() != null
				? request.sortOrd()
				: bizMenuRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo).size() + 1;
		BizMenu menu = BizMenu.builder()
				.menuCd(generateMenuCd())
				.bizRegNo(bizRegNo)
				.bizCatCd(request.bizCatCd())
				.menuNm(request.menuNm())
				.menuDesc(request.menuDesc())
				.price(request.price())
				.imgUrl(request.imgUrl())
				.badge(request.badge())
				.sortOrd(sortOrd)
				.useYn(request.useYn() != null ? request.useYn() : "Y")
				.build();
		bizMenuRepository.save(menu);
		return BizMenuResponse.from(menu);
	}

	@Transactional
	public BizMenuResponse updateMenu(String bizRegNo, String menuCd, BizMenuRequest request) {
		BizMenu menu = bizMenuRepository.findById(menuCd)
				.orElseThrow(() -> new ResourceNotFoundException("메뉴를 찾을 수 없습니다: " + menuCd));
		menu.setBizCatCd(request.bizCatCd());
		menu.setMenuNm(request.menuNm());
		menu.setMenuDesc(request.menuDesc());
		menu.setPrice(request.price());
		menu.setImgUrl(request.imgUrl());
		menu.setBadge(request.badge());
		if (request.sortOrd() != null) {
			menu.setSortOrd(request.sortOrd());
		}
		if (request.useYn() != null) {
			menu.setUseYn(request.useYn());
		}
		return BizMenuResponse.from(menu);
	}

	@Transactional
	public void deleteMenu(String bizRegNo, String menuCd) {
		BizMenu menu = bizMenuRepository.findById(menuCd)
				.orElseThrow(() -> new ResourceNotFoundException("메뉴를 찾을 수 없습니다: " + menuCd));
		menuService.deleteAllOptionsForMenu(menuCd);
		bizMenuRepository.delete(menu);
	}

	private String generateMenuCd() {
		String menuCd;
		int attempts = 0;
		do {
			menuCd = "MN" + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
			attempts++;
		} while (bizMenuRepository.existsById(menuCd) && attempts < 5);
		return menuCd;
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
				.useTimeMin(request.useTimeMin() != null ? request.useTimeMin() : 90)
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

	public List<BizSeatResponse> getSeatsForAdmin(String bizRegNo) {
		return bizSeatRepository.findById_BizRegNoOrderBySortOrdAsc(bizRegNo).stream()
				.map(BizSeatResponse::from)
				.toList();
	}

	@Transactional
	public BizSeatResponse createSeat(String bizRegNo, BizSeatRequest request) {
		LocalDateTime now = LocalDateTime.now();
		int sortOrd = request.sortOrd() != null
				? request.sortOrd()
				: bizSeatRepository.findById_BizRegNoOrderBySortOrdAsc(bizRegNo).size() + 1;
		BizSeat seat = BizSeat.builder()
				.id(new BizSeatId(bizRegNo, generateSeatCd(bizRegNo)))
				.seatNm(request.seatNm())
				.capacity(request.capacity())
				.seatDesc(request.seatDesc())
				.imgUrl(request.imgUrl())
				.sortOrd(sortOrd)
				.useYn(request.useYn() != null ? request.useYn() : "Y")
				.regUsrId("admin")
				.regDt(now)
				.build();
		bizSeatRepository.save(seat);
		return BizSeatResponse.from(seat);
	}

	@Transactional
	public BizSeatResponse updateSeat(String bizRegNo, String seatCd, BizSeatRequest request) {
		BizSeat seat = bizSeatRepository.findById(new BizSeatId(bizRegNo, seatCd))
				.orElseThrow(() -> new ResourceNotFoundException("좌석을 찾을 수 없습니다: " + seatCd));
		seat.setSeatNm(request.seatNm());
		seat.setCapacity(request.capacity());
		seat.setSeatDesc(request.seatDesc());
		seat.setImgUrl(request.imgUrl());
		if (request.sortOrd() != null) {
			seat.setSortOrd(request.sortOrd());
		}
		if (request.useYn() != null) {
			seat.setUseYn(request.useYn());
		}
		seat.setUpdUsrId("admin");
		seat.setUpdDt(LocalDateTime.now());
		return BizSeatResponse.from(seat);
	}

	@Transactional
	public void deleteSeat(String bizRegNo, String seatCd) {
		BizSeatId id = new BizSeatId(bizRegNo, seatCd);
		BizSeat seat = bizSeatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("좌석을 찾을 수 없습니다: " + seatCd));
		bizSeatRepository.delete(seat);
	}

	private String generateSeatCd(String bizRegNo) {
		String seatCd;
		int attempts = 0;
		do {
			seatCd = "ST" + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
			attempts++;
		} while (bizSeatRepository.existsById(new BizSeatId(bizRegNo, seatCd)) && attempts < 5);
		return seatCd;
	}
}
