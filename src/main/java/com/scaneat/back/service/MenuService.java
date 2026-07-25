package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.menu.MenuOptionGroupRequest;
import com.scaneat.back.dto.menu.MenuOptionGroupResponse;
import com.scaneat.back.dto.menu.MenuOptionRequest;
import com.scaneat.back.dto.menu.MenuOptionResponse;
import com.scaneat.back.entity.BizMenu;
import com.scaneat.back.entity.BizMenuOptCd;
import com.scaneat.back.entity.BizMenuOptGrp;
import com.scaneat.back.repository.BizMenuOptCdRepository;
import com.scaneat.back.repository.BizMenuOptGrpRepository;
import com.scaneat.back.repository.BizMenuRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

	private final BizMenuOptGrpRepository bizMenuOptGrpRepository;
	private final BizMenuOptCdRepository bizMenuOptCdRepository;
	private final BizMenuRepository bizMenuRepository;

	public List<MenuOptionGroupResponse> getMenuOptions(String menuCd) {
		List<BizMenuOptCd> optionCodes = bizMenuOptCdRepository.findByMenuCdAndUseYnOrderBySortOrdAsc(menuCd, "Y");
		if (optionCodes.isEmpty()) {
			return List.of();
		}

		Map<String, List<MenuOptionResponse>> optionsByGroupCd = optionCodes.stream()
				.collect(Collectors.groupingBy(BizMenuOptCd::getOptGrpCd,
						Collectors.mapping(MenuOptionResponse::from, Collectors.toList())));

		List<BizMenuOptGrp> groups = bizMenuOptGrpRepository.findAllById(optionsByGroupCd.keySet());

		return groups.stream()
				.sorted(Comparator.comparing(BizMenuOptGrp::getSortOrd, Comparator.nullsLast(Integer::compareTo)))
				.map(grp -> MenuOptionGroupResponse.from(grp, optionsByGroupCd.getOrDefault(grp.getOptGrpCd(), List.of())))
				.toList();
	}

	@Transactional
	public MenuOptionGroupResponse createOptionGroup(String menuCd, MenuOptionGroupRequest request, CurrentAdmin requester) {
		verifyOwnership(menuCd, requester);
		LocalDateTime now = LocalDateTime.now();
		String optGrpCd = generateOptGrpCd();

		BizMenuOptGrp grp = BizMenuOptGrp.builder()
				.optGrpCd(optGrpCd)
				.optGrpNm(request.optGrpNm())
				.optType(request.optType() != null ? request.optType() : "R")
				.requiredYn(request.requiredYn() != null ? request.requiredYn() : "N")
				.sortOrd(countGroupsForMenu(menuCd) + 1)
				.useYn("Y")
				.minSelCnt(request.minSelCnt())
				.maxSelCnt(request.maxSelCnt())
				.regUsrId("admin")
				.regDt(now)
				.build();
		bizMenuOptGrpRepository.save(grp);

		List<MenuOptionRequest> optionRequests = request.options() != null ? request.options() : List.of();
		List<BizMenuOptCd> codes = new ArrayList<>();
		int seq = 1;
		for (MenuOptionRequest optReq : optionRequests) {
			codes.add(BizMenuOptCd.builder()
					.optCd(generateOptCd())
					.optGrpCd(optGrpCd)
					.menuCd(menuCd)
					.optNm(optReq.optNm())
					.addPrice(optReq.addPrice() != null ? optReq.addPrice() : BigDecimal.ZERO)
					.sortOrd(seq++)
					.useYn("Y")
					.regUsrId("admin")
					.regDt(now)
					.build());
		}
		bizMenuOptCdRepository.saveAll(codes);

		return MenuOptionGroupResponse.from(grp, codes.stream().map(MenuOptionResponse::from).toList());
	}

	@Transactional
	public MenuOptionResponse addOption(String menuCd, String optGrpCd, MenuOptionRequest request, CurrentAdmin requester) {
		verifyOwnership(menuCd, requester);
		LocalDateTime now = LocalDateTime.now();
		int sortOrd = bizMenuOptCdRepository.findByMenuCdAndOptGrpCd(menuCd, optGrpCd).size() + 1;
		BizMenuOptCd code = BizMenuOptCd.builder()
				.optCd(generateOptCd())
				.optGrpCd(optGrpCd)
				.menuCd(menuCd)
				.optNm(request.optNm())
				.addPrice(request.addPrice() != null ? request.addPrice() : BigDecimal.ZERO)
				.sortOrd(sortOrd)
				.useYn("Y")
				.regUsrId("admin")
				.regDt(now)
				.build();
		bizMenuOptCdRepository.save(code);
		return MenuOptionResponse.from(code);
	}

	@Transactional
	public void deleteOption(String menuCd, String optGrpCd, String optCd, CurrentAdmin requester) {
		verifyOwnership(menuCd, requester);
		bizMenuOptCdRepository.deleteById(optCd);
		cleanupOrphanGroup(optGrpCd);
	}

	@Transactional
	public void deleteOptionGroup(String menuCd, String optGrpCd, CurrentAdmin requester) {
		verifyOwnership(menuCd, requester);
		List<BizMenuOptCd> codes = bizMenuOptCdRepository.findByMenuCdAndOptGrpCd(menuCd, optGrpCd);
		bizMenuOptCdRepository.deleteAll(codes);
		cleanupOrphanGroup(optGrpCd);
	}

	@Transactional
	public void deleteAllOptionsForMenu(String menuCd) {
		List<BizMenuOptCd> codes = bizMenuOptCdRepository.findByMenuCd(menuCd);
		bizMenuOptCdRepository.deleteAll(codes);
		codes.stream().map(BizMenuOptCd::getOptGrpCd).distinct().forEach(this::cleanupOrphanGroup);
	}

	// 로그인은 했지만(다른 매장 관리자여도) 옵션을 건드리려는 메뉴가 실제 본인 매장 소유인지 확인한다.
	// AdminAuthInterceptor의 일반 소유권 체크는 경로에 bizRegNo가 있을 때만 동작하는데,
	// 이 API들은 menuCd만 경로에 있어서 여기서 별도로 DB를 조회해 확인해야 한다.
	private void verifyOwnership(String menuCd, CurrentAdmin requester) {
		if (requester.isSuper()) return;
		BizMenu menu = bizMenuRepository.findById(menuCd)
				.orElseThrow(() -> new ResourceNotFoundException("메뉴를 찾을 수 없습니다: " + menuCd));
		if (!menu.getBizRegNo().equals(requester.bizRegNo())) {
			throw new BusinessException(HttpStatus.FORBIDDEN, "다른 매장의 메뉴는 수정할 수 없습니다.");
		}
	}

	private void cleanupOrphanGroup(String optGrpCd) {
		if (!bizMenuOptCdRepository.existsByOptGrpCd(optGrpCd)) {
			bizMenuOptGrpRepository.deleteById(optGrpCd);
		}
	}

	private int countGroupsForMenu(String menuCd) {
		return (int) bizMenuOptCdRepository.findByMenuCd(menuCd).stream()
				.map(BizMenuOptCd::getOptGrpCd)
				.distinct()
				.count();
	}

	private String generateOptGrpCd() {
		String code;
		int attempts = 0;
		do {
			code = "OG" + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
			attempts++;
		} while (bizMenuOptGrpRepository.existsById(code) && attempts < 5);
		return code;
	}

	private String generateOptCd() {
		String code;
		int attempts = 0;
		do {
			code = "OC" + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
			attempts++;
		} while (bizMenuOptCdRepository.existsById(code) && attempts < 5);
		return code;
	}
}
