package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrOrderRepository extends JpaRepository<UsrOrder, String> {

	List<UsrOrder> findByUuidOrderByRegDtDesc(String uuid);

	boolean existsByBizRegNoAndRsvnNoAndRegDtBetween(String bizRegNo, String rsvnNo, LocalDateTime from, LocalDateTime to);
}
