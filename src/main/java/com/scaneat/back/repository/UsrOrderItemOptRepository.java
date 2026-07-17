package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrOrderItemOpt;
import com.scaneat.back.entity.UsrOrderItemOptId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrOrderItemOptRepository extends JpaRepository<UsrOrderItemOpt, UsrOrderItemOptId> {

	List<UsrOrderItemOpt> findById_OrderNoOrderById_OrderSeqAsc(String orderNo);
}
