package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.model.PhieuNhap;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Long>{
	@Query("select p from PhieuNhap p where p.ngayNhapHang >= :nbd and p.ngayNhapHang <= :nkt and p.donViCungCap.id = :id")
	Page<PhieuNhap> getPhieuNhap(@Param("nbd") LocalDateTime nbd, @Param("nkt") LocalDateTime nkt, @Param("id") long id,Pageable page);
	
	@Query(value = "SELECT DISTINCT p.* FROM phieunhap p " +
	        "JOIN donvicuncap d ON p.DVCC_ID = d.DVCC_ID " +
	        "JOIN chitietphieunhap c ON c.PN_ID = p.PN_ID " +
	        "JOIN bienthe b ON b.BT_ID = c.BT_ID " +
	        "JOIN sanpham s ON s.SP_ID = b.SP_ID " +
	        "WHERE d.DVCC_TEN LIKE CONCAT('%', :tendvcc, '%') " +
	        "AND s.SP_TEN LIKE CONCAT('%', :tensp, '%') " +
	        "AND p.PN_NGAYNHAPHANG >= :nbd AND p.PN_NGAYNHAPHANG <= :nkt " +
	        "ORDER BY p.PN_NGAYNHAPHANG DESC",

	       countQuery = "SELECT COUNT(DISTINCT p.PN_ID) FROM phieunhap p " +
	        "JOIN donvicuncap d ON p.DVCC_ID = d.DVCC_ID " +
	        "JOIN chitietphieunhap c ON c.PN_ID = p.PN_ID " +
	        "JOIN bienthe b ON b.BT_ID = c.BT_ID " +
	        "JOIN sanpham s ON s.SP_ID = b.SP_ID " +
	        "WHERE d.DVCC_TEN LIKE CONCAT('%', :tendvcc, '%') " +
	        "AND s.SP_TEN LIKE CONCAT('%', :tensp, '%') " +
	        "AND p.PN_NGAYNHAPHANG >= :nbd AND p.PN_NGAYNHAPHANG <= :nkt",
	       
	       nativeQuery = true)
	Page<PhieuNhap> getPhieuNhapByTen(
	        @Param("nbd") LocalDateTime nbd,
	        @Param("nkt") LocalDateTime nkt,
	        @Param("tendvcc") String tendvcc,
	        @Param("tensp") String tensp,
	        Pageable page);

	
	
	@Query(value = "SELECT * FROM bienthequycachdonggoi b WHERE b.SOLUONG = :soLuong AND b.DGC_ID = :dgcId AND b.BT_ID = :btId", nativeQuery = true)
	BienTheDongGoiChan getMaVachQuyCach(@Param("soLuong") int soLuong, @Param("dgcId") int dgcId, @Param("btId") int btId);

}
