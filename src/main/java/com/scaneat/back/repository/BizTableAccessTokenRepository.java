package com.scaneat.back.repository;

import com.scaneat.back.entity.BizTableAccessToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizTableAccessTokenRepository extends JpaRepository<BizTableAccessToken, Long> {

	Optional<BizTableAccessToken> findFirstByBizRegNoAndSeatCdAndTokenAndExpiresAtAfterOrderByIdDesc(
			String bizRegNo, String seatCd, String token, LocalDateTime now);
}
