package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.model.ThongSo;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer>{
	@Modifying
	@Query(value = "DELETE FROM danhmuc_thongso WHERE DM_ID IN (:id) AND TS_ID IN (:idts)", nativeQuery = true)
	public void deleteDanhMucThongSoByDanhMuc(@Param("id") List<Integer> id, @Param("idts") List<Long> idts);
	public Page<DanhMuc> findByTenContainingIgnoreCase(String keyword, Pageable pageable);
	}

