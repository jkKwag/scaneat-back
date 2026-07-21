package com.scaneat.back.repository;

import com.scaneat.back.entity.IndCls;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndClsRepository extends JpaRepository<IndCls, String> {

	List<IndCls> findAllByOrderByClsLvlAscSortOrdAsc();
}
