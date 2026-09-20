package com.scaneat.back.service;

import com.scaneat.back.client.TossPaymentsClient;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.common.security.PgSecretCrypto;
import com.scaneat.back.dto.pg.BizPgRegisterRequest;
import com.scaneat.back.dto.pg.BizPgResponse;
import com.scaneat.back.entity.BizPg;
import com.scaneat.back.entity.BizPgId;
import com.scaneat.back.repository.BizPgRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사업장별 PG(결제대행사) 연동 정보 등록/조회. 손님이 가게에 결제하는 주문결제 전용이며,
// 사업장이 Scaneat에 내는 구독료 결제(BizSubsptService)는 이 서비스와 무관하게 항상 플랫폼
// 공용 키를 쓴다. 키 원본은 업체 본인이 직접 입력하고(오프라인 전달 없이 HTTPS로 바로 서버에),
// 저장 후에는 다시 평문으로 노출하지 않는다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BizPgService {

	private static final String DEFAULT_PROVIDER = "TOSS";

	private final BizPgRepository bizPgRepository;
	private final TossPaymentsClient tossPaymentsClient;
	private final PgSecretCrypto pgSecretCrypto;

	public List<BizPgResponse> getConnections(String bizRegNo) {
		return bizPgRepository.findById_BizRegNo(bizRegNo).stream()
				.map(BizPgResponse::from)
				.toList();
	}

	@Transactional
	public BizPgResponse register(String bizRegNo, BizPgRegisterRequest request, String actorId) {
		String provider = request.pgProvider() == null || request.pgProvider().isBlank()
				? DEFAULT_PROVIDER : request.pgProvider();

		if (!tossPaymentsClient.verifySecretKey(request.secretKey())) {
			throw new BusinessException("유효하지 않은 시크릿키입니다. 토스페이먼츠에서 발급받은 키를 다시 확인해주세요.");
		}

		BizPgId id = new BizPgId(bizRegNo, provider);
		BizPg bizPg = bizPgRepository.findById(id).orElse(null);
		LocalDateTime now = LocalDateTime.now();
		String secretKeyEnc = pgSecretCrypto.encrypt(request.secretKey());

		if (bizPg == null) {
			bizPg = BizPg.builder()
					.id(id)
					.secretKeyEnc(secretKeyEnc)
					.clientKey(request.clientKey())
					.status("ACTIVE")
					.verifiedDt(now)
					.regUsrId(actorId)
					.regDt(now)
					.build();
		} else {
			bizPg.setSecretKeyEnc(secretKeyEnc);
			bizPg.setClientKey(request.clientKey());
			bizPg.setStatus("ACTIVE");
			bizPg.setVerifiedDt(now);
			bizPg.setUpdUsrId(actorId);
			bizPg.setUpdDt(now);
		}
		bizPgRepository.save(bizPg);
		return BizPgResponse.from(bizPg);
	}

	@Transactional
	public BizPgResponse setStatus(String bizRegNo, String pgProvider, String status, String actorId) {
		BizPg bizPg = bizPgRepository.findById(new BizPgId(bizRegNo, pgProvider))
				.orElseThrow(() -> new ResourceNotFoundException("등록된 PG 연동 정보가 없습니다: " + pgProvider));
		bizPg.setStatus(status);
		bizPg.setUpdUsrId(actorId);
		bizPg.setUpdDt(LocalDateTime.now());
		bizPgRepository.save(bizPg);
		return BizPgResponse.from(bizPg);
	}

	// 주문결제 시점에 실제로 쓸 복호화된 시크릿키 — 활성 연동이 없으면 null을 반환해서
	// 호출부(PaymentService)가 플랫폼 공용 키로 폴백할 수 있게 한다.
	public String getActiveSecretKey(String bizRegNo) {
		return bizPgRepository.findById_BizRegNoAndStatus(bizRegNo, "ACTIVE")
				.map(bizPg -> pgSecretCrypto.decrypt(bizPg.getSecretKeyEnc()))
				.orElse(null);
	}

	// 결제위젯을 열 때 쓰는 클라이언트키 — 시크릿키와 달리 공개돼도 되는 값이라 손님(비로그인)도
	// 조회할 수 있다. 등록된 게 없으면 null — 프론트가 플랫폼 고정 클라이언트키로 폴백한다.
	public String getActiveClientKey(String bizRegNo) {
		return bizPgRepository.findById_BizRegNoAndStatus(bizRegNo, "ACTIVE")
				.map(BizPg::getClientKey)
				.filter(k -> k != null && !k.isBlank())
				.orElse(null);
	}
}
