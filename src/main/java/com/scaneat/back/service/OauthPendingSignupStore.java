package com.scaneat.back.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 소셜 로그인(카카오 등)으로 신원은 확인됐지만 아직 사업자 정보를 안 받아서 계정을 못 만든 상태를
// 잠깐(10분) 들고 있는 저장소. WAS가 한 대뿐이라 DB 테이블 없이 메모리로 충분하다.
@Component
public class OauthPendingSignupStore {

	private static final long TTL_MILLIS = 10 * 60 * 1000;

	// accessToken은 가입 완료 직후 "카카오톡 나에게 보내기"로 가입완료 메시지를 보낼 때 쓴다 —
	// 이 시점이 지나면(가입 폼 작성하는 동안) 다시 구할 방법이 없어서 미리 같이 들고 있어야 한다.
	public record Entry(String provider, String providerUserId, String nickname, String email, String accessToken, long expiresAtEpochMilli) {
		boolean isExpired() {
			return Instant.now().toEpochMilli() > expiresAtEpochMilli;
		}
	}

	private final Map<String, Entry> entries = new ConcurrentHashMap<>();

	public String put(String provider, String providerUserId, String nickname, String email, String accessToken) {
		String token = UUID.randomUUID().toString();
		entries.put(token, new Entry(provider, providerUserId, nickname, email, accessToken, Instant.now().toEpochMilli() + TTL_MILLIS));
		return token;
	}

	public Entry consume(String token) {
		Entry entry = entries.remove(token);
		if (entry == null || entry.isExpired()) {
			return null;
		}
		return entry;
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000)
	void evictExpired() {
		entries.values().removeIf(Entry::isExpired);
	}
}
