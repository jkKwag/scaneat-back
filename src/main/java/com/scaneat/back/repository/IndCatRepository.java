package com.scaneat.back.repository;

import com.scaneat.back.entity.IndCat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndCatRepository extends JpaRepository<IndCat, String> {

	List<IndCat> findByIndCdAndUseYnOrderBySortOrdAsc(String indCd, String useYn);
}
