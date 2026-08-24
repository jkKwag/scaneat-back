package com.scaneat.back.repository;

import com.scaneat.back.entity.PushReg;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushRegRepository extends JpaRepository<PushReg, Long> {

	List<PushReg> findByBizRegNoAndUseYn(String bizRegNo, String useYn);

	Optional<PushReg> findByBizRegNoAndBrEndptUrl(String bizRegNo, String brEndptUrl);
}
