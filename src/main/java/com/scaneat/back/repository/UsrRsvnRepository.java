package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrRsvn;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRsvnRepository extends JpaRepository<UsrRsvn, Long> {

	List<UsrRsvn> findByUuidOrderByRsvnDtDesc(String uuid);

	List<UsrRsvn> findByBizRegNoOrderByRsvnDtDesc(String bizRegNo);

	List<UsrRsvn> findByBizRegNoAndRsvnDtBetweenOrderByRsvnDtAsc(String bizRegNo, LocalDateTime from, LocalDateTime to);

	List<UsrRsvn> findByBizRegNoAndRsvnDtBetweenOrderByRsvnDtDesc(String bizRegNo, LocalDateTime from, LocalDateTime to);

	Optional<UsrRsvn> findByRsvnNo(String rsvnNo);

	boolean existsByRsvnNo(String rsvnNo);
}
