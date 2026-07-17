package com.scaneat.back.repository;

import com.scaneat.back.entity.CmmCd;
import com.scaneat.back.entity.CmmCdId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmmCdRepository extends JpaRepository<CmmCd, CmmCdId> {

	List<CmmCd> findById_GrpCdAndUseYnOrderBySortOrdAsc(String grpCd, String useYn);
}
