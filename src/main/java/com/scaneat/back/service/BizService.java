package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.repository.BizCatRepository;
import com.scaneat.back.repository.BizMenuRepository;
import com.scaneat.back.repository.BizRepository;
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
}
