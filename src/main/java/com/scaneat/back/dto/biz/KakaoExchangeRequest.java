package com.scaneat.back.dto.biz;

import jakarta.validation.constraints.NotBlank;

public record KakaoExchangeRequest(
		@NotBlank(message = "인가 코드가 없습니다.") String code
) {
}
