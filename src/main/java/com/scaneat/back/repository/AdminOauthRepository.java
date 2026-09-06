package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminOauth;
import com.scaneat.back.entity.AdminOauthId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminOauthRepository extends JpaRepository<AdminOauth, AdminOauthId> {

	// provider는 @EmbeddedId(AdminOauthId) 안에 있는 필드라 "Id_" 프리픽스로 파고들어야 한다.
	Optional<AdminOauth> findById_ProviderAndProviderUserId(String provider, String providerUserId);
}
