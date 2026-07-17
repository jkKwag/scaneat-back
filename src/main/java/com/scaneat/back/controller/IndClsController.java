package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.industry.IndClsResponse;
import com.scaneat.back.service.IndClsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/industry")
@RequiredArgsConstructor
public class IndClsController {

	private final IndClsService indClsService;

	@GetMapping
	public ApiResponse<List<IndClsResponse>> getAll() {
		return ApiResponse.ok(indClsService.getAll());
	}

	@GetMapping("/{indCd}")
	public ApiResponse<IndClsResponse> getByCode(@PathVariable String indCd) {
		return ApiResponse.ok(indClsService.getByCode(indCd));
	}
}
