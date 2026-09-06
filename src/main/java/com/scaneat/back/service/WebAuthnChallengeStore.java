package com.scaneat.back.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 패스키 등록/로그인 챌린지를 검증 단계까지 잠깐(5분) 들고 있는 저장소. WAS가 한 대뿐이라
// DB 테이블 없이 메모리로 충분하고, 서버 재시작 시 진행 중이던 등록/로그인 시도만 만료되면 그만이다.
// 등록은 로그인된 계정이 요청하는 거라 (admin_no, admin_type) 기준으로 한 번에 하나만 진행 중이면
// 되고, 로그인은 아직 신원이 없어서 서버가 발급한 임의의 flowId로 구분한다.
@Component
public class WebAuthnChallengeStore {

	private static final long TTL_MILLIS = 5 * 60 * 1000;

	public record Entry(byte[] challenge, String adminNo, String adminType, long expiresAtEpochMilli) {
		boolean isExpired() {
			return Instant.now().toEpochMilli() > expiresAtEpochMilli;
		}
	}

	private final Map<String, Entry> entries = new ConcurrentHashMap<>();

	private String registrationKey(String adminNo, String adminType) {
		return "reg:" + adminNo + ":" + adminType;
	}

	public void putRegistration(String adminNo, String adminType, byte[] challenge) {
		entries.put(registrationKey(adminNo, adminType),
				new Entry(challenge, adminNo, adminType, Instant.now().toEpochMilli() + TTL_MILLIS));
	}

	public Entry consumeRegistration(String adminNo, String adminType) {
		return consume(registrationKey(adminNo, adminType));
	}

	public String putLogin(String adminNo, String adminType, byte[] challenge) {
		String flowId = UUID.randomUUID().toString();
		entries.put("login:" + flowId,
				new Entry(challenge, adminNo, adminType, Instant.now().toEpochMilli() + TTL_MILLIS));
		return flowId;
	}

	public Entry consumeLogin(String flowId) {
		return consume("login:" + flowId);
	}

	private Entry consume(String key) {
		Entry entry = entries.remove(key);
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
