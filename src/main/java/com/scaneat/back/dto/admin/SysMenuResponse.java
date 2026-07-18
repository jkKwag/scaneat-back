package com.scaneat.back.dto.admin;

import com.scaneat.back.entity.SysMenu;
import java.util.List;

public record SysMenuResponse(
		String menuCd,
		String menuNm,
		String menuUrl,
		List<SysMenuResponse> children
) {
	public static SysMenuResponse from(SysMenu menu, List<SysMenuResponse> children) {
		return new SysMenuResponse(menu.getMenuCd(), menu.getMenuNm(), menu.getMenuUrl(), children);
	}
}
