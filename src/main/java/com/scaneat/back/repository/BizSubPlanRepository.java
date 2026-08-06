package com.scaneat.back.repository;

import com.scaneat.back.entity.BizSubPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizSubPlanRepository extends JpaRepository<BizSubPlan, String> {

	List<BizSubPlan> findByUseYnOrderBySortOrdAsc(String useYn);
}
