package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.order.OrderItemOptionRequest;
import com.scaneat.back.dto.order.OrderItemOptionResponse;
import com.scaneat.back.dto.order.OrderItemRequest;
import com.scaneat.back.dto.order.OrderItemResponse;
import com.scaneat.back.dto.order.OrderRequest;
import com.scaneat.back.dto.order.OrderResponse;
import com.scaneat.back.entity.OrderStatus;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.entity.UsrOrderItem;
import com.scaneat.back.entity.UsrOrderItemId;
import com.scaneat.back.entity.UsrOrderItemOpt;
import com.scaneat.back.entity.UsrOrderItemOptId;
import com.scaneat.back.repository.UsrOrderItemOptRepository;
import com.scaneat.back.repository.UsrOrderItemRepository;
import com.scaneat.back.repository.UsrOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private final UsrOrderRepository usrOrderRepository;
	private final UsrOrderItemRepository usrOrderItemRepository;
	private final UsrOrderItemOptRepository usrOrderItemOptRepository;

	public OrderResponse getOrder(String orderNo) {
		UsrOrder order = findOrder(orderNo);
		return buildOrderResponse(order);
	}

	public List<OrderResponse> getOrdersByUuid(String uuid) {
		return usrOrderRepository.findByUuidOrderByRegDtDesc(uuid).stream()
				.map(this::buildOrderResponse)
				.toList();
	}

	@Transactional
	public OrderResponse createOrder(OrderRequest request) {
		LocalDateTime now = LocalDateTime.now();
		String orderNo = generateOrderNo();

		List<UsrOrderItem> items = new ArrayList<>();
		List<UsrOrderItemOpt> options = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;

		int seq = 1;
		for (OrderItemRequest itemRequest : request.items()) {
			UsrOrderItemId itemId = new UsrOrderItemId(orderNo, seq);
			items.add(UsrOrderItem.builder()
					.id(itemId)
					.menuCd(itemRequest.menuCd())
					.menuNm(itemRequest.menuNm())
					.price(itemRequest.price())
					.qty(itemRequest.qty())
					.regDt(now)
					.build());

			BigDecimal lineAmount = itemRequest.price();

			if (itemRequest.options() != null) {
				for (OrderItemOptionRequest optRequest : itemRequest.options()) {
					options.add(UsrOrderItemOpt.builder()
							.id(new UsrOrderItemOptId(orderNo, seq, optRequest.optCd()))
							.optNm(optRequest.optNm())
							.addPrice(optRequest.addPrice())
							.regDt(now)
							.build());
					lineAmount = lineAmount.add(optRequest.addPrice());
				}
			}

			totalAmount = totalAmount.add(lineAmount.multiply(BigDecimal.valueOf(itemRequest.qty())));
			seq++;
		}

		UsrOrder order = UsrOrder.builder()
				.orderNo(orderNo)
				.uuid(request.uuid())
				.bizRegNo(request.bizRegNo())
				.seatNo(request.seatNo())
				.totalAmount(totalAmount)
				.status(OrderStatus.PENDING)
				.regUsrId("guest")
				.regDt(now)
				.build();

		usrOrderRepository.save(order);
		usrOrderItemRepository.saveAll(items);
		usrOrderItemOptRepository.saveAll(options);

		return buildOrderResponse(order);
	}

	private UsrOrder findOrder(String orderNo) {
		return usrOrderRepository.findById(orderNo)
				.orElseThrow(() -> new ResourceNotFoundException("주문을 찾을 수 없습니다: " + orderNo));
	}

	private OrderResponse buildOrderResponse(UsrOrder order) {
		List<UsrOrderItem> items = usrOrderItemRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo());
		List<UsrOrderItemOpt> options = usrOrderItemOptRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo());

		Map<Integer, List<OrderItemOptionResponse>> optionsBySeq = options.stream()
				.collect(Collectors.groupingBy(o -> o.getId().getOrderSeq(),
						Collectors.mapping(OrderItemOptionResponse::from, Collectors.toList())));

		List<OrderItemResponse> itemResponses = items.stream()
				.map(item -> OrderItemResponse.from(item, optionsBySeq.getOrDefault(item.getId().getOrderSeq(), List.of())))
				.toList();

		return OrderResponse.from(order, itemResponses);
	}

	private String generateOrderNo() {
		String orderNo;
		int attempts = 0;
		do {
			String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
			orderNo = "O" + LocalDateTime.now().format(ORDER_NO_FORMAT) + suffix;
			attempts++;
		} while (usrOrderRepository.existsById(orderNo) && attempts < 5);
		return orderNo;
	}
}
