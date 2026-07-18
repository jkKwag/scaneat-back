package com.scaneat.back.dto.biz;

import java.util.List;

public record BizPageResponse(
		List<BizResponse> items,
		boolean hasMore
) {
}
