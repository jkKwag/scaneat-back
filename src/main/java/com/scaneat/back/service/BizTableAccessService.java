package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.dto.biz.BizTableAccessGrantResponse;
import com.scaneat.back.dto.biz.BizTableAccessTokenResponse;
import com.scaneat.back.entity.BizTableAccessGrant;
import com.scaneat.back.entity.BizTableAccessToken;
import com.scaneat.back.repository.BizTableAccessGrantRepository;
import com.scaneat.back.repository.BizTableAccessTokenRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 직원이 발급한 단기 QR을 손님이 스캔하면, 그 손님(uuid)에게 해당 테이블(seatCd)에서
// 일정 시간 동안 주문할 수 있는 권한을 부여한다. (직원=서버, 손님=클라이언트 모델)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BizTableAccessService {

	private static final String TOKEN_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final int TOKEN_LENGTH = 8;
	private static final long TOKEN_VALID_MINUTES = 2;
	private static final long GRANT_VALID_HOURS = 3;
	private static final SecureRandom RANDOM = new SecureRandom();

	private final BizTableAccessTokenRepository tokenRepository;
	private final BizTableAccessGrantRepository grantRepository;

	@Transactional
	public BizTableAccessTokenResponse issueToken(String bizRegNo, String seatCd) {
		LocalDateTime now = LocalDateTime.now();
		BizTableAccessToken token = BizTableAccessToken.builder()
				.bizRegNo(bizRegNo)
				.seatCd(seatCd)
				.token(generateToken())
				.expiresAt(now.plusMinutes(TOKEN_VALID_MINUTES))
				.regDt(now)
				.build();
		tokenRepository.save(token);
		return new BizTableAccessTokenResponse(token.getToken(), token.getExpiresAt());
	}

	@Transactional
	public BizTableAccessGrantResponse redeemToken(String bizRegNo, String seatCd, String token, String uuid) {
		LocalDateTime now = LocalDateTime.now();
		BizTableAccessToken accessToken = tokenRepository
				.findFirstByBizRegNoAndSeatCdAndTokenAndExpiresAtAfterOrderByIdDesc(bizRegNo, seatCd, token, now)
				.orElseThrow(() -> new BusinessException("QR이 만료되었거나 올바르지 않습니다. 직원에게 다시 요청해주세요."));

		// 같은 QR이 유효시간 내에 다른 사람에게 재사용되지 않도록 스캔 즉시 소모시킨다.
		accessToken.setExpiresAt(now);
		tokenRepository.save(accessToken);

		LocalDateTime expiresAt = now.plusHours(GRANT_VALID_HOURS);
		BizTableAccessGrant grant = BizTableAccessGrant.builder()
				.bizRegNo(bizRegNo)
				.seatCd(seatCd)
				.uuid(uuid)
				.grantedAt(now)
				.expiresAt(expiresAt)
				.build();
		grantRepository.save(grant);
		return new BizTableAccessGrantResponse(true, expiresAt);
	}

	public BizTableAccessGrantResponse getGrantStatus(String bizRegNo, String seatCd, String uuid) {
		LocalDateTime now = LocalDateTime.now();
		return grantRepository
				.findFirstByBizRegNoAndSeatCdAndUuidAndExpiresAtAfterOrderByExpiresAtDesc(bizRegNo, seatCd, uuid, now)
				.map(grant -> new BizTableAccessGrantResponse(true, grant.getExpiresAt()))
				.orElseGet(() -> new BizTableAccessGrantResponse(false, null));
	}

	public boolean hasValidGrant(String bizRegNo, String seatCd, String uuid) {
		return grantRepository.existsByBizRegNoAndSeatCdAndUuidAndExpiresAtAfter(bizRegNo, seatCd, uuid, LocalDateTime.now());
	}

	private String generateToken() {
		StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
		for (int i = 0; i < TOKEN_LENGTH; i++) {
			sb.append(TOKEN_CHARS.charAt(RANDOM.nextInt(TOKEN_CHARS.length())));
		}
		return sb.toString();
	}
}
