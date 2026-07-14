package com.scaneat.back.repository;

import com.scaneat.back.entity.BizCat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizCatRepository extends JpaRepository<BizCat, String> {

	List<BizCat> findByBizRegNoAndUseYnOrderBySortOrdAsc(String bizRegNo, String useYn);

	List<BizCat> findByBizRegNoOrderBySortOrdAsc(String bizRegNo);
}
