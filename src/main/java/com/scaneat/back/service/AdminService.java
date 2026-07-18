package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.dto.admin.AdminLoginRequest;
import com.scaneat.back.dto.admin.AdminLoginResponse;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.repository.AdminUsrRepository;
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
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AdminLoginResponse login(AdminLoginRequest request) {
		AdminUsr admin = adminUsrRepository.findByAdminIdAndUseYn(request.adminId(), "Y")
				.orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
		if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
		}
		return AdminLoginResponse.from(admin);
	}
}
