package com.scaneat.back.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.entity.AdminSession;
import com.scaneat.back.repository.AdminSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

// 관리자/직원 전용 API에 세션 토큰 검증을 강제한다. WebConfig에서 아래 경로들에만 등록됨:
//   /api/admin/**(로그인 제외), /api/dashboard/**, /api/order/biz/**, /api/payment/biz/**,
//   /api/menu/*/option-groups/**, /api/biz/**(GET은 공개 열람이라 통과, 그 외 메서드/employees·seats/admin은 검증)
// bizRegNo가 경로/쿼리에 있으면 SUPER가 아닌 한 본인 매장 것만 접근 가능하도록 함께 확인한다.
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

	private final AdminSessionRepository adminSessionRepository;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// CORS preflight는 인증 헤더 없이 오므로 그대로 통과시켜야 브라우저의 실제 요청이 막히지 않는다.
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
		if (!requiresAuth(request)) return true;

		String token = extractToken(request);
		if (token == null) return reject(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

		AdminSession session = adminSessionRepository.findByTokenAndExpiresDtAfter(token, LocalDateTime.now()).orElse(null);
		if (session == null) return reject(response, HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인해주세요.");

		if ("SUPER".equals(session.getAdminRole())) {
			request.setAttribute(CurrentAdmin.REQUEST_ATTR,
					new CurrentAdmin(session.getAdminId(), session.getAdminRole(), session.getBizRegNo()));
			return true;
		}

		// 대시보드는 SUPER 전용 — bizRegNo가 경로에 없어 아래 소유권 체크로는 걸러지지 않는다.
		if (request.getRequestURI().startsWith("/api/dashboard/")) {
			return reject(response, HttpStatus.FORBIDDEN, "슈퍼관리자만 접근할 수 있습니다.");
		}

		String bizInPath = extractBizRegNo(request);
		if (bizInPath != null && !bizInPath.equals(session.getBizRegNo())) {
			return reject(response, HttpStatus.FORBIDDEN, "다른 매장의 정보에는 접근할 수 없습니다.");
		}

		request.setAttribute(CurrentAdmin.REQUEST_ATTR,
				new CurrentAdmin(session.getAdminId(), session.getAdminRole(), session.getBizRegNo()));
		return true;
	}

	// /api/biz/**는 조회(GET)는 손님도 QR로 보는 공개 메뉴판이라 그대로 열어두고,
	// 등록/수정/삭제와 employees·seats/admin·seat-status·approvals·registration-cert 조회는 관리자 인증을 요구한다.
	// signup 관련 경로만 아직 로그인이 불가능한 가입 단계라 예외적으로 공개해둔다.
	private boolean requiresAuth(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (uri.startsWith("/api/biz/") || uri.equals("/api/biz")) {
			if (uri.equals("/api/biz/signup") || uri.equals("/api/biz/signup/email-code")
					|| uri.equals("/api/biz/signup/email-code/verify")) {
				return false;
			}
			// 손님이 직원 QR을 스캔해 주문권한을 받거나(POST) 현재 권한 상태를 조회(GET)하는 API —
			// 로그인하지 않은 손님이 호출하므로 메서드에 상관없이 항상 공개해야 한다.
			if (uri.endsWith("/access-grants")) {
				return false;
			}
			if (uri.endsWith("/employees") || uri.endsWith("/seats/admin") || uri.endsWith("/seat-status")
					|| uri.endsWith("/approvals") || uri.endsWith("/registration-cert") || uri.contains("/subscription")) {
				return true;
			}
			return !"GET".equalsIgnoreCase(request.getMethod());
		}
		return true;
	}

	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) return header.substring(7).trim();
		return null;
	}

	@SuppressWarnings("unchecked")
	private String extractBizRegNo(HttpServletRequest request) {
		Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (attr instanceof Map<?, ?> pathVars) {
			Object bizno = ((Map<String, String>) pathVars).get("bizno");
			if (bizno != null) return (String) bizno;
			Object bizRegNo = ((Map<String, String>) pathVars).get("bizRegNo");
			if (bizRegNo != null) return (String) bizRegNo;
		}
		String param = request.getParameter("bizRegNo");
		return param != null ? param : request.getParameter("bizno");
	}

	private boolean reject(HttpServletResponse response, HttpStatus status, String message) throws Exception {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(message)));
		return false;
	}
}
