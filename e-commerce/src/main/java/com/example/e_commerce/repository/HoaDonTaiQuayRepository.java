package com.example.e_commerce.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.HoaDonTaiQuay;

@Repository
public interface HoaDonTaiQuayRepository extends JpaRepository<HoaDonTaiQuay,Long>{
	@Query(value = "SELECT h FROM HoaDonTaiQuay h WHERE h.ngayLap>=:bd AND h.ngayLap<= :kt")
	public List<HoaDonTaiQuay> getHoaDonTaiQuay(LocalDateTime bd, LocalDateTime kt);
	
	@Query(value = "SELECT h.HD_ID,hd.HD_DIACHI,hd.HD_ZPTR, h.HTTT_ID,hd.HD_HINHTHUCHOANTRA, h.NV_ID, hd.KH_ID,hd_qrcode, hd.HD_NGAYLAP, hd.HD_TONGTIEN, hd.HD_DAHOANTIEN, hd.HD_DATHANHTOAN, hd.HD_TRANSID, hd.TT_ID " +
	           "FROM hoadontaiquay h " +
	           "JOIN hoadon hd ON h.HD_ID = hd.HD_ID " +
	           "JOIN trangthai t ON hd.TT_ID = t.TT_ID " +
	           "JOIN nguoidung n ON h.NV_ID = n.ND_ID " +
	           "LEFT JOIN khachhang k ON hd.KH_ID = k.ND_ID " +
	           "WHERE (:maHoaDon IS NULL OR CAST(h.HD_ID AS CHAR) LIKE CONCAT('%', :maHoaDon, '%')) " +
	           "AND (:tt = 0 OR t.TT_ID = :tt) " +
	           "AND (:tenOrSdt IS NULL OR n.ND_TEN LIKE CONCAT('%', :tenOrSdt, '%') OR n.ND_SDT LIKE CONCAT('%', :tenOrSdt, '%') " +
	           "    OR (k.ND_ID IS NOT NULL AND (n.ND_TEN LIKE CONCAT('%', :tenOrSdt, '%') OR n.ND_SDT LIKE CONCAT('%', :tenOrSdt, '%')))) " +
	           "AND (:ngayLap IS NULL OR DATE(hd.HD_NGAYLAP) = :ngayLap) " +
	           "ORDER BY " +
	           "CASE WHEN :sort = 1 THEN hd.HD_NGAYLAP END DESC, " +
	           "CASE WHEN :sort = 2 THEN hd.HD_NGAYLAP END ASC, " +
	           "CASE WHEN :sort = 3 THEN hd.HD_TONGTIEN END ASC, " +
	           "CASE WHEN :sort = 4 THEN hd.HD_TONGTIEN END DESC",
	           nativeQuery = true)
	    Page<HoaDonTaiQuay> findHoaDonTaiQuayByFilters(
	            @Param("maHoaDon") String maHoaDon,
	            @Param("tt") Long tt,
	            @Param("tenOrSdt") String tenOrSdt,
	            @Param("ngayLap") LocalDate ngayLap,
	            @Param("sort") int sort,
	            Pageable pageable
	    );
}
