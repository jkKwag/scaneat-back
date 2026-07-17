package com.scaneat.back.repository;

import com.scaneat.back.entity.CmmGrpCd;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmmGrpCdRepository extends JpaRepository<CmmGrpCd, String> {

	List<CmmGrpCd> findByUseYnOrderBySortOrdAsc(String useYn);
}
