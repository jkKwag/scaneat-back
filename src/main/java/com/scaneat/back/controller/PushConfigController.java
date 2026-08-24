package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 웹 푸시 구독에 필요한 VAPID 공개키를 내려주는 공개 API. 공개키는 이름 그대로 공개되어도
// 되는 값이라 별도 인증 없이 열어둔다 (비밀키는 서버에만 있음).
@RestController
@RequestMapping("/api/push")
public class PushConfigController {

	@Value("${push.vapid.public-key}")
	private String publicKey;

	@GetMapping("/vapid-public-key")
	public ApiResponse<String> vapidPublicKey() {
		return ApiResponse.ok(publicKey);
	}
}
