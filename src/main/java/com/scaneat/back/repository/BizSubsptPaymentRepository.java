package com.scaneat.back.repository;

import com.scaneat.back.entity.BizSubsptPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizSubsptPaymentRepository extends JpaRepository<BizSubsptPayment, String> {

	List<BizSubsptPayment> findByBizRegNoOrderByRegDtDesc(String bizRegNo);
}
