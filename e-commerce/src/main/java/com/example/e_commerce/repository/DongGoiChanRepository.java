package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.DongGoiChan;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface DongGoiChanRepository extends JpaRepository<DongGoiChan, Integer>{
	@Query(value = "SELECT COUNT(*) FROM bienthequycachdonggoi_lannhap d WHERE d.DGC_ID = :id", nativeQuery = true)
	int countNhap(@Param("id") int id);


}
