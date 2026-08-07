package com.scaneat.back.dto.biz;

// 테스트용 "사업장 전체 데이터 삭제" 결과 요약 — 실제로 몇 건씩 지워졌는지 확인용.
public record BizWipeResponse(
		int adminUsrCount,
		int menuCount,
		int orderCount,
		int paymentCount,
		int reservationCount
) {
}
