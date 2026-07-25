package com.scaneat.back.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaneat.back.common.security.AdminAuthInterceptor;
import com.scaneat.back.repository.AdminSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.cors.allowed-origins}")
	private String allowedOrigins;

	private final AdminSessionRepository adminSessionRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigins.split(","))
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}

	// 관리자/직원 전용 API에만 세션 토큰 검증을 건다 — 고객이 QR로 보는 메뉴판/주문/결제
	// 흐름은 그대로 인증 없이 열어둔다 (AdminAuthInterceptor 클래스 주석 참고).
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AdminAuthInterceptor(adminSessionRepository, objectMapper))
				.addPathPatterns(
						"/api/admin/**",
						"/api/dashboard/**",
						"/api/order/biz/**",
						"/api/payment/biz/**",
						"/api/reservation/biz/**",
						"/api/menu/*/option-groups/**",
						"/api/biz",
						"/api/biz/**")
				.excludePathPatterns(
						"/api/admin/login",
						"/api/reservation/biz/*/availability");
	}
}
