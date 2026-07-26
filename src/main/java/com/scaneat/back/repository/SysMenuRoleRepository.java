package com.scaneat.back.repository;

import com.scaneat.back.entity.SysMenuRole;
import com.scaneat.back.entity.SysMenuRoleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysMenuRoleRepository extends JpaRepository<SysMenuRole, SysMenuRoleId> {

	List<SysMenuRole> findById_AdminRole(String adminRole);
}
