package com.scaneat.back.repository;

import com.scaneat.back.entity.BizTableAccessGrant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizTableAccessGrantRepository extends JpaRepository<BizTableAccessGrant, Long> {

	Optional<BizTableAccessGrant> findFirstByBizRegNoAndSeatCdAndUuidAndExpiresAtAfterOrderByExpiresAtDesc(
			String bizRegNo, String seatCd, String uuid, LocalDateTime now);

	boolean existsByBizRegNoAndSeatCdAndUuidAndExpiresAtAfter(
			String bizRegNo, String seatCd, String uuid, LocalDateTime now);

	List<BizTableAccessGrant> findByBizRegNoAndExpiresAtAfter(String bizRegNo, LocalDateTime now);
}
