package com.scaneat.back.common.security;

import com.scaneat.back.common.exception.BusinessException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 업체별 PG 시크릿키(tb_biz_pg.secret_key_enc)를 저장/조회할 때 쓰는 양방향 암호화 유틸.
// 비밀번호(bcrypt)와 달리 PG 호출 시점에 원본 키를 그대로 꺼내 써야 하므로 단방향 해시가 아니라
// AES-256-GCM으로 암호화한다. 암호화키는 DB와 분리해서 EC2 환경변수(PG_SECRET_ENC_KEY)로만
// 관리 — DB가 유출돼도 이 값이 없으면 시크릿키를 복원할 수 없다.
@Slf4j
@Component
public class PgSecretCrypto {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int IV_LENGTH_BYTES = 12;
	private static final int TAG_LENGTH_BITS = 128;

	private final SecretKeySpec key;

	// 환경변수가 아직 없어도(로컬 개발, 초기 배포 등) 앱 전체가 기동 실패하지 않게 기본값을 비워둔다 —
	// PG 키 등록/조회를 실제로 시도할 때만 requireKey()에서 에러를 낸다 (webauthn4j 사태 재발 방지).
	public PgSecretCrypto(@Value("${app.pg.encryption-key:}") String base64Key) {
		if (base64Key == null || base64Key.isBlank()) {
			this.key = null;
			log.warn("[PG암호화] PG_SECRET_ENC_KEY가 설정되지 않았습니다. 업체별 PG 키 등록/조회 기능은 동작하지 않습니다.");
			return;
		}
		byte[] decoded = Base64.getDecoder().decode(base64Key);
		this.key = new SecretKeySpec(decoded, "AES");
	}

	public String encrypt(String plainText) {
		requireKey();
		try {
			byte[] iv = new byte[IV_LENGTH_BYTES];
			new SecureRandom().nextBytes(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
			buffer.put(iv).put(cipherText);
			return Base64.getEncoder().encodeToString(buffer.array());
		} catch (GeneralSecurityException ex) {
			throw new BusinessException("PG 시크릿키 암호화에 실패했습니다.");
		}
	}

	public String decrypt(String encoded) {
		requireKey();
		try {
			byte[] raw = Base64.getDecoder().decode(encoded);
			byte[] iv = Arrays.copyOfRange(raw, 0, IV_LENGTH_BYTES);
			byte[] cipherText = Arrays.copyOfRange(raw, IV_LENGTH_BYTES, raw.length);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] plainText = cipher.doFinal(cipherText);
			return new String(plainText, StandardCharsets.UTF_8);
		} catch (GeneralSecurityException ex) {
			throw new BusinessException("PG 시크릿키 복호화에 실패했습니다.");
		}
	}

	private void requireKey() {
		if (key == null) {
			throw new BusinessException("PG 암호화 키가 설정되지 않았습니다. (PG_SECRET_ENC_KEY 환경변수 필요)");
		}
	}
}
