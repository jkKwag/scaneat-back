package com.scaneat.back.service;

import com.scaneat.back.dto.consent.ConsentCheckResponse;
import com.scaneat.back.dto.consent.ConsentRequest;
import com.scaneat.back.dto.consent.ConsentResponse;
import com.scaneat.back.entity.UsrPrvCns;
import com.scaneat.back.repository.UsrPrvCnsRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsentService {

	private final UsrPrvCnsRepository usrPrvCnsRepository;

	public List<ConsentResponse> getConsents(String uuid) {
		return usrPrvCnsRepository.findByUuidOrderByCnsDtDesc(uuid).stream()
				.map(ConsentResponse::from)
				.toList();
	}

	@Transactional
	public ConsentResponse createConsent(ConsentRequest request) {
		UsrPrvCns consent = UsrPrvCns.builder()
				.uuid(request.uuid())
				.bizRegNo(request.bizRegNo())
				.guestName(request.guestName())
				.cnsDt(LocalDateTime.now())
				.build();
		return ConsentResponse.from(usrPrvCnsRepository.save(consent));
	}

	public ConsentCheckResponse checkConsent(String uuid, String bizRegNo) {
		return usrPrvCnsRepository.findFirstByUuidAndBizRegNoOrderByCnsDtDesc(uuid, bizRegNo)
				.map(consent -> new ConsentCheckResponse(true, consent.getCnsDt()))
				.orElse(new ConsentCheckResponse(false, null));
	}
}
