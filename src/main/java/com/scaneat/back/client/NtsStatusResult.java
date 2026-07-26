package com.scaneat.back.client;

// statusCd: 국세청 사업자상태코드(01=계속사업자, 02=휴업자, 03=폐업자) — 확인 못 했으면 null
// errorMessage: API 자체가 실패해서 확인을 못 한 경우의 안내 문구 — 정상 조회(성공/미확인)면 null
public record NtsStatusResult(String statusCd, String errorMessage) {

	public static NtsStatusResult of(String statusCd) {
		return new NtsStatusResult(statusCd, null);
	}

	public static NtsStatusResult error(String errorMessage) {
		return new NtsStatusResult(null, errorMessage);
	}

	public static NtsStatusResult empty() {
		return new NtsStatusResult(null, null);
	}
}
