package com.scaneat.back.dto.subspt;

import jakarta.validation.constraints.NotBlank;

// 프론트에서 토스페이먼츠 카드 등록(빌링 인증) 위젯을 완료하면 받는 authKey/customerKey를 그대로 전달받는다.
// 0원짜리 이벤트 무료 요금제는 카드 등록 자체가 없어 authKey/customerKey가 비어 올 수 있다 —
// 유료 요금제일 때만 서비스 계층에서 필수 여부를 확인한다.
public record BizSubsptStartRequest(
		@NotBlank(message = "planCd는 필수입니다.") String planCd,
		String authKey,
		String customerKey
) {
}
