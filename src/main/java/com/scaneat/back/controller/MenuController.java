package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.menu.MenuOptionGroupResponse;
import com.scaneat.back.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
