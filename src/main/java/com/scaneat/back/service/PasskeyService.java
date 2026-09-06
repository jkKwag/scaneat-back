package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.config.WebAuthnConfig;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.admin.PasskeyDeviceResponse;
import com.scaneat.back.dto.admin.PasskeyLoginOptionsRequest;
import com.scaneat.back.dto.admin.PasskeyLoginOptionsResponse;
import com.scaneat.back.dto.admin.PasskeyLoginRequest;
import com.scaneat.back.dto.admin.PasskeyRegisterOptionsResponse;
import com.scaneat.back.dto.admin.PasskeyRegisterOptionsResponse.CredentialDescriptor;
import com.scaneat.back.dto.admin.PasskeyRegisterOptionsResponse.PubKeyCredParam;
import com.scaneat.back.dto.admin.PasskeyRegisterRequest;
import com.scaneat.back.entity.AdminPasskey;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.BizEmp;
import com.scaneat.back.repository.AdminPasskeyRepository;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.util.exception.WebAuthnException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 슈퍼관리자를 제외한 관리자/직원 계정의 패스키(지문/생체인증) 등록·로그인. WebAuthn 표준을 그대로
// 쓰기 때문에 등록/로그인 검증 로직은 웹이든(추후) 앱이든 동일하게 재사용된다 — platform 필드로만
// 어디서 등록했는지 구분해서 기기 목록에 표시한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasskeyService {

	private static final long CEREMONY_TIMEOUT_MILLIS = 60_000;
	private static final List<PublicKeyCredentialParameters> PUB_KEY_CRED_PARAMS = List.of(
			new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
			new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
	);

	private final AdminUsrRepository adminUsrRepository;
	private final BizEmpRepository bizEmpRepository;
	private final AdminPasskeyRepository adminPasskeyRepository;
	private final AdminService adminService;
	private final WebAuthnChallengeStore challengeStore;
	private final WebAuthnConfig webAuthnConfig;
	private final WebAuthnManager webAuthnManager;
	private final AttestedCredentialDataConverter attestedCredentialDataConverter;
	private final SecureRandom secureRandom = new SecureRandom();

	// ── 등록 ──────────────────────────────────────────────────────────────────

	public PasskeyRegisterOptionsResponse registerOptions(CurrentAdmin requester) {
		requireNotSuper(requester);
		String adminNo = requester.adminNo();
		String adminType = requester.adminRole();

		byte[] challenge = randomBytes(32);
		challengeStore.putRegistration(adminNo, adminType, challenge);

		String label = resolveLabel(adminNo, adminType);
		List<CredentialDescriptor> excludeCredentials = adminPasskeyRepository
				.findByAdminNoAndAdminTypeOrderByRegDtDesc(adminNo, adminType).stream()
				.map(p -> new CredentialDescriptor("public-key", p.getCredId()))
				.toList();

		return new PasskeyRegisterOptionsResponse(
				webAuthnConfig.getRpId(),
				webAuthnConfig.getRpName(),
				Base64UrlUtil.encodeToString(challenge),
				Base64UrlUtil.encodeToString(adminNo.getBytes(StandardCharsets.UTF_8)),
				label,
				label,
				PUB_KEY_CRED_PARAMS.stream().map(p -> new PubKeyCredParam("public-key", (int) p.getAlg().getValue())).toList(),
				excludeCredentials,
				CEREMONY_TIMEOUT_MILLIS
		);
	}

	@Transactional
	public PasskeyDeviceResponse register(CurrentAdmin requester, PasskeyRegisterRequest request) {
		requireNotSuper(requester);
		String adminNo = requester.adminNo();
		String adminType = requester.adminRole();

		WebAuthnChallengeStore.Entry entry = challengeStore.consumeRegistration(adminNo, adminType);
		if (entry == null) {
			throw new BusinessException("등록 요청이 만료되었습니다. 다시 시도해주세요.");
		}

		ServerProperty serverProperty = new ServerProperty(
				Origin.create(webAuthnConfig.getOrigin()), webAuthnConfig.getRpId(), new DefaultChallenge(entry.challenge()));
		RegistrationParameters params = new RegistrationParameters(serverProperty, PUB_KEY_CRED_PARAMS, true, true);

		RegistrationData registrationData;
		try {
			registrationData = webAuthnManager.verifyRegistrationResponseJSON(request.credentialJson(), params);
		} catch (WebAuthnException e) {
			throw new BusinessException("패스키 등록 검증에 실패했습니다: " + e.getMessage());
		}

		AttestedCredentialData attestedCredentialData = registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData();
		String credId = Base64UrlUtil.encodeToString(attestedCredentialData.getCredentialId());
		if (adminPasskeyRepository.existsById(credId)) {
			throw new BusinessException("이미 등록된 기기입니다.");
		}

		String transports = registrationData.getTransports() == null ? null
				: registrationData.getTransports().stream().map(AuthenticatorTransport::getValue).collect(Collectors.joining(","));

		AdminPasskey passkey = AdminPasskey.builder()
				.credId(credId)
				.adminNo(adminNo)
				.adminType(adminType)
				.publicKey(Base64.getEncoder().encodeToString(attestedCredentialDataConverter.convert(attestedCredentialData)))
				.signCount(registrationData.getAttestationObject().getAuthenticatorData().getSignCount())
				.platform(request.platform())
				.transports(transports)
				.backedUp(registrationData.getAttestationObject().getAuthenticatorData().isFlagBE() ? "Y" : "N")
				.deviceLabel(request.deviceLabel())
				.regDt(LocalDateTime.now())
				.build();
		adminPasskeyRepository.save(passkey);
		return PasskeyDeviceResponse.from(passkey);
	}

	public List<PasskeyDeviceResponse> listDevices(CurrentAdmin requester) {
		return adminPasskeyRepository.findByAdminNoAndAdminTypeOrderByRegDtDesc(requester.adminNo(), requester.adminRole()).stream()
				.map(PasskeyDeviceResponse::from)
				.toList();
	}

	@Transactional
	public void deleteDevice(CurrentAdmin requester, String credId) {
		AdminPasskey passkey = adminPasskeyRepository.findById(credId)
				.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "등록된 기기를 찾을 수 없습니다."));
		if (!passkey.getAdminNo().equals(requester.adminNo()) || !passkey.getAdminType().equals(requester.adminRole())) {
			throw new BusinessException(HttpStatus.FORBIDDEN, "본인이 등록한 기기만 삭제할 수 있습니다.");
		}
		adminPasskeyRepository.delete(passkey);
	}

	// ── 로그인 ──────────────────────────────────────────────────────────────────

	public PasskeyLoginOptionsResponse loginOptions(PasskeyLoginOptionsRequest request) {
		String normalizedAdminId = request.adminId().trim().toLowerCase();
		String adminNo;
		String adminType;
		Optional<AdminUsr> admin = adminUsrRepository.findByAdminIdAndUseYn(normalizedAdminId, "Y");
		if (admin.isPresent()) {
			adminNo = admin.get().getAdminNo();
			adminType = admin.get().getAdminRole().name();
		} else {
			Optional<BizEmp> emp = bizEmpRepository.findByEmpIdAndUseYn(normalizedAdminId, "Y");
			if (emp.isEmpty()) {
				return PasskeyLoginOptionsResponse.none();
			}
			adminNo = emp.get().getEmpNo();
			adminType = "EMPLOYEE";
		}

		List<AdminPasskey> passkeys = adminPasskeyRepository.findByAdminNoAndAdminTypeOrderByRegDtDesc(adminNo, adminType);
		if (passkeys.isEmpty()) {
			return PasskeyLoginOptionsResponse.none();
		}

		byte[] challenge = randomBytes(32);
		String flowId = challengeStore.putLogin(adminNo, adminType, challenge);
		List<CredentialDescriptor> allowCredentials = passkeys.stream()
				.map(p -> new CredentialDescriptor("public-key", p.getCredId()))
				.toList();

		return new PasskeyLoginOptionsResponse(
				true, flowId, webAuthnConfig.getRpId(), Base64UrlUtil.encodeToString(challenge), allowCredentials, CEREMONY_TIMEOUT_MILLIS);
	}

	@Transactional
	public AdminLoginResponse login(PasskeyLoginRequest request) {
		WebAuthnChallengeStore.Entry entry = challengeStore.consumeLogin(request.flowId());
		if (entry == null) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "인증 요청이 만료되었습니다. 다시 시도해주세요.");
		}

		AuthenticationData parsedData;
		try {
			parsedData = webAuthnManager.parseAuthenticationResponseJSON(request.credentialJson());
		} catch (WebAuthnException e) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "패스키 인증에 실패했습니다.");
		}

		String credId = Base64UrlUtil.encodeToString(parsedData.getCredentialId());
		AdminPasskey stored = adminPasskeyRepository.findById(credId)
				.orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "등록되지 않은 기기입니다."));
		if (!stored.getAdminNo().equals(entry.adminNo()) || !stored.getAdminType().equals(entry.adminType())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "패스키 인증에 실패했습니다.");
		}

		AttestedCredentialData attestedCredentialData = attestedCredentialDataConverter.convert(Base64.getDecoder().decode(stored.getPublicKey()));
		Authenticator authenticator = new AuthenticatorImpl(attestedCredentialData, new NoneAttestationStatement(), stored.getSignCount());
		ServerProperty serverProperty = new ServerProperty(
				Origin.create(webAuthnConfig.getOrigin()), webAuthnConfig.getRpId(), new DefaultChallenge(entry.challenge()));
		AuthenticationParameters params = new AuthenticationParameters(serverProperty, authenticator, null, true);

		AuthenticationData verified;
		try {
			verified = webAuthnManager.verify(parsedData, params);
		} catch (WebAuthnException e) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "패스키 인증에 실패했습니다.");
		}

		stored.setSignCount(verified.getAuthenticatorData().getSignCount());
		stored.setLastUsedDt(LocalDateTime.now());
		adminPasskeyRepository.save(stored);

		return adminService.issueSessionForPasskeyLogin(stored.getAdminNo(), stored.getAdminType());
	}

	// ── 공통 ──────────────────────────────────────────────────────────────────

	private void requireNotSuper(CurrentAdmin requester) {
		if (requester.isSuper()) {
			throw new BusinessException(HttpStatus.FORBIDDEN, "슈퍼관리자는 패스키를 등록할 수 없습니다.");
		}
	}

	// OS 생체인증 프롬프트/브라우저 비밀번호 관리자에 표시되는 라벨 — 이메일이 있으면 이메일, 없으면 계정번호.
	private String resolveLabel(String adminNo, String adminType) {
		if ("EMPLOYEE".equals(adminType)) {
			return bizEmpRepository.findById(adminNo).map(BizEmp::getEmpId).orElse(adminNo);
		}
		return adminUsrRepository.findById(adminNo).map(AdminUsr::getAdminId).orElse(adminNo);
	}

	private byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		secureRandom.nextBytes(bytes);
		return bytes;
	}
}
