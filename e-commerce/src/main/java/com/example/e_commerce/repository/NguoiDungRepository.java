package com.example.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;   // đúng import

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    // Tra về Optional để tránh NullPointerException
    Optional<NguoiDung> findBySodienthoai(String sodienthoai);

    @Query("SELECT k FROM KhachHang k WHERE k.sodienthoai = :sdt")
   KhachHang getKhachHangBySDT(@Param("sdt") String soDienThoai);
}
