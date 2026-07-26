package com.scaneat.back.entity;

public enum AdminRole {
	SUPER,
	BIZ,
	// 셀프 가입 직후 승인 전 상태 — 로그인은 되지만 제한된 메뉴만 보인다. 승인되면 BIZ로 바뀐다.
	PROV_ADMIN
}
