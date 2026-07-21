package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.AdminUsrResponse;
import com.scaneat.back.dto.admin.SysMenuResponse;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.SysMenu;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.SysMenuRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

	private static final String INVALID_CREDENTIALS_MESSAGE = "아이디 또는 비밀번호가 올바르지 않습니다.";

	private final AdminUsrRepository adminUsrRepository;
	private final SysMenuRepository sysMenuRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AdminLoginResponse login(AdminLoginRequest request) {
		AdminUsr admin = adminUsrRepository.findByAdminIdAndUseYn(request.adminId(), "Y")
				.orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
		if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
		}
		return AdminLoginResponse.from(admin);
	}

	public List<AdminUsrResponse> getUsersByBiz(String bizRegNo) {
		return adminUsrRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo).stream()
				.map(AdminUsrResponse::from)
				.toList();
	}

	public List<SysMenuResponse> getMenuTree(String adminRole) {
		List<SysMenu> menus = sysMenuRepository.findByAdminRoleInAndUseYnOrderBySortOrdAsc(List.of(adminRole, "ALL"), "Y");
		Map<String, List<SysMenu>> childrenByParent = menus.stream()
				.collect(Collectors.groupingBy(m -> m.getUpperMenuCd() == null ? "ROOT" : m.getUpperMenuCd()));
		return buildTree(childrenByParent, "ROOT");
	}

	private List<SysMenuResponse> buildTree(Map<String, List<SysMenu>> childrenByParent, String parentKey) {
		return childrenByParent.getOrDefault(parentKey, List.of()).stream()
				.map(m -> SysMenuResponse.from(m, buildTree(childrenByParent, m.getMenuCd())))
				.toList();
	}
}
