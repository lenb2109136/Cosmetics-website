package com.example.e_commerce.repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.DTO.request.BienThe;
import com.example.e_commerce.model.ChiTietPhieuNhap;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, Long>{
	@Query("SELECT c.bienThe, c.phieuNhap.ngayNhapHang,c.soLuong, c.donGia,c.phieuNhap.id FROM ChiTietPhieuNhap c " +
		       "WHERE c.phieuNhap.ngayNhapHang >= :nbd " +
		       "AND c.phieuNhap.ngayNhapHang <= :nkt " +
		       "AND c.phieuNhap.donViCungCap.id = :id")
		Page<Object[]> getBienTheDonViCungCap(
		       @Param("nbd") LocalDateTime nbd, 
		       @Param("nkt") LocalDateTime nkt, 
		       @Param("id") long id,
		       org.springframework.data.domain.Pageable pageable);
		
		@Query(value = "SELECT SUM(c.CTPN_SOLUONG) FROM chitietphieunhap c join phieunhap p ON c.PN_ID=p.PN_ID WHERE c.BT_ID=:id"
				+ " AND p.PN_NGAYNHAPHANG<=:nkt  AND p.PN_NGAYNHAPHANG>=:nbd",nativeQuery = true)
		public Integer TongSoLuongNhapTaiThoiDiem(@Param("id") int id, @Param("nkt") LocalDateTime nkt,@Param("nbd") LocalDateTime nbd);
		
		
		@Query(value = "SELECT c.* FROM chitietphieunhap c JOIN phieunhap p ON p.PN_ID = c.PN_ID " +
	               "WHERE BT_ID = :id AND p.PN_NGAYNHAPHANG <= :nn " +
	               "ORDER BY p.PN_NGAYNHAPHANG ASC", nativeQuery = true)
	public List<ChiTietPhieuNhap> getAllChiTietPhieuNhapBienThe(
	        @Param("id") int id, 
	        @Param("nn") LocalDateTime nn);
		
		
		@Query(value = "SELECT COUNT(*) FROM bienthequycachdonggoi_lannhap " +
	               "WHERE BTDGC_SOLUONG = :sl AND BT_ID = :btId AND DGC_ID = :dgcId", nativeQuery = true)
	int demBanGhiTrung(@Param("sl") int sl, @Param("btId") int btId, @Param("dgcId") int dgcId);

		
		@Query(value = "SELECT c.* " +
		           "FROM chitietphieunhap c " +
		           "JOIN phieunhap p ON c.PN_ID = p.PN_ID " +
		           "WHERE c.BT_ID = :bienTheId " +
		           "ORDER BY ABS(TIMESTAMPDIFF(SECOND, p.PN_NGAYNHAPHANG, :ngayNhap)) " +
		           "LIMIT 1", nativeQuery = true)
		Optional<ChiTietPhieuNhap> findLatestByBienTheIdAndNgayNhap(int bienTheId, LocalDateTime ngayNhap);
}
