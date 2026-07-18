package com.scaneat.back.repository;

import com.scaneat.back.entity.BizHourStd;
import com.scaneat.back.entity.BizHourStdId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizHourStdRepository extends JpaRepository<BizHourStd, BizHourStdId> {

	List<BizHourStd> findById_BizRegNo(String bizRegNo);
}
