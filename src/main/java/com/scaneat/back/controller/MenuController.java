package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.menu.MenuOptionGroupRequest;
import com.scaneat.back.dto.menu.MenuOptionGroupResponse;
import com.scaneat.back.dto.menu.MenuOptionRequest;
import com.scaneat.back.dto.menu.MenuOptionResponse;
import com.scaneat.back.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@GetMapping("/{menuCd}/options")
	public ApiResponse<List<MenuOptionGroupResponse>> getMenuOptions(@PathVariable String menuCd) {
		return ApiResponse.ok(menuService.getMenuOptions(menuCd));
	}

	@PostMapping("/{menuCd}/option-groups")
	public ApiResponse<MenuOptionGroupResponse> createOptionGroup(
			@PathVariable String menuCd, @RequestBody MenuOptionGroupRequest request, HttpServletRequest httpRequest) {
		return ApiResponse.ok(menuService.createOptionGroup(menuCd, request, currentAdmin(httpRequest)));
	}

	@PostMapping("/{menuCd}/option-groups/{optGrpCd}/options")
	public ApiResponse<MenuOptionResponse> addOption(
			@PathVariable String menuCd, @PathVariable String optGrpCd, @RequestBody MenuOptionRequest request,
			HttpServletRequest httpRequest) {
		return ApiResponse.ok(menuService.addOption(menuCd, optGrpCd, request, currentAdmin(httpRequest)));
	}

	@DeleteMapping("/{menuCd}/option-groups/{optGrpCd}/options/{optCd}")
	public ApiResponse<Void> deleteOption(
			@PathVariable String menuCd, @PathVariable String optGrpCd, @PathVariable String optCd,
			HttpServletRequest httpRequest) {
		menuService.deleteOption(menuCd, optGrpCd, optCd, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	@DeleteMapping("/{menuCd}/option-groups/{optGrpCd}")
	public ApiResponse<Void> deleteOptionGroup(
			@PathVariable String menuCd, @PathVariable String optGrpCd, HttpServletRequest httpRequest) {
		menuService.deleteOptionGroup(menuCd, optGrpCd, currentAdmin(httpRequest));
		return ApiResponse.ok(null);
	}

	private CurrentAdmin currentAdmin(HttpServletRequest request) {
		return (CurrentAdmin) request.getAttribute(CurrentAdmin.REQUEST_ATTR);
	}
}
