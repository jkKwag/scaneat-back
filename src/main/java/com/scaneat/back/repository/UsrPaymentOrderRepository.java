package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrPaymentOrder;
import com.scaneat.back.entity.UsrPaymentOrderId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrPaymentOrderRepository extends JpaRepository<UsrPaymentOrder, UsrPaymentOrderId> {

	List<UsrPaymentOrder> findById_PaymentKey(String paymentKey);
}
