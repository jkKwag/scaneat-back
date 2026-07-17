package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrPaymentRepository extends JpaRepository<UsrPayment, String> {

	List<UsrPayment> findByUuidOrderByRegDtDesc(String uuid);
}
