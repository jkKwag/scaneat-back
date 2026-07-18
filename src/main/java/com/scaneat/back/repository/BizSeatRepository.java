package com.scaneat.back.repository;

import com.scaneat.back.entity.BizSeat;
import com.scaneat.back.entity.BizSeatId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizSeatRepository extends JpaRepository<BizSeat, BizSeatId> {

	List<BizSeat> findById_BizRegNoAndUseYnOrderBySortOrdAsc(String bizRegNo, String useYn);
}
