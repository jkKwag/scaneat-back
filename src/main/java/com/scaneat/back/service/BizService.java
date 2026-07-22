package com.scaneat.back.service;

import com.scaneat.back.client.SupabaseStorageClient;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.biz.BizCatRequest;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizEmpResponse;
import com.scaneat.back.dto.biz.BizCreateRequest;
import com.scaneat.back.dto.biz.BizUpdateRequest;
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
import com.scaneat.back.dto.biz.ImageUploadResponse;
import com.scaneat.back.entity.Biz;
import com.scaneat.back.entity.BizCat;
import com.scaneat.back.entity.BizHourStd;
import com.scaneat.back.entity.BizHourStdId;
import com.scaneat.back.entity.BizMenu;
import com.scaneat.back.entity.BizRsvnStd;
import com.scaneat.back.entity.BizSeat;
import com.scaneat.back.entity.BizSeatId;
import com.scaneat.back.repository.BizCatRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.scaneat.back.repository.BizHourStdRepository;
import com.scaneat.back.repository.BizMenuRepository;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.BizRsvnStdRepository;
import com.scaneat.back.repository.BizSeatRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BizService {

	private final BizRepository bizRepository;
	private final SupabaseStorageClient supabaseStorageClient;
	private final BizCatRepository bizCatRepository;
	private final BizMenuRepository bizMenuRepository;
	private final BizHourStdRepository bizHourStdRepository;
	private final BizRsvnStdRepository bizRsvnStdRepository;
	private final BizSeatRepository bizSeatRepository;
	private final BizEmpRepository bizEmpRepository;
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

	@Transactional
	public BizResponse createBiz(BizCreateRequest request) {
		if (request.bizRegNo() == null || request.bizRegNo().isBlank()) {
			throw new BusinessException("사업자등록번호를 입력해주세요.");
		}
		if (bizRepository.existsById(request.bizRegNo())) {
			throw new BusinessException(HttpStatus.CONFLICT, "이미 등록된 사업자등록번호입니다: " + request.bizRegNo());
		}
		if (request.repNm() == null || request.repNm().isBlank()) {
			throw new BusinessException("대표자명을 입력해주세요.");
		}
		Biz biz = Biz.builder()
				.bizRegNo(request.bizRegNo())
				.bizNm(request.bizNm())
				.repNm(request.repNm())
				.bizStatus("O")
				.telNo(request.telNo())
				.emailAddr(request.emailAddr())
				.indCd(request.indCd())
				.addr(request.addr())
				.addrDtl(request.addrDtl())
				.build();
		bizRepository.save(biz);
		return BizResponse.from(biz);
	}

	@Transactional
	public BizResponse updateBiz(String bizRegNo, BizUpdateRequest request) {
		Biz biz = bizRepository.findById(bizRegNo)
				.orElseThrow(() -> new ResourceNotFoundException("사업자를 찾을 수 없습니다: " + bizRegNo));
		biz.setBizNm(request.bizNm());
		biz.setTelNo(request.telNo());
		biz.setEmailAddr(request.emailAddr());
		biz.setIndCd(request.indCd());
		biz.setAddr(request.addr());
		biz.setAddrDtl(request.addrDtl());
		return BizResponse.from(biz);
	}

	public List<BizCatResponse> getCategories(String bizRegNo) {
		return bizCatRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo).stream()
				.map(BizCatResponse::from)
				.toList();
	}

	@Transactional
	public BizCatResponse createCategory(String bizRegNo, BizCatRequest request) {
		LocalDateTime now = LocalDateTime.now();
		// 정렬순서를 비워두면(자동) 0으로 설정해 목록 맨 위로 오도록 한다.
		int sortOrd = request.sortOrd() != null ? request.sortOrd() : 0;
		BizCat cat = BizCat.builder()
				.bizCatCd(generateBizCatCd())
				.bizRegNo(bizRegNo)
				.catCd(request.catCd())
				.bizCatNm(request.bizCatNm())
				.sortOrd(sortOrd)
				.useYn(request.useYn() != null ? request.useYn() : "Y")
				.rmrk(request.rmrk())
				.regUsrId("admin")
				.regDt(now)
				.build();
		bizCatRepository.save(cat);
		return BizCatResponse.from(cat);
	}

	@Transactional
	public BizCatResponse updateCategory(String bizRegNo, String bizCatCd, BizCatRequest request) {
		BizCat cat = bizCatRepository.findById(bizCatCd)
				.orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다: " + bizCatCd));
		cat.setCatCd(request.catCd());
		cat.setBizCatNm(request.bizCatNm());
		cat.setRmrk(request.rmrk());
		if (request.sortOrd() != null) {
			cat.setSortOrd(request.sortOrd());
		}
		if (request.useYn() != null) {
			cat.setUseYn(request.useYn());
		}
		cat.setUpdUsrId("admin");
		cat.setUpdDt(LocalDateTime.now());
		return BizCatResponse.from(cat);
	}

	@Transactional
	public void deleteCategory(String bizRegNo, String bizCatCd) {
		BizCat cat = bizCatRepository.findById(bizCatCd)
				.orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다: " + bizCatCd));
		if (bizMenuRepository.existsByBizRegNoAndBizCatCd(bizRegNo, bizCatCd)) {
			throw new BusinessException("해당 카테고리를 사용중인 메뉴가 있어 삭제할 수 없습니다.");
		}
		bizCatRepository.delete(cat);
	}

	private String generateBizCatCd() {
		String bizCatCd;
		int attempts = 0;
		do {
			bizCatCd = "CT" + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
			attempts++;
		} while (bizCatRepository.existsById(bizCatCd) && attempts < 5);
		return bizCatCd;
	}

	public List<BizMenuResponse> getMenus(String bizRegNo) {
		return bizMenuRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo).stream()
				.map(BizMenuResponse::from)
				.toList();
	}

	@Transactional
	public BizMenuResponse createMenu(String bizRegNo, BizMenuRequest request) {
		// 정렬순서를 비워두면(자동) 0으로 설정해 목록 맨 위로 오도록 한다.
		int sortOrd = request.sortOrd() != null ? request.sortOrd() : 0;
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
		// 정렬순서를 비워두면(자동) 0으로 설정해 목록 맨 위로 오도록 한다.
		int sortOrd = request.sortOrd() != null ? request.sortOrd() : 0;
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

	public List<BizEmpResponse> getEmployees(String bizRegNo) {
		return bizEmpRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo).stream()
				.map(BizEmpResponse::from)
				.toList();
	}

	// 프론트에서 리사이징/압축까지 마친 이미지를 받아 Supabase Storage에 서버가 대신 업로드한다.
	// service_role 키는 서버에만 있으므로 클라이언트에 노출되지 않는다.
	public ImageUploadResponse uploadMenuImage(String bizRegNo, MultipartFile file) {
		return uploadImage("menu-image", bizRegNo, file);
	}

	public ImageUploadResponse uploadSeatImage(String bizRegNo, MultipartFile file) {
		return uploadImage("seat-image", bizRegNo, file);
	}

	private ImageUploadResponse uploadImage(String bucket, String bizRegNo, MultipartFile file) {
		if (file.isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다.");
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
		}
		String path = bizRegNo + "/" + System.currentTimeMillis() + ".jpg";
		try {
			String url = supabaseStorageClient.upload(bucket, path, file.getBytes(), "image/jpeg");
			return new ImageUploadResponse(url);
		} catch (IOException e) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "파일을 읽을 수 없습니다.");
		}
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
