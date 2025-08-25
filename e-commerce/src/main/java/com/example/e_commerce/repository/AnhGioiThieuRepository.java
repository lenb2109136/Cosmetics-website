package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.AnhGioiThieu;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface AnhGioiThieuRepository extends JpaRepository<AnhGioiThieu, Long>{
	@Modifying
	@Query("DELETE FROM AnhGioiThieu e WHERE e.id = :id")
	int deleteByIdCustom(@Param("id") Long id);
}
