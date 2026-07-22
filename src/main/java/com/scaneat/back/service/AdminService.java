package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.AdminUsrResponse;
import com.scaneat.back.dto.admin.SysMenuResponse;
import com.scaneat.back.dto.common.PasswordChangeRequest;
import com.scaneat.back.dto.common.PasswordVerifyRequest;
import com.scaneat.back.dto.common.PasswordVerifyResponse;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.BizEmp;
import com.scaneat.back.entity.SysMenu;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.scaneat.back.repository.SysMenuRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private static final String FORBIDDEN_MESSAGE = "본인 계정의 비밀번호만 변경할 수 있습니다.";
	private static final String WRONG_CURRENT_PASSWORD_MESSAGE = "현재 비밀번호가 일치하지 않습니다.";
	private static final String SAME_PASSWORD_MESSAGE = "현재 비밀번호와 다른 비밀번호로 설정해주세요.";

	private final AdminUsrRepository adminUsrRepository;
	private final BizEmpRepository bizEmpRepository;
	private final SysMenuRepository sysMenuRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// 관리자 계정(tb_admin_usr)과 직원 계정(tb_biz_emp)을 같은 로그인 화면에서 함께 조회한다.
	// 두 테이블은 완전히 분리된 채로 두고, 로그인 시도만 순서대로 두 곳을 확인한다.
	public AdminLoginResponse login(AdminLoginRequest request) {
		Optional<AdminUsr> admin = adminUsrRepository.findByAdminIdAndUseYn(request.adminId(), "Y");
		if (admin.isPresent()) {
			if (!passwordEncoder.matches(request.password(), admin.get().getPasswordHash())) {
				throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
			}
			return AdminLoginResponse.from(admin.get());
		}

		BizEmp emp = bizEmpRepository.findByEmpIdAndUseYn(request.adminId(), "Y")
				.orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
		if (!passwordEncoder.matches(request.password(), emp.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
		}
		return AdminLoginResponse.fromEmployee(emp);
	}

	public List<AdminUsrResponse> getUsersByBiz(String bizRegNo) {
		return adminUsrRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo).stream()
				.map(AdminUsrResponse::from)
				.toList();
	}

	@Transactional
	public void changePassword(String adminId, PasswordChangeRequest request) {
		checkSelfOrSuper(adminId, request.requesterId(), request.requesterRole());
		AdminUsr admin = adminUsrRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("관리자 계정을 찾을 수 없습니다: " + adminId));
		if (!passwordEncoder.matches(request.currentPassword(), admin.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, WRONG_CURRENT_PASSWORD_MESSAGE);
		}
		if (passwordEncoder.matches(request.newPassword(), admin.getPasswordHash())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, SAME_PASSWORD_MESSAGE);
		}
		admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		adminUsrRepository.save(admin);
	}

	@Transactional
	public void changeEmployeePassword(String empId, PasswordChangeRequest request) {
		checkSelfOrSuper(empId, request.requesterId(), request.requesterRole());
		BizEmp emp = bizEmpRepository.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException("직원 계정을 찾을 수 없습니다: " + empId));
		if (!passwordEncoder.matches(request.currentPassword(), emp.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, WRONG_CURRENT_PASSWORD_MESSAGE);
		}
		if (passwordEncoder.matches(request.newPassword(), emp.getPasswordHash())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, SAME_PASSWORD_MESSAGE);
		}
		emp.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		bizEmpRepository.save(emp);
	}

	// 비밀번호 변경 전, 입력한 현재 비밀번호가 맞는지만 확인한다 (실제 변경은 하지 않음).
	public PasswordVerifyResponse verifyPassword(String adminId, PasswordVerifyRequest request) {
		checkSelfOrSuper(adminId, request.requesterId(), request.requesterRole());
		AdminUsr admin = adminUsrRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("관리자 계정을 찾을 수 없습니다: " + adminId));
		return new PasswordVerifyResponse(passwordEncoder.matches(request.password(), admin.getPasswordHash()));
	}

	public PasswordVerifyResponse verifyEmployeePassword(String empId, PasswordVerifyRequest request) {
		checkSelfOrSuper(empId, request.requesterId(), request.requesterRole());
		BizEmp emp = bizEmpRepository.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException("직원 계정을 찾을 수 없습니다: " + empId));
		return new PasswordVerifyResponse(passwordEncoder.matches(request.password(), emp.getPasswordHash()));
	}

	// 슈퍼관리자가 아니면 본인 계정의 비밀번호만 변경/확인할 수 있다.
	private void checkSelfOrSuper(String targetId, String requesterId, String requesterRole) {
		boolean isSuper = "SUPER".equals(requesterRole);
		boolean isSelf = requesterId.equals(targetId);
		if (!isSuper && !isSelf) {
			throw new BusinessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
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
