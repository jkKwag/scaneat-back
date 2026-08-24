package com.scaneat.back.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.push.PushSubscribeRequest;
import com.scaneat.back.entity.Biz;
import com.scaneat.back.entity.PushReg;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.PushRegRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사업장 관리자 브라우저의 웹 푸시 등록/해지, 그리고 손님의 "직원호출" 시 그 사업장에
// 등록된 모든 브라우저로 실제 푸시를 발송하는 역할을 담당한다.
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushRegService {

	private final PushRegRepository pushRegRepository;
	private final BizRepository bizRepository;
	private final PushService pushService;
	private final ObjectMapper objectMapper;

	@Transactional
	public void subscribe(String bizRegNo, PushSubscribeRequest request, String adminId) {
		LocalDateTime now = LocalDateTime.now();
		PushReg reg = pushRegRepository.findByBizRegNoAndBrEndptUrl(bizRegNo, request.endpoint())
				.orElseGet(() -> PushReg.builder()
						.bizRegNo(bizRegNo)
						.brEndptUrl(request.endpoint())
						.deviceType("WEB")
						.regUsrId(adminId)
						.regDt(now)
						.build());
		reg.setPushPubKey(request.keys().p256dh());
		reg.setPushAuthKey(request.keys().auth());
		reg.setUseYn("Y");
		reg.setUpdUsrId(adminId);
		reg.setUpdDt(now);
		pushRegRepository.save(reg);
	}

	// 손님이 매장주문 화면에서 직원호출을 누르면 호출된다 — 로그인하지 않은 손님이 호출하는
	// 공개 API라 관리자 인증을 요구하지 않는다 (AdminAuthInterceptor에서 별도 예외 처리됨).
	@Transactional
	public void callStaff(String bizRegNo, String seatCd) {
		Biz biz = bizRepository.findById(bizRegNo)
				.orElseThrow(() -> new ResourceNotFoundException("사업자를 찾을 수 없습니다: " + bizRegNo));

		List<PushReg> regs = pushRegRepository.findByBizRegNoAndUseYn(bizRegNo, "Y");
		if (regs.isEmpty()) {
			log.info("직원호출: {} 사업장에 등록된 알림 수신 브라우저가 없습니다.", bizRegNo);
			return;
		}

		String body = (seatCd != null ? seatCd + "번 좌석" : "매장") + "에서 직원을 호출했습니다.";
		Map<String, String> payloadMap = new LinkedHashMap<>();
		payloadMap.put("title", "🔔 " + (biz.getBizNm() != null ? biz.getBizNm() : "직원호출"));
		payloadMap.put("body", body);
		String payload = toJson(payloadMap);

		for (PushReg reg : regs) {
			try {
				Subscription subscription = new Subscription(reg.getBrEndptUrl(),
						new Subscription.Keys(reg.getPushPubKey(), reg.getPushAuthKey()));
				Notification notification = new Notification(subscription, payload);
				HttpResponse response = pushService.send(notification);
				int status = response.getStatusLine().getStatusCode();
				if (status == 404 || status == 410) {
					// 브라우저에서 알림 권한을 해지했거나 만료된 구독 — 더 이상 발송 대상에서 제외
					reg.setUseYn("N");
					pushRegRepository.save(reg);
				}
			} catch (Exception e) {
				log.warn("직원호출 푸시 발송 실패 (pushId={}): {}", reg.getPushId(), e.getMessage());
			}
		}
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException("푸시 알림 페이로드 생성에 실패했습니다.", e);
		}
	}
}
