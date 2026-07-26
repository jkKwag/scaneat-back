package com.scaneat.back.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// AWS SES REST API(v2)를 SDK 없이 호출하기 위한 AWS Signature Version 4 서명 구현.
// --offline gradle 빌드 환경이라 새 의존성(AWS SDK)을 추가할 수 없어서, TotpUtil과 같은 방식으로
// JDK 표준 암호화 API(Mac, MessageDigest)만으로 직접 구현했다.
public final class AwsSigV4Signer {

	private static final String ALGORITHM = "AWS4-HMAC-SHA256";

	private AwsSigV4Signer() {
	}

	// content-type/host/x-amz-date 세 헤더만 서명 대상으로 쓴다 — 실제 요청에도 이 세 헤더만 보낸다.
	public static String authorizationHeader(String accessKey, String secretKey, String region, String service,
			String method, String host, String path, String payload, String amzDate) {
		String dateStamp = amzDate.substring(0, 8);

		TreeMap<String, String> headers = new TreeMap<>();
		headers.put("content-type", "application/json");
		headers.put("host", host);
		headers.put("x-amz-date", amzDate);

		StringBuilder canonicalHeaders = new StringBuilder();
		StringBuilder signedHeaders = new StringBuilder();
		for (var entry : headers.entrySet()) {
			canonicalHeaders.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
			if (signedHeaders.length() > 0) signedHeaders.append(';');
			signedHeaders.append(entry.getKey());
		}

		String canonicalRequest = method + "\n"
				+ path + "\n"
				+ "\n"
				+ canonicalHeaders
				+ "\n"
				+ signedHeaders + "\n"
				+ sha256Hex(payload);

		String credentialScope = dateStamp + "/" + region + "/" + service + "/aws4_request";
		String stringToSign = ALGORITHM + "\n"
				+ amzDate + "\n"
				+ credentialScope + "\n"
				+ sha256Hex(canonicalRequest);

		byte[] kDate = hmacSha256(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), dateStamp);
		byte[] kRegion = hmacSha256(kDate, region);
		byte[] kService = hmacSha256(kRegion, service);
		byte[] kSigning = hmacSha256(kService, "aws4_request");
		String signature = toHex(hmacSha256(kSigning, stringToSign));

		return ALGORITHM + " Credential=" + accessKey + "/" + credentialScope
				+ ", SignedHeaders=" + signedHeaders
				+ ", Signature=" + signature;
	}

	private static String sha256Hex(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return toHex(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static byte[] hmacSha256(byte[] key, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(key, "HmacSHA256"));
			return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
