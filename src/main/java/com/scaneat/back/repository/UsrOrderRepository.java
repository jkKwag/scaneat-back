package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrOrderRepository extends JpaRepository<UsrOrder, String> {

	List<UsrOrder> findByUuidOrderByRegDtDesc(String uuid);

	List<UsrOrder> findByBizRegNoOrderByRegDtDesc(String bizRegNo);

	List<UsrOrder> findByBizRegNoAndRegDtBetweenOrderByRegDtDesc(String bizRegNo, LocalDateTime from, LocalDateTime to);

	boolean existsByBizRegNoAndPickupNoAndRegDtBetween(String bizRegNo, String pickupNo, LocalDateTime from, LocalDateTime to);
}
