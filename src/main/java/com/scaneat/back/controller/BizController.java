package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.biz.BizCatResponse;
import com.scaneat.back.dto.biz.BizMenuResponse;
import com.scaneat.back.dto.biz.BizResponse;
import com.scaneat.back.service.BizService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biz")
@RequiredArgsConstructor
public class BizController {

	private final BizService bizService;

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
}
