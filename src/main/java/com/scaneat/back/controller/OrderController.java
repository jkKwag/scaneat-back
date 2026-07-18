package com.scaneat.back.controller;

import com.scaneat.back.common.ApiResponse;
import com.scaneat.back.dto.order.OrderRequest;
import com.scaneat.back.dto.order.OrderResponse;
import com.scaneat.back.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
	public ApiResponse<List<OrderResponse>> getOrdersByBiz(@PathVariable String bizRegNo) {
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
}
