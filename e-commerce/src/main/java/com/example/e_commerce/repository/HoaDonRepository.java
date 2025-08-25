package com.example.e_commerce.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon,Long>{
	@Query("SELECT h FROM HoaDon h WHERE h.khachHang.id = :kh AND h.trangThai.id =:tt")
	List<HoaDon> getHoaDonByStatus(@Param("kh") long kh, @Param("tt") long tt);
	
	
	@Query("SELECT h FROM HoaDonOnline h WHERE h.khachHang.id = :kh "
			+ "AND h.trangThai.id IN (:tt) "
+"AND (\r\n"
+ "			        (:ngayLap IS NULL OR :ngayLapKT IS NULL) \r\n"
+ "			        OR (h.ngayLap BETWEEN :ngayLap AND :ngayLapKT)\r\n"
+ "			      ) "
			+ "AND h.id in (select d.hoaDon.id from ChiTietHoaDon d where d.bienThe.sanPham.ten LIKE %:pp%)"
			+ "ORDER BY h.ngayLap DESC")
	Page<HoaDonOnline> getHoaDonByStatusV2(
	        @Param("kh") int kh, 
	        @Param("ngayLap") LocalDateTime ngayLap, 
	        @Param("ngayLapKT") LocalDateTime ngayLapKT,
	        @Param("tt") List<Long> tt,
	        @Param("pp") String pp,
	        Pageable p);


	
	
	@Modifying
	@Transactional
	@Query(value = """
	       DELETE h FROM hoadonhaohut h 
	       JOIN phieukiemhang k ON k.PKH_ID = h.PKH_ID
	       WHERE h.HD_ID = :hoaDonId AND k.PKH_DAXACNHAN = false
	   """, nativeQuery = true)
	 public void deleteByHoaDonIdAndPhieuChuaXacNhan(@Param("hoaDonId") Long hoaDonId);
	
	
	@Query(value = "SELECT COUNT(*) FROM phieukiemhang h JOIN hoadonhaohut hh WHERE h.PKH_DAXACNHAN=0 AND hh.HD_ID=:id",nativeQuery = true)
	public int kiemTraCoDangDuocXoaKhong(long id);
	
}
