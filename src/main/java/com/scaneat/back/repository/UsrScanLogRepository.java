package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrScanLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrScanLogRepository extends JpaRepository<UsrScanLog, Long> {

	List<UsrScanLog> findByUuidOrderByScanDtDesc(String uuid);

	List<UsrScanLog> findByBizRegNoOrderByScanDtDesc(String bizRegNo);
}
