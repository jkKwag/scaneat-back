package com.scaneat.back.repository;

import com.scaneat.back.entity.BizEmp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizEmpRepository extends JpaRepository<BizEmp, String> {

	List<BizEmp> findByBizRegNoOrderByRegDtAsc(String bizRegNo);

	Optional<BizEmp> findByEmpIdAndUseYn(String empId, String useYn);
}
