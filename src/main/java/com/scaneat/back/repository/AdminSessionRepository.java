package com.scaneat.back.repository;

import com.scaneat.back.entity.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSessionRepository extends JpaRepository<AdminSession, String> {
}
