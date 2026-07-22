package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminRole;
import com.scaneat.back.entity.AdminUsr;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUsrRepository extends JpaRepository<AdminUsr, String> {

	Optional<AdminUsr> findByAdminIdAndUseYn(String adminId, String useYn);

	List<AdminUsr> findByBizRegNoOrderByRegDtAsc(String bizRegNo);

	long countByAdminRole(AdminRole adminRole);

	long countByUseYn(String useYn);
}
