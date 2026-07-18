package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminUsr;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUsrRepository extends JpaRepository<AdminUsr, String> {

	Optional<AdminUsr> findByAdminIdAndUseYn(String adminId, String useYn);
}
