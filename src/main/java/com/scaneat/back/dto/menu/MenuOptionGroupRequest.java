package com.scaneat.back.dto.menu;

import java.util.List;

public record MenuOptionGroupRequest(
		String optGrpNm,
		String optType,
		String requiredYn,
		Integer minSelCnt,
		Integer maxSelCnt,
		List<MenuOptionRequest> options
) {
}
