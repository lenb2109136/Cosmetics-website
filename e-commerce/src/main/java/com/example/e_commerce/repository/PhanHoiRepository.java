package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.PhanHoi;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi,Long>{
	@Query(value = "SELECT AVG(p.PH_SOSAO), COUNT(*) FROM phanhoi p WHERE p.SP_ID= :id", nativeQuery = true)
	public List<Object[]> getGeneralPhanHoiSanPham(@Param("id") long id);
}
