package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.common.CmmCdResponse;
import com.scaneat.back.dto.common.CmmGrpCdResponse;
import com.scaneat.back.service.CmmCdService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-code")
@RequiredArgsConstructor
public class CmmCdController {

	private final CmmCdService cmmCdService;

	@GetMapping("/group")
	public ApiResponse<List<CmmGrpCdResponse>> getGroups() {
		return ApiResponse.ok(cmmCdService.getGroups());
	}

	@GetMapping("/{grpCd}")
	public ApiResponse<List<CmmCdResponse>> getCodes(@PathVariable String grpCd) {
		return ApiResponse.ok(cmmCdService.getCodes(grpCd));
	}
}
