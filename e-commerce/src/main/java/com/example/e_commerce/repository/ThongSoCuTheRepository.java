package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ThongSoCuThe;
import com.example.e_commerce.model.ThuongHieu;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ThongSoCuTheRepository extends JpaRepository<ThongSoCuThe, Long>{
	@Modifying
	@Query(value = "DELETE FROM sanpham_thongsocuthe WHERE TSCT_ID = :id", nativeQuery = true)
	void deleteThongSoCuThe(@Param("id") long id);
	
	@Query(value = "SELECT DISTINCT t.* FROM sanpham s JOIN sanpham_thongsocuthe h ON h.SP_ID=s.SP_ID JOIN "
			+ "thongsocuthe t ON t.TSCT_ID=h.TSCT_ID "
			+ "JOIN danhmuc d ON d.DM_ID=s.DMM_ID WHERE  "
			+ "s.SP_TEN LIKE CONCAT('%', :ten, '%') AND d.DM_ID IN (:idDanhMuc) ",nativeQuery = true)
	public List<ThongSoCuThe> getThuongHieuBySanPhamAndDanhMuc(@Param("ten") String ten,
			@Param("idDanhMuc") List<Integer> idDanhMuc);
}
