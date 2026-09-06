package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminOauth;
import com.scaneat.back.entity.AdminOauthId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminOauthRepository extends JpaRepository<AdminOauth, AdminOauthId> {

	// provider는 @EmbeddedId(AdminOauthId) 안에 있는 필드라 "Id_" 프리픽스로 파고들어야 한다.
	Optional<AdminOauth> findById_ProviderAndProviderUserId(String provider, String providerUserId);

	// 계정 삭제(BizWipeService 등) 시 연동된 소셜 로그인 정보도 같이 정리해야 고아 row가 안 남는다.
	void deleteById_AdminNo(String adminNo);
}
