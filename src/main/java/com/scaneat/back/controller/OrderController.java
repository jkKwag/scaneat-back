package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.order.OrderRequest;
import com.scaneat.back.dto.order.OrderResponse;
import com.scaneat.back.dto.order.OrderStatusUpdateRequest;
import com.scaneat.back.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@GetMapping
	public ApiResponse<List<OrderResponse>> getOrders(@RequestParam String uuid) {
		return ApiResponse.ok(orderService.getOrdersByUuid(uuid));
	}

	@GetMapping("/biz/{bizRegNo}")
	public ApiResponse<List<OrderResponse>> getOrdersByBiz(
			@PathVariable String bizRegNo,
			@RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to) {
		if (from != null && to != null) {
			return ApiResponse.ok(orderService.getOrdersByBiz(bizRegNo, from, to));
		}
		return ApiResponse.ok(orderService.getOrdersByBiz(bizRegNo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
		return ApiResponse.ok(orderService.createOrder(request));
	}

	@GetMapping("/{orderNo}")
	public ApiResponse<OrderResponse> getOrder(@PathVariable String orderNo) {
		return ApiResponse.ok(orderService.getOrder(orderNo));
	}

	@PutMapping("/{orderNo}/status")
	public ApiResponse<OrderResponse> updateStatus(
			@PathVariable String orderNo, @Valid @RequestBody OrderStatusUpdateRequest request) {
		return ApiResponse.ok(orderService.updateStatus(orderNo, request));
	}

	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream(@RequestParam String uuid, HttpServletResponse response) {
		// 리버스 프록시(nginx 등)가 스트리밍 응답을 버퍼링해서 이벤트 전달이 지연되는 것을 방지
		response.setHeader("X-Accel-Buffering", "no");
		response.setHeader("Cache-Control", "no-cache");
		return orderService.subscribe(uuid);
	}
}
