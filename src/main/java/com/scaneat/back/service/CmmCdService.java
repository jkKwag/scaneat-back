package com.scaneat.back.service;

import com.scaneat.back.dto.common.CmmCdResponse;
import com.scaneat.back.dto.common.CmmGrpCdResponse;
import com.scaneat.back.repository.CmmCdRepository;
import com.scaneat.back.repository.CmmGrpCdRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmmCdService {

	private final CmmGrpCdRepository cmmGrpCdRepository;
	private final CmmCdRepository cmmCdRepository;

	public List<CmmGrpCdResponse> getGroups() {
		return cmmGrpCdRepository.findByUseYnOrderBySortOrdAsc("Y").stream()
				.map(CmmGrpCdResponse::from)
				.toList();
	}

	public List<CmmCdResponse> getCodes(String grpCd) {
		return cmmCdRepository.findById_GrpCdAndUseYnOrderBySortOrdAsc(grpCd, "Y").stream()
				.map(CmmCdResponse::from)
				.toList();
	}
}
