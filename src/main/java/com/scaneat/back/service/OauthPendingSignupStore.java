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

	public record Entry(String provider, String providerUserId, String nickname, String email, long expiresAtEpochMilli) {
		boolean isExpired() {
			return Instant.now().toEpochMilli() > expiresAtEpochMilli;
		}
	}

	private final Map<String, Entry> entries = new ConcurrentHashMap<>();

	public String put(String provider, String providerUserId, String nickname, String email) {
		String token = UUID.randomUUID().toString();
		entries.put(token, new Entry(provider, providerUserId, nickname, email, Instant.now().toEpochMilli() + TTL_MILLIS));
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
