package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrPrvCns;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrPrvCnsRepository extends JpaRepository<UsrPrvCns, Long> {

	List<UsrPrvCns> findByUuidOrderByConsentAtDesc(String uuid);

	Optional<UsrPrvCns> findFirstByUuidAndBizRegNoOrderByConsentAtDesc(String uuid, String bizRegNo);

	boolean existsByUuidAndBizRegNo(String uuid, String bizRegNo);
}
