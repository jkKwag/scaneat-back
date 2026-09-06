package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminPasskey;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPasskeyRepository extends JpaRepository<AdminPasskey, String> {

	List<AdminPasskey> findByAdminNoAndAdminTypeOrderByRegDtDesc(String adminNo, String adminType);

	boolean existsByAdminNoAndAdminType(String adminNo, String adminType);
}
