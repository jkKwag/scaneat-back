package com.scaneat.back.repository;

import com.scaneat.back.entity.BizMenuOptGrp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizMenuOptGrpRepository extends JpaRepository<BizMenuOptGrp, String> {

	List<BizMenuOptGrp> findByMenuCdOrderBySortOrdAsc(String menuCd);
}
