package com.scaneat.back.repository;

import com.scaneat.back.entity.EmailVerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyCodeRepository extends JpaRepository<EmailVerifyCode, String> {
}
