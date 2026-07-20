package com.scaneat.back.service;

import com.scaneat.back.dto.order.OrderResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// uuid별로 열려있는 SSE 연결을 들고 있다가, 주문상태가 바뀌면 그 손님에게만 밀어줌
@Service
public class OrderEventService {

	private final Map<String, List<SseEmitter>> emittersByUuid = new ConcurrentHashMap<>();

	public SseEmitter subscribe(String uuid) {
		SseEmitter emitter = new SseEmitter(0L);
		emittersByUuid.computeIfAbsent(uuid, k -> new CopyOnWriteArrayList<>()).add(emitter);

		emitter.onCompletion(() -> removeEmitter(uuid, emitter));
		emitter.onTimeout(() -> removeEmitter(uuid, emitter));
		emitter.onError(e -> removeEmitter(uuid, emitter));

		try {
			emitter.send(SseEmitter.event().name("connected").data("ok"));
		} catch (IOException e) {
			removeEmitter(uuid, emitter);
		}
		return emitter;
	}

	public void notifyOrderUpdated(String uuid, OrderResponse order) {
		List<SseEmitter> emitters = emittersByUuid.get(uuid);
		if (emitters == null || emitters.isEmpty()) {
			return;
		}
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("order-status").data(order));
			} catch (IOException e) {
				removeEmitter(uuid, emitter);
			}
		}
	}

	// 프록시/로드밸런서가 idle 커넥션을 끊지 않도록 주기적으로 핑을 보냄
	@Scheduled(fixedRate = 25000)
	public void heartbeat() {
		emittersByUuid.forEach((uuid, emitters) -> {
			for (SseEmitter emitter : emitters) {
				try {
					emitter.send(SseEmitter.event().name("ping").data(""));
				} catch (IOException e) {
					removeEmitter(uuid, emitter);
				}
			}
		});
	}

	private void removeEmitter(String uuid, SseEmitter emitter) {
		List<SseEmitter> emitters = emittersByUuid.get(uuid);
		if (emitters == null) {
			return;
		}
		emitters.remove(emitter);
		if (emitters.isEmpty()) {
			emittersByUuid.remove(uuid);
		}
	}
}
