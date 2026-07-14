package com.scaneat.back.dto.menu;

import com.scaneat.back.entity.BizMenuOptGrp;
import java.util.List;

public record MenuOptionGroupResponse(
		String optGrpCd,
		String optGrpNm,
		String requiredYn,
		Integer minSelCnt,
		Integer maxSelCnt,
		Integer sortOrd,
		List<MenuOptionResponse> options
) {
	public static MenuOptionGroupResponse from(BizMenuOptGrp grp, List<MenuOptionResponse> options) {
		return new MenuOptionGroupResponse(
				grp.getOptGrpCd(),
				grp.getOptGrpNm(),
				grp.getRequiredYn(),
				grp.getMinSelCnt(),
				grp.getMaxSelCnt(),
				grp.getSortOrd(),
				options
		);
	}
}
