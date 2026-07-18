package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizHourRequest;
import com.scaneat.back.dto.biz.BizHourResponse;
import com.scaneat.back.dto.biz.BizMenuRequest;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizPageResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.dto.biz.BizRsvnStdRequest;
import com.scaneat.back.dto.biz.BizRsvnStdResponse;
import com.scaneat.back.dto.biz.BizSeatResponse;
import com.scaneat.back.service.BizService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/{bizno}/categories")
	public ApiResponse<List<BizCatResponse>> getCategories(@PathVariable String bizno) {
		return ApiResponse.ok(bizService.getCategories(bizno));
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
}
