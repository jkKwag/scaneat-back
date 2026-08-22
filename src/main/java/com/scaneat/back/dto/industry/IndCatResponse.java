package com.scaneat.back.dto.industry;

import com.scaneat.back.entity.IndCat;

public record IndCatResponse(String catCd, String catNm) {
	public static IndCatResponse from(IndCat indCat) {
		return new IndCatResponse(indCat.getCatCd(), indCat.getCatNm());
	}
}
