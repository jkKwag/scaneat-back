package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrPayment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsrPaymentRepository extends JpaRepository<UsrPayment, String> {

	// 24시간 영업 매장도 있어 최소 이틀(어제 00:00부터 지금까지)은 조회 가능해야 함
	// 등록일자(reg_dt)가 아닌 실제 결제 승인일자(approved_dt) 기준으로 필터링
	@Query(value = "SELECT * FROM tb_usr_payment WHERE uuid = :uuid "
			+ "AND approved_dt >= date_trunc('day', now() - interval '1 day') "
			+ "ORDER BY approved_dt DESC", nativeQuery = true)
	List<UsrPayment> findRecentByUuid(@Param("uuid") String uuid);

	List<UsrPayment> findByBizRegNoOrderByRegDtDesc(String bizRegNo);

	List<UsrPayment> findByBizRegNoAndApprovedDtBetweenOrderByApprovedDtDesc(String bizRegNo, LocalDateTime from, LocalDateTime to);

	// 결제완료(DONE) 건 기준, 사업장별 총매출 상위 N개
	@Query(value = "SELECT p.biz_reg_no AS bizRegNo, b.biz_nm AS bizNm, "
			+ "SUM(p.total_amount) AS totalAmount, COUNT(*) AS paymentCount "
			+ "FROM tb_usr_payment p JOIN tb_biz b ON b.biz_reg_no = p.biz_reg_no "
			+ "WHERE p.status = 'DONE' "
			+ "GROUP BY p.biz_reg_no, b.biz_nm "
			+ "ORDER BY SUM(p.total_amount) DESC "
			+ "LIMIT :limit", nativeQuery = true)
	List<BizRevenueRankProjection> findTopBizByRevenue(@Param("limit") int limit);
}
