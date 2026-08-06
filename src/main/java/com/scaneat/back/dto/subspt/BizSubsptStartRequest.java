package com.scaneat.back.dto.subspt;

import jakarta.validation.constraints.NotBlank;

// 프론트에서 토스페이먼츠 카드 등록(빌링 인증) 위젯을 완료하면 받는 authKey/customerKey를 그대로 전달받는다.
public record BizSubsptStartRequest(
		@NotBlank(message = "planCd는 필수입니다.") String planCd,
		@NotBlank(message = "authKey는 필수입니다.") String authKey,
		@NotBlank(message = "customerKey는 필수입니다.") String customerKey
) {
}
