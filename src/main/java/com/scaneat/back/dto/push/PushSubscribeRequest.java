package com.scaneat.back.dto.push;

// 브라우저의 PushSubscription.toJSON() 결과와 동일한 형태 — { endpoint, keys: { p256dh, auth } }
public record PushSubscribeRequest(String endpoint, Keys keys) {
	public record Keys(String p256dh, String auth) {
	}
}
