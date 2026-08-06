package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.biz.BizApprovalResponse;
import com.scaneat.back.dto.biz.BizCatRequest;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizCertExtractResult;
import com.scaneat.back.dto.biz.BizCertUploadResponse;
import com.scaneat.back.dto.biz.BizEmpResponse;
import com.scaneat.back.dto.biz.BizCreateRequest;
import com.scaneat.back.dto.biz.BizRejectRequest;
import com.scaneat.back.dto.biz.BizSignupRequest;
import com.scaneat.back.dto.biz.BizSignupResponse;
import com.scaneat.back.dto.biz.BizUpdateRequest;
import com.scaneat.back.dto.biz.EmailCodeSendRequest;
import com.scaneat.back.dto.biz.EmailCodeVerifyRequest;
import com.scaneat.back.dto.biz.BizHourRequest;
import com.scaneat.back.dto.biz.BizHourResponse;
import com.scaneat.back.dto.biz.BizMenuRequest;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizNtsStatusResponse;
import com.scaneat.back.dto.biz.BizPageResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.dto.biz.BizRsvnStdRequest;
import com.scaneat.back.dto.biz.BizRsvnStdResponse;
import com.scaneat.back.dto.biz.BizSeatRequest;
import com.scaneat.back.dto.biz.BizSeatResponse;
import com.scaneat.back.dto.biz.ImageUploadResponse;
import com.scaneat.back.service.BizService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/biz")
@RequiredArgsConstructor
public class BizController {

	private final BizService bizService;

	@GetMapping
	public ApiResponse<BizPageResponse> getBizPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.ok(bizService.getBizPage(page, size));
	}

	@GetMapping("/{bizno}")
	public ApiResponse<BizResponse> getBiz(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getBiz(bizno));
	}

	@PostMapping
	public ApiResponse<BizResponse> createBiz(@RequestBody BizCreateRequest request) {
		return ApiResponse.ok(bizService.createBiz(request));
	}

	// 가입 폼에 이메일 입력 직후 인증코드 발송 — 아직 계정이 없으므로 공개 API
	@PostMapping("/signup/email-code")
	public ApiResponse<Void> sendEmailCode(@Valid @RequestBody EmailCodeSendRequest request) {
		bizService.sendEmailCode(request);
		return ApiResponse.ok(null);
	}

	@PostMapping("/signup/email-code/verify")
	public ApiResponse<Void> verifyEmailCode(@Valid @RequestBody EmailCodeVerifyRequest request) {
		bizService.verifyEmailCode(request);
		return ApiResponse.ok(null);
	}

	// 손님/사업자가 직접 신청하는 셀프 가입 — 로그인 없이 열려있는 공개 API (WebConfig에서 제외 처리)
	@PostMapping("/signup")
	public ApiResponse<BizSignupResponse> signup(@Valid @RequestBody BizSignupRequest request) {
		return ApiResponse.ok(bizService.signup(request));
	}

	// 로그인 후(본인 또는 SUPER) 사업자등록증 업로드/재업로드 — 소유권 확인은 AdminAuthInterceptor가 처리
	@PostMapping(value = "/{bizno}/registration-cert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<BizCertUploadResponse> uploadRegistrationCert(@PathVariable String bizno, @RequestParam("file") MultipartFile file) {
		return ApiResponse.ok(bizService.uploadRegistrationCert(bizno, file));
	}

	@GetMapping("/{bizno}/registration-cert")
	public ApiResponse<String> getRegistrationCertUrl(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getRegistrationCertUrl(bizno));
	}

	// 사업자등록증 업로드 직후 등 필요할 때 국세청 상태조회(계속사업자/휴업자/폐업자)를 재확인
	@PostMapping("/{bizno}/nts-status")
	public ApiResponse<BizNtsStatusResponse> checkNtsStatus(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.checkNtsStatus(bizno));
	}

	// 업로드 완료 후 프론트가 이어서 호출 — 업로드 응답과 분리해서 인식이 오래 걸려도 업로드 자체엔 영향 없게 한다.
	@PostMapping(value = "/{bizno}/registration-cert/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<BizCertExtractResult> extractCertInfo(@PathVariable String bizno, @RequestParam("file") MultipartFile file) {
		return ApiResponse.ok(bizService.extractCertInfo(bizno, file));
	}

	// SUPER 전용 — 승인 대기 중인 가입건 목록
	@GetMapping("/approvals")
	public ApiResponse<List<BizApprovalResponse>> getPendingApprovals(HttpServletRequest httpRequest) {
		return ApiResponse.ok(bizService.getPendingApprovals(currentAdmin(httpRequest)));
	}

	@PutMapping("/{bizno}/approve")
	public ApiResponse<Void> approveBiz(@PathVariable String bizno, HttpServletRequest httpRequest) {
		bizService.approveBiz(bizno, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	@PutMapping("/{bizno}/reject")
	public ApiResponse<Void> rejectBiz(
			@PathVariable String bizno, @Valid @RequestBody BizRejectRequest request, HttpServletRequest httpRequest) {
		bizService.rejectBiz(bizno, request, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	@PutMapping("/{bizno}")
	public ApiResponse<BizResponse> updateBiz(@PathVariable String bizno, @RequestBody BizUpdateRequest request) {
		return ApiResponse.ok(bizService.updateBiz(bizno, request));
	}

	@GetMapping("/{bizno}/categories")
	public ApiResponse<List<BizCatResponse>> getCategories(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getCategories(bizno));
	}

	@PostMapping("/{bizno}/categories")
	public ApiResponse<BizCatResponse> createCategory(@PathVariable String bizno, @RequestBody BizCatRequest request) {
		return ApiResponse.ok(bizService.createCategory(bizno, request));
	}

	@PutMapping("/{bizno}/categories/{bizCatCd}")
	public ApiResponse<BizCatResponse> updateCategory(
			@PathVariable String bizno, @PathVariable String bizCatCd, @RequestBody BizCatRequest request) {
		return ApiResponse.ok(bizService.updateCategory(bizno, bizCatCd, request));
	}

	@DeleteMapping("/{bizno}/categories/{bizCatCd}")
	public ApiResponse<Void> deleteCategory(@PathVariable String bizno, @PathVariable String bizCatCd) {
		bizService.deleteCategory(bizno, bizCatCd);
		return ApiResponse.ok(null);
	}

	@GetMapping("/{bizno}/menus")
	public ApiResponse<List<BizMenuResponse>> getMenus(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getMenus(bizno));
	}

	@PostMapping("/{bizno}/menus")
	public ApiResponse<BizMenuResponse> createMenu(@PathVariable String bizno, @RequestBody BizMenuRequest request) {
		return ApiResponse.ok(bizService.createMenu(bizno, request));
	}

	@PutMapping("/{bizno}/menus/{menuCd}")
	public ApiResponse<BizMenuResponse> updateMenu(
			@PathVariable String bizno, @PathVariable String menuCd, @RequestBody BizMenuRequest request) {
		return ApiResponse.ok(bizService.updateMenu(bizno, menuCd, request));
	}

	@DeleteMapping("/{bizno}/menus/{menuCd}")
	public ApiResponse<Void> deleteMenu(@PathVariable String bizno, @PathVariable String menuCd) {
		bizService.deleteMenu(bizno, menuCd);
		return ApiResponse.ok(null);
	}

	@GetMapping("/{bizno}/hours")
	public ApiResponse<List<BizHourResponse>> getHours(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getHours(bizno));
	}

	@PutMapping("/{bizno}/hours")
	public ApiResponse<List<BizHourResponse>> saveHours(@PathVariable String bizno, @Valid @RequestBody BizHourRequest request) {
		return ApiResponse.ok(bizService.saveHours(bizno, request));
	}

	@GetMapping("/{bizno}/reservation-standard")
	public ApiResponse<BizRsvnStdResponse> getRsvnStd(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getRsvnStd(bizno));
	}

	@PutMapping("/{bizno}/reservation-standard")
	public ApiResponse<BizRsvnStdResponse> saveRsvnStd(@PathVariable String bizno, @RequestBody BizRsvnStdRequest request) {
		return ApiResponse.ok(bizService.saveRsvnStd(bizno, request));
	}

	@GetMapping("/{bizno}/seats")
	public ApiResponse<List<BizSeatResponse>> getSeats(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getSeats(bizno));
	}

	@GetMapping("/{bizno}/seats/admin")
	public ApiResponse<List<BizSeatResponse>> getSeatsForAdmin(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getSeatsForAdmin(bizno));
	}

	@PostMapping("/{bizno}/seats")
	public ApiResponse<BizSeatResponse> createSeat(@PathVariable String bizno, @RequestBody BizSeatRequest request) {
		return ApiResponse.ok(bizService.createSeat(bizno, request));
	}

	@PutMapping("/{bizno}/seats/{seatCd}")
	public ApiResponse<BizSeatResponse> updateSeat(
			@PathVariable String bizno, @PathVariable String seatCd, @RequestBody BizSeatRequest request) {
		return ApiResponse.ok(bizService.updateSeat(bizno, seatCd, request));
	}

	@DeleteMapping("/{bizno}/seats/{seatCd}")
	public ApiResponse<Void> deleteSeat(@PathVariable String bizno, @PathVariable String seatCd) {
		bizService.deleteSeat(bizno, seatCd);
		return ApiResponse.ok(null);
	}

	@GetMapping("/{bizno}/employees")
	public ApiResponse<List<BizEmpResponse>> getEmployees(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getEmployees(bizno));
	}

	@PostMapping(value = "/{bizno}/menu-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<ImageUploadResponse> uploadMenuImage(@PathVariable String bizno, @RequestParam("file") MultipartFile file) {
		return ApiResponse.ok(bizService.uploadMenuImage(bizno, file));
	}

	@PostMapping(value = "/{bizno}/seat-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<ImageUploadResponse> uploadSeatImage(@PathVariable String bizno, @RequestParam("file") MultipartFile file) {
		return ApiResponse.ok(bizService.uploadSeatImage(bizno, file));
	}

	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
