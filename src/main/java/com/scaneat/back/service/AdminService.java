package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.common.security.TotpUtil;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.AdminUsrResponse;
import com.scaneat.back.dto.admin.SysMenuResponse;
import com.scaneat.back.dto.admin.TotpConfirmRequest;
import com.scaneat.back.dto.admin.TotpSetupResponse;
import com.scaneat.back.dto.common.PasswordChangeRequest;
import com.scaneat.back.dto.common.PasswordVerifyRequest;
import com.scaneat.back.dto.common.PasswordVerifyResponse;
import com.scaneat.back.entity.AdminSession;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.BizEmp;
import com.scaneat.back.entity.SysMenu;
import com.scaneat.back.repository.AdminSessionRepository;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.scaneat.back.repository.SysMenuRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
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
	// 프론트가 이 메시지를 보고 인증 코드 입력창을 띄운다 (로그인 실패가 아니라 추가 단계 필요 신호).
	private static final String TOTP_REQUIRED_MESSAGE = "TOTP_REQUIRED";
	private static final String TOTP_INVALID_MESSAGE = "인증 코드가 올바르지 않습니다.";
	private static final String SUPER_ONLY_MESSAGE = "슈퍼관리자만 설정할 수 있습니다.";
	private static final String TOTP_ISSUER = "Scaneat";
	private static final long SESSION_TTL_HOURS = 12;

	private final AdminUsrRepository adminUsrRepository;
	private final BizEmpRepository bizEmpRepository;
	private final SysMenuRepository sysMenuRepository;
	private final AdminSessionRepository adminSessionRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final SecureRandom secureRandom = new SecureRandom();

	// 관리자 계정(tb_admin_usr)과 직원 계정(tb_biz_emp)을 같은 로그인 화면에서 함께 조회한다.
	// 두 테이블은 완전히 분리된 채로 두고, 로그인 시도만 순서대로 두 곳을 확인한다.
	@Transactional
	public AdminLoginResponse login(AdminLoginRequest request) {
		Optional<AdminUsr> admin = adminUsrRepository.findByAdminIdAndUseYn(request.adminId(), "Y");
		if (admin.isPresent()) {
			if (!passwordEncoder.matches(request.password(), admin.get().getPasswordHash())) {
				throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
			}
			// TOTP를 등록한 계정만 추가 단계가 필요하다 — 등록 전까지는 비밀번호만으로 로그인된다.
			if (admin.get().getTotpSecret() != null) {
				if (request.totpCode() == null || request.totpCode().isBlank()) {
					throw new BusinessException(HttpStatus.UNAUTHORIZED, TOTP_REQUIRED_MESSAGE);
				}
				if (!TotpUtil.verifyCode(admin.get().getTotpSecret(), request.totpCode())) {
					throw new BusinessException(HttpStatus.UNAUTHORIZED, TOTP_INVALID_MESSAGE);
				}
			}
			String token = issueSession(admin.get().getAdminId(), admin.get().getAdminRole().name(), admin.get().getBizRegNo());
			return AdminLoginResponse.from(admin.get(), token);
		}

		BizEmp emp = bizEmpRepository.findByEmpIdAndUseYn(request.adminId(), "Y")
				.orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
		if (!passwordEncoder.matches(request.password(), emp.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
		}
		String token = issueSession(emp.getEmpId(), "EMPLOYEE", emp.getBizRegNo());
		return AdminLoginResponse.fromEmployee(emp, token);
	}

	// 위조 불가능한 무작위 세션 토큰을 발급해 DB에 저장한다 — 로그아웃/비밀번호 변경 시
	// 이 row만 지우면 즉시 무효화할 수 있고(JWT와 달리 서버가 상태를 들고 있음),
	// 여러 대의 WAS로 이중화해도 같은 DB를 보므로 별도 처리 없이 그대로 동작한다.
	private String issueSession(String adminId, String adminRole, String bizRegNo) {
		byte[] randomBytes = new byte[32];
		secureRandom.nextBytes(randomBytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
		LocalDateTime now = LocalDateTime.now();
		adminSessionRepository.save(AdminSession.builder()
				.token(token)
				.adminId(adminId)
				.adminRole(adminRole)
				.bizRegNo(bizRegNo)
				.issuedDt(now)
				.expiresDt(now.plusHours(SESSION_TTL_HOURS))
				.build());
		return token;
	}

	public List<AdminUsrResponse> getUsersByBiz(String bizRegNo) {
		return adminUsrRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo).stream()
				.map(AdminUsrResponse::from)
				.toList();
	}

	@Transactional
	public void changePassword(String adminId, PasswordChangeRequest request, CurrentAdmin requester) {
		checkSelfOrSuper(adminId, requester);
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
	public void changeEmployeePassword(String empId, PasswordChangeRequest request, CurrentAdmin requester) {
		checkSelfOrSuper(empId, requester);
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
	public PasswordVerifyResponse verifyPassword(String adminId, PasswordVerifyRequest request, CurrentAdmin requester) {
		checkSelfOrSuper(adminId, requester);
		AdminUsr admin = adminUsrRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("관리자 계정을 찾을 수 없습니다: " + adminId));
		return new PasswordVerifyResponse(passwordEncoder.matches(request.password(), admin.getPasswordHash()));
	}

	public PasswordVerifyResponse verifyEmployeePassword(String empId, PasswordVerifyRequest request, CurrentAdmin requester) {
		checkSelfOrSuper(empId, requester);
		BizEmp emp = bizEmpRepository.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException("직원 계정을 찾을 수 없습니다: " + empId));
		return new PasswordVerifyResponse(passwordEncoder.matches(request.password(), emp.getPasswordHash()));
	}

	// 슈퍼관리자가 아니면 본인 계정의 비밀번호만 변경/확인할 수 있다.
	// requester는 요청 바디가 아니라 AdminAuthInterceptor가 세션 토큰으로 확인한 신원이라 위조할 수 없다.
	private void checkSelfOrSuper(String targetId, CurrentAdmin requester) {
		if (!requester.isSuper() && !requester.adminId().equals(targetId)) {
			throw new BusinessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
	}

	// 새 비밀키를 생성해서 보여주기만 하고 아직 저장하지 않는다 — 등록 화면에서 사용자가
	// 실제로 그 키로 코드를 만들어낼 수 있음을 confirmTotp에서 증명해야 저장된다.
	public TotpSetupResponse setupTotp(CurrentAdmin requester) {
		requireSuper(requester);
		String secret = TotpUtil.generateSecret();
		return new TotpSetupResponse(secret, buildOtpauthUri(requester.adminId(), secret));
	}

	// 구글 OTP 등 인증 앱이 QR로 읽을 수 있는 표준 otpauth:// URI를 만든다.
	private String buildOtpauthUri(String adminId, String secret) {
		String issuer = URLEncoder.encode(TOTP_ISSUER, StandardCharsets.UTF_8);
		String label = URLEncoder.encode(TOTP_ISSUER + ":" + adminId, StandardCharsets.UTF_8).replace("+", "%20");
		return "otpauth://totp/" + label + "?secret=" + secret + "&issuer=" + issuer + "&algorithm=SHA1&digits=6&period=30";
	}

	@Transactional
	public void confirmTotp(CurrentAdmin requester, TotpConfirmRequest request) {
		requireSuper(requester);
		if (!TotpUtil.verifyCode(request.secret(), request.code())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, TOTP_INVALID_MESSAGE);
		}
		AdminUsr admin = adminUsrRepository.findById(requester.adminId())
				.orElseThrow(() -> new ResourceNotFoundException("관리자 계정을 찾을 수 없습니다: " + requester.adminId()));
		admin.setTotpSecret(request.secret());
		adminUsrRepository.save(admin);
	}

	private void requireSuper(CurrentAdmin requester) {
		if (!requester.isSuper()) {
			throw new BusinessException(HttpStatus.FORBIDDEN, SUPER_ONLY_MESSAGE);
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
