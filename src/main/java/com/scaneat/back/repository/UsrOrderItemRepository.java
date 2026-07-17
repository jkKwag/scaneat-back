package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrOrderItem;
import com.scaneat.back.entity.UsrOrderItemId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrOrderItemRepository extends JpaRepository<UsrOrderItem, UsrOrderItemId> {

	List<UsrOrderItem> findById_OrderNoOrderById_OrderSeqAsc(String orderNo);
}
