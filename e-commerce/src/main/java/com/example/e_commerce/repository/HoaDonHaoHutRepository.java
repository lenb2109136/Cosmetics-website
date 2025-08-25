package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.HoaDonHaoHut;
import com.example.e_commerce.model.embeded.EHoaDonHaoHut;

@Repository
public interface HoaDonHaoHutRepository extends JpaRepository<HoaDonHaoHut,EHoaDonHaoHut>{

}
