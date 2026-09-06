package com.scaneat.back.service;

import com.scaneat.back.client.KakaoClient;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.dto.biz.KakaoExchangeResponse;
import com.scaneat.back.dto.biz.KakaoSignupRequest;
import com.scaneat.back.entity.AdminOauth;
import com.scaneat.back.entity.AdminOauthId;
import com.scaneat.back.entity.AdminRole;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.repository.AdminOauthRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 카카오 로그인 한 버튼으로 "로그인"과 "회원가입"을 함께 처리한다 — 이미 연동된 카카오 계정이면
// 바로 로그인, 처음 보는 카카오 계정이면 신원만 확인해두고 사업자 정보 입력을 마저 받는다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService {

	private static final String PROVIDER = "KAKAO";

	private final KakaoClient kakaoClient;
	private final AdminOauthRepository adminOauthRepository;
	private final BizService bizService;
	private final AdminService adminService;
	private final OauthPendingSignupStore pendingSignupStore;

	@Transactional
	public KakaoExchangeResponse exchange(String code) {
		String accessToken = kakaoClient.exchangeCodeForAccessToken(code);
		KakaoClient.KakaoUserInfo userInfo = kakaoClient.getUserInfo(accessToken);

		return adminOauthRepository.findById_ProviderAndProviderUserId(PROVIDER, userInfo.providerUserId())
				.map(oauth -> KakaoExchangeResponse.loggedIn(
						adminService.issueSessionByAdminNo(oauth.getId().getAdminNo(), oauth.getId().getAdminType())))
				.orElseGet(() -> {
					String signupToken = pendingSignupStore.put(PROVIDER, userInfo.providerUserId(), userInfo.nickname(), userInfo.email());
					return KakaoExchangeResponse.needsSignup(signupToken, userInfo.nickname(), userInfo.email());
				});
	}

	@Transactional
	public AdminLoginResponse signup(KakaoSignupRequest request) {
		OauthPendingSignupStore.Entry pending = pendingSignupStore.consume(request.signupToken());
		if (pending == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "카카오 인증이 만료되었습니다. 다시 시도해주세요.");
		}

		AdminUsr admin = bizService.signupForOauth(request);
		adminOauthRepository.save(AdminOauth.builder()
				.id(new AdminOauthId(admin.getAdminNo(), AdminRole.PROV_ADMIN.name(), pending.provider()))
				.providerUserId(pending.providerUserId())
				.regDt(LocalDateTime.now())
				.build());

		return adminService.issueSessionByAdminNo(admin.getAdminNo(), AdminRole.PROV_ADMIN.name());
	}
}
