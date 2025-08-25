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

import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;

@Repository
public interface HoaDonOnlineRepository extends JpaRepository<HoaDonOnline, Long> {
	@Query(value = "SELECT hd.HD_ID,hd.HD_DIACHI,hd.HD_ZPTR, hd.HD_HINHTHUCHOANTRA,h.HDO_DALENGHN, hd.KH_ID, hd.HD_QRCODE, hd.HD_NGAYLAP, hd.HD_TONGTIEN, hd.HD_DAHOANTIEN,h.DH_ID, hd.HD_DATHANHTOAN, hd.HD_TRANSID, hd.TT_ID, h.HDO_GHNCODE"
			+ ",HDO_PHIBAOHIEM,HDO_PHIHOANTRA,HDO_TONGPHI,HDO_DUKIENGIAO, h.HDO_TONGKHOILUONG " + "FROM hoadon hd "
			+ "JOIN trangthai t ON hd.TT_ID = t.TT_ID JOIN hoadononline h ON h.HD_ID=hd.HD_ID "
			+ "JOIN khachhang k ON hd.KH_ID = k.ND_ID " + "JOIN nguoidung n ON n.ND_ID = k.ND_ID "
			+ "WHERE hd.KH_ID IS NOT NULL "
			+ "AND (:maHoaDon IS NULL OR CAST(hd.HD_ID AS CHAR) LIKE CONCAT('%', :maHoaDon, '%')) "
			+ "AND (:tt = 0 OR t.TT_ID = :tt) "
			+ "AND (:tenOrSdt IS NULL OR n.ND_TEN LIKE CONCAT('%', :tenOrSdt, '%') OR n.ND_SDT LIKE CONCAT('%', :tenOrSdt, '%')) "
			+ "AND (:ngayLap IS NULL OR DATE(hd.HD_NGAYLAP) = :ngayLap) " + "ORDER BY "
			+ "CASE WHEN :sort = 1 THEN hd.HD_NGAYLAP END DESC, " + "CASE WHEN :sort = 2 THEN hd.HD_NGAYLAP END ASC, "
			+ "CASE WHEN :sort = 3 THEN hd.HD_TONGTIEN END ASC, "
			+ "CASE WHEN :sort = 4 THEN hd.HD_TONGTIEN END DESC", nativeQuery = true)
	Page<HoaDonOnline> findHoaDonOnlineByFilters(@Param("maHoaDon") String maHoaDon, @Param("tt") Long tt,
			@Param("tenOrSdt") String tenOrSdt, @Param("ngayLap") LocalDate ngayLap, @Param("sort") int sort,
			Pageable pageable);


	


	
	@Query("SELECT h FROM HoaDonOnline h WHERE h.ghnCode = :ghnCode")
	HoaDonOnline findByGhnCode(@Param("ghnCode") String ghnCode);

}
