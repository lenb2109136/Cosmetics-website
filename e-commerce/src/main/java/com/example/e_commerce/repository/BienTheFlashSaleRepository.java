package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.embeded.EBienTheFlashSale;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface BienTheFlashSaleRepository extends JpaRepository<BienTheFlashSale, EBienTheFlashSale> {
	@Query(value = "SELECT b.* FROM flashsale f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "JOIN bientheflashsale b ON k.KM_ID = b.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG > NOW() " +
            "AND b.BT_ID = :id " +
            "AND k.KM_CONSUDUNG = true " +
            "AND b.BTFS_CONSUDUNG = true", 
    nativeQuery = true)
public List<BienTheFlashSale> getBienTheFlashSaleTheoBienThe(@Param("id") int id);

	
	
	@Query(value = "SELECT bt.* FROM khuyenmai k JOIN flashsale f ON k.KM_ID=f.KM_ID "
			+ "JOIN bientheflashsale bt ON bt.KM_ID=f.KM_ID\r\n"
			+ "JOIN bienthe b ON bt.BT_ID=b.BT_ID \r\n"
			+ "WHERE NOW() >= k.KM_THOIGIANAPDUNG \r\n"
			+ "AND b.BT_ID IN (:idBienThe) \r\n"
			+ "AND NOW() <=k.KM_THOIGIANNGUNG\r\n"
			+ "AND k.KM_CONSUDUNG=TRUE \r\n"
			+ "AND bt.BTFS_CONSUDUNG=TRUE\r\n"
			+ "AND bt.BTFS_GIOIHANSOLUONG-bt.BTFS_SOLUONGDADUNG > 0\r\n"
			+ "AND b.BT_SOLUONG > 0\r\n"
			+ "", nativeQuery = true)
	public List<BienTheFlashSale> getFlashSaleOfSanPhamDangApDung(@Param("idBienThe") List<Integer> idBienThe );
}
