package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.QuyCachDongGoiLanNhap;
import com.example.e_commerce.model.embeded.EQuyCachDongGoiLanNhap;

@Repository
public interface BienTheDongGoiLanNhapRepository extends JpaRepository<QuyCachDongGoiLanNhap, EQuyCachDongGoiLanNhap>{

}
