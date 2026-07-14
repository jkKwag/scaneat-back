package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrRsvn;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRsvnRepository extends JpaRepository<UsrRsvn, String> {

	List<UsrRsvn> findByUuidOrderByRsvnDtDesc(String uuid);

	List<UsrRsvn> findByBizRegNoOrderByRsvnDtDesc(String bizRegNo);
}
