package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.TrangThai;

@Repository
public interface TrangThaiRepository extends JpaRepository<TrangThai, Long>{

}
