package com.example.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ThongSo;

@Repository
public interface ThongSoRepository extends JpaRepository<ThongSo, Long>{
	public ThongSo findByTen(String ten);
	public Page<ThongSo> findByTenContainingIgnoreCase(String keyword, Pageable pageable);
}
