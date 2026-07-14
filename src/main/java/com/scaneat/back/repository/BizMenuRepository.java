package com.scaneat.back.repository;

import com.scaneat.back.entity.BizMenu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizMenuRepository extends JpaRepository<BizMenu, String> {

	List<BizMenu> findByBizRegNoOrderBySortOrdAsc(String bizRegNo);

	List<BizMenu> findByBizRegNoAndBizCatCdOrderBySortOrdAsc(String bizRegNo, String bizCatCd);

	List<BizMenu> findByBizRegNoAndMenuNmContaining(String bizRegNo, String menuNm);
}
