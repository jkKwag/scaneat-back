package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSessionRepository extends JpaRepository<AdminSession, String> {

	Optional<AdminSession> findByTokenAndExpiresDtAfter(String token, LocalDateTime now);

	List<AdminSession> findByBizRegNo(String bizRegNo);
}
