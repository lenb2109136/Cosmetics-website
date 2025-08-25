package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.SanPhamChinh;
import com.example.e_commerce.model.embeded.ESanPhamChinh;

@Repository
public interface SanPhamChinhRepository extends JpaRepository<SanPhamChinh,ESanPhamChinh>{

}
