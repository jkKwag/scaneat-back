package com.scaneat.back.dto.biz;

import com.scaneat.back.entity.BizMenu;
import java.math.BigDecimal;

public record BizMenuResponse(
		String menuCd,
		String bizCatCd,
		String menuNm,
		String menuDesc,
		BigDecimal price,
		String imgUrl,
		String badge,
		Integer sortOrd
) {
	public static BizMenuResponse from(BizMenu menu) {
		return new BizMenuResponse(
				menu.getMenuCd(),
				menu.getBizCatCd(),
				menu.getMenuNm(),
				menu.getMenuDesc(),
				menu.getPrice(),
				menu.getImgUrl(),
				menu.getBadge(),
				menu.getSortOrd()
		);
	}
}
