package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.DonGiaBanHang;

@Repository
public interface DonGiaBanHangRepository extends JpaRepository<DonGiaBanHang, Long>{

}
