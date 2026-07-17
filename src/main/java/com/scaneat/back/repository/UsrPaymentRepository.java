package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsrPaymentRepository extends JpaRepository<UsrPayment, String> {

	// 24시간 영업 매장도 있어 최소 이틀(어제 00:00부터 지금까지)은 조회 가능해야 함
	@Query(value = "SELECT * FROM tb_usr_payment WHERE uuid = :uuid "
			+ "AND reg_dt >= date_trunc('day', now() - interval '1 day') "
			+ "ORDER BY reg_dt DESC", nativeQuery = true)
	List<UsrPayment> findRecentByUuid(@Param("uuid") String uuid);
}
