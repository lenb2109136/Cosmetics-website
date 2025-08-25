package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.model.TrangThaiHoaDon;
import com.example.e_commerce.model.embeded.ETrangThaiHoaDon;

@Repository
public interface TrangThaiHoaDonRepository extends JpaRepository<TrangThaiHoaDon,ETrangThaiHoaDon>{

}
