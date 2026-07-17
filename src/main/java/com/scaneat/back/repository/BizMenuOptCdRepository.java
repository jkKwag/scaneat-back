package com.scaneat.back.repository;

import com.scaneat.back.entity.BizMenuOptCd;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizMenuOptCdRepository extends JpaRepository<BizMenuOptCd, String> {

	List<BizMenuOptCd> findByMenuCdAndUseYnOrderBySortOrdAsc(String menuCd, String useYn);
}
