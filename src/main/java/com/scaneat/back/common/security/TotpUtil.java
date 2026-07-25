package com.scaneat.back.common.security;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// RFC 6238(TOTP) 구현 — 구글 OTP/Authy 같은 인증 앱과 호환되도록 외부 라이브러리 없이
// HMAC-SHA1 + Base32만으로 직접 구현했다(둘 다 JDK 표준 API만으로 가능).
public final class TotpUtil {

	private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	private static final int SECRET_BYTES = 20; // 160비트
	private static final int TIME_STEP_SECONDS = 30;
	private static final int CODE_DIGITS = 6;
	private static final int ALLOWED_DRIFT_STEPS = 1; // 기기 시간 오차 ±30초 허용

	private TotpUtil() {
	}

	public static String generateSecret() {
		byte[] bytes = new byte[SECRET_BYTES];
		new SecureRandom().nextBytes(bytes);
		return base32Encode(bytes);
	}

	public static boolean verifyCode(String base32Secret, String code) {
		if (base32Secret == null || code == null || !code.matches("\\d{6}")) {
			return false;
		}
		byte[] key = base32Decode(base32Secret);
		long currentStep = System.currentTimeMillis() / 1000L / TIME_STEP_SECONDS;
		for (int drift = -ALLOWED_DRIFT_STEPS; drift <= ALLOWED_DRIFT_STEPS; drift++) {
			if (code.equals(generateCode(key, currentStep + drift))) {
				return true;
			}
		}
		return false;
	}

	private static String generateCode(byte[] key, long timeStep) {
		try {
			byte[] data = new byte[8];
			long value = timeStep;
			for (int i = 7; i >= 0; i--) {
				data[i] = (byte) (value & 0xFF);
				value >>= 8;
			}
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			byte[] hash = mac.doFinal(data);
			int offset = hash[hash.length - 1] & 0x0F;
			int binary = ((hash[offset] & 0x7F) << 24)
					| ((hash[offset + 1] & 0xFF) << 16)
					| ((hash[offset + 2] & 0xFF) << 8)
					| (hash[offset + 3] & 0xFF);
			int otp = binary % (int) Math.pow(10, CODE_DIGITS);
			return String.format(Locale.ROOT, "%0" + CODE_DIGITS + "d", otp);
		} catch (Exception e) {
			throw new IllegalStateException("TOTP 코드 생성 실패", e);
		}
	}

	private static String base32Encode(byte[] data) {
		StringBuilder sb = new StringBuilder();
		int bits = 0;
		int value = 0;
		for (byte b : data) {
			value = (value << 8) | (b & 0xFF);
			bits += 8;
			while (bits >= 5) {
				sb.append(BASE32_ALPHABET.charAt((value >> (bits - 5)) & 0x1F));
				bits -= 5;
			}
		}
		if (bits > 0) {
			sb.append(BASE32_ALPHABET.charAt((value << (5 - bits)) & 0x1F));
		}
		return sb.toString();
	}

	private static byte[] base32Decode(String base32) {
		String clean = base32.trim().toUpperCase(Locale.ROOT).replace("=", "");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int bits = 0;
		int value = 0;
		for (char c : clean.toCharArray()) {
			int idx = BASE32_ALPHABET.indexOf(c);
			if (idx < 0) continue;
			value = (value << 5) | idx;
			bits += 5;
			if (bits >= 8) {
				out.write((value >> (bits - 8)) & 0xFF);
				bits -= 8;
			}
		}
		return out.toByteArray();
	}
}
