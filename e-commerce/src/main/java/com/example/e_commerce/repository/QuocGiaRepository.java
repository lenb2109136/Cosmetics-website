package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.QuocGia;

@Repository
public interface QuocGiaRepository extends JpaRepository<QuocGia, Long>{

}
