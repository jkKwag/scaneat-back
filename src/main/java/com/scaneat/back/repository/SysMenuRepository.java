package com.scaneat.back.repository;

import com.scaneat.back.entity.SysMenu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysMenuRepository extends JpaRepository<SysMenu, String> {

	List<SysMenu> findByAdminRoleInAndUseYnOrderBySortOrdAsc(List<String> adminRoles, String useYn);
}
