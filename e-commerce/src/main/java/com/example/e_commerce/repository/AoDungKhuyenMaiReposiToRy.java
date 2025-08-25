package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ApDungKhuyenMai;
import com.example.e_commerce.model.embeded.EApDungKhuyenMai;
@Repository
public interface AoDungKhuyenMaiReposiToRy extends JpaRepository<ApDungKhuyenMai, EApDungKhuyenMai>{

}
