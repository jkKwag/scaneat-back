package com.scaneat.back.repository;

import com.scaneat.back.entity.BizPg;
import com.scaneat.back.entity.BizPgId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizPgRepository extends JpaRepository<BizPg, BizPgId> {

	// 한 업체에 등록된 PG 연동 전체 조회 (PK 안의 bizRegNo로 조회하려면 "Id_" 접두사 필요)
	List<BizPg> findById_BizRegNo(String bizRegNo);

	// 결제 시 실제로 쓸 활성화된 PG 하나만 조회
	Optional<BizPg> findById_BizRegNoAndStatus(String bizRegNo, String status);
}
