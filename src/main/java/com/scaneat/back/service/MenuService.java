package com.scaneat.back.service;

import com.scaneat.back.dto.menu.MenuOptionGroupResponse;
import com.scaneat.back.dto.menu.MenuOptionResponse;
import com.scaneat.back.entity.BizMenuOptCd;
import com.scaneat.back.entity.BizMenuOptGrp;
import com.scaneat.back.repository.BizMenuOptCdRepository;
import com.scaneat.back.repository.BizMenuOptGrpRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

	private final BizMenuOptGrpRepository bizMenuOptGrpRepository;
	private final BizMenuOptCdRepository bizMenuOptCdRepository;

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
}
