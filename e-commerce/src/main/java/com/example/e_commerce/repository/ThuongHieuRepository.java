package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ThuongHieu;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long> {
	@Query(value = "SELECT DISTINCT h.* FROM sanpham s " + "JOIN thuonghieu h ON h.TH_ID = s.TH_ID "
			+ "JOIN danhmuc d ON d.DM_ID = s.DMM_ID "
			+ "WHERE s.SP_TEN LIKE CONCAT('%', :ten, '%') AND d.DM_ID IN (:idDanhMuc)", nativeQuery = true)
	public List<ThuongHieu> getThuongHieuBySanPhamAndDanhMuc(@Param("ten") String ten,
			@Param("idDanhMuc") List<Integer> idDanhMuc);
	
	
	@Query(value = "SELECT * FROM thuonghieu t WHERE LOWER(TRIM(t.TH_TEN)) LIKE CONCAT(LOWER(:ten), '%')", nativeQuery = true)
	public List<ThuongHieu> getByChar(@Param("ten") String ten);

	Page<ThuongHieu> findByTenContainingIgnoreCase(String ten, Pageable pageable);
	
}
