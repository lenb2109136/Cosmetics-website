package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.embeded.EBienTheDealPhu;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface BienTheDealPhuRepository extends JpaRepository<BienTheDealPhu, EBienTheDealPhu>{
			
	@Query(value = "SELECT DISTINCT b.* FROM bienthedealphu b " +
	        "JOIN khuyenmai d ON b.KM_ID = d.KM_ID " +
	        "JOIN deal d2 ON d2.KM_ID = d.KM_ID " +
	        "JOIN bienthe bb ON bb.BT_ID = b.BT_ID " +
	        "WHERE b.BT_ID IN (:idBienThe) " +
	        "AND NOW() >= d.KM_THOIGIANAPDUNG " +
	        "AND NOW() <= d.KM_THOIGIANNGUNG " +
	        "AND d.KM_CONSUDUNG = TRUE " +
	        "AND b.BTDP_CONSUDUNG = TRUE " +
	        "AND d2.D_SOLUONGGIOIHAN > d2.D_SOLUONGDADUNG " +
	        "AND bb.BT_SOLUONG > 0", 
	        nativeQuery = true)
	public List<BienTheDealPhu> getDealPhuOfSanPham(@Param("idBienThe") List<Integer> idBienThe);

}
