package com.scaneat.back.repository;

import com.scaneat.back.entity.Qna;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Long> {

	List<Qna> findByBizRegNoOrderByCreatedAtDesc(String bizRegNo);

	List<Qna> findAllByOrderByCreatedAtDesc();
}
