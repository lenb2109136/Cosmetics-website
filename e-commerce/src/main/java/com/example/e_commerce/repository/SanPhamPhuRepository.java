package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.SanPhamKiem;
import com.example.e_commerce.model.SanPhamPhu;
import com.example.e_commerce.model.embeded.ESanPhamPhu;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SanPhamPhuRepository extends JpaRepository<SanPhamPhu, ESanPhamPhu>{
	@Query(value = "SELECT * FROM sanphamkiem s JOIN phieukiemhang p ON p.PKH_ID=s.PKH_ID WHERE s.BT_ID=:id AND p.PKH_DAXACNHAN=true",nativeQuery = true)
	public List<SanPhamKiem> getSanPhamKiem(@Param("id") int id);
}
