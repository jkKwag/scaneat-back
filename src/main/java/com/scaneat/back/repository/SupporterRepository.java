package com.scaneat.back.repository;

import com.scaneat.back.entity.Supporter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupporterRepository extends JpaRepository<Supporter, Long> {

	java.util.List<Supporter> findAllByOrderByCreatedAtDesc();
}
