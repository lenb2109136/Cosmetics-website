package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.FlashSale;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long>{
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM flashsale f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "JOIN bientheflashsale b ON k.KM_ID = b.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG >= NOW() " +
            "AND b.BT_ID = :id " +
            "AND k.KM_CONSUDUNG = true " +
            "AND b.BTFS_CONSUDUNG = true",
    nativeQuery = true)
public List<FlashSale> getFlashSaleTheoBienThe(@Param("id") int id);
	
	
	@Modifying
	@Query(value = "UPDATE bientheflashsale b "
	        + "JOIN sanpham s ON b.KM_ID = s.SP_ID "
	        + "JOIN flashsale f ON f.KM_ID = b.KM_ID "
	        + "SET b.BTFS_CONSUDUNG = FALSE "
	        + "WHERE f.KM_ID = :id AND s.SP_ID IN :list", nativeQuery = true)
	void unActiveFlashSale(@Param("id") long id, @Param("list") List<Long> list);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM flashsale f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANAPDUNG >= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<FlashSale> getFlashSaleBeforNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM flashsale f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG >= NOW() AND k.KM_THOIGIANAPDUNG <= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<FlashSale> getFlashSaleInNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM flashsale f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG < NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd)) " ,nativeQuery = true)
	public Page<FlashSale> getFlashSaleAfterNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);


}
