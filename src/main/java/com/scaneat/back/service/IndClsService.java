package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.industry.IndClsResponse;
import com.scaneat.back.repository.IndClsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndClsService {

	private final IndClsRepository indClsRepository;

	public List<IndClsResponse> getAll() {
		return indClsRepository.findAll().stream()
				.map(IndClsResponse::from)
				.toList();
	}

	public IndClsResponse getByCode(String indCd) {
		return indClsRepository.findById(indCd)
				.map(IndClsResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("업종분류를 찾을 수 없습니다: " + indCd));
	}
}
