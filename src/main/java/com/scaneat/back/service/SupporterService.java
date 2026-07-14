package com.scaneat.back.service;

import com.scaneat.back.dto.supporter.SupporterRequest;
import com.scaneat.back.dto.supporter.SupporterResponse;
import com.scaneat.back.entity.Supporter;
import com.scaneat.back.repository.SupporterRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupporterService {

	private final SupporterRepository supporterRepository;

	public List<SupporterResponse> getSupporters() {
		return supporterRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(SupporterResponse::from)
				.toList();
	}

	@Transactional
	public SupporterResponse createSupporter(SupporterRequest request) {
		Supporter supporter = Supporter.builder()
				.name(request.name())
				.amount(request.amount())
				.message(request.message())
				.createdAt(LocalDateTime.now())
				.build();
		return SupporterResponse.from(supporterRepository.save(supporter));
	}
}
