package com.example.e_commerce.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.PhieuKiemHang;
import com.example.e_commerce.model.SanPhamKiem;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PhieuKiemRepository extends JpaRepository<PhieuKiemHang, Long> {
//	@Query(value = "SELECT * FROM phieukiemhang p " + "JOIN nguoidung n ON p.NV_ID = n.ND_ID "
//			+ "WHERE n.ND_TEN LIKE CONCAT('%', :ten, '%') " + "AND p.PKH_NGAYLAP >= :nbd "
//			+ "AND p.PKH_NGAYLAP <= :nkt " + "AND ( :active = 0 OR "
//			+ "      (:active = 1 AND p.PKH_DAXACNHAN = true) OR "
//			+ "      (:active = -1 AND p.PKH_DAXACNHAN = false) ) " + "ORDER BY p.PKH_NGAYLAP DESC", nativeQuery = true)
//	Page<PhieuKiemHang> findByNguoiDungTenAndNgayLapBetween(Pageable p, @Param("ten") String ten,
//			@Param("nbd") LocalDateTime nbd, @Param("nkt") LocalDateTime nkt, @Param("active") Integer active);

	//thay đổi thử
	@Query(
		    value = "SELECT DISTINCT p.* FROM phieukiemhang p " +
		            "JOIN nguoidung n ON p.NV_ID = n.ND_ID " +
		            "JOIN sanphamkiem s ON p.PKH_ID = s.PKH_ID " +
		            "JOIN bienthe b ON b.BT_ID = s.BT_ID " +
		            "JOIN sanpham l ON l.SP_ID = b.SP_ID " +
		            "WHERE (l.SP_TEN LIKE CONCAT('%', :mavach, '%') OR b.BT_MAVACH LIKE CONCAT('%', :mavach, '%')) " +
		            "AND n.ND_TEN LIKE CONCAT('%', :ten, '%') " +
		            "AND p.PKH_NGAYLAP >= :nbd " +
		            "AND p.PKH_NGAYLAP <= :nkt " +
		            "AND ( :active = 0 OR " +
		            "      (:active = 1 AND p.PKH_DAXACNHAN = true) OR " +
		            "      (:active = -1 AND p.PKH_DAXACNHAN = false) ) " +
		            "ORDER BY p.PKH_NGAYLAP DESC",
		    countQuery = "SELECT COUNT(DISTINCT p.PKH_ID) FROM phieukiemhang p " +
		                 "JOIN nguoidung n ON p.NV_ID = n.ND_ID " +
		                 "JOIN sanphamkiem s ON p.PKH_ID = s.PKH_ID " +
		                 "JOIN bienthe b ON b.BT_ID = s.BT_ID " +
		                 "JOIN sanpham l ON l.SP_ID = b.SP_ID " +
		                 "WHERE (l.SP_TEN LIKE CONCAT('%', :mavach, '%') OR b.BT_MAVACH LIKE CONCAT('%', :mavach, '%')) " +
		                 "AND n.ND_TEN LIKE CONCAT('%', :ten, '%') " +
		                 "AND p.PKH_NGAYLAP >= :nbd " +
		                 "AND p.PKH_NGAYLAP <= :nkt " +
		                 "AND ( :active = 0 OR " +
		                 "      (:active = 1 AND p.PKH_DAXACNHAN = true) OR " +
		                 "      (:active = -1 AND p.PKH_DAXACNHAN = false) )",
		    nativeQuery = true
		)
		Page<PhieuKiemHang> findByNguoiDungTenAndNgayLapBetween(
		    Pageable pageable,
		    @Param("ten") String ten,
		    @Param("mavach") String mavach,
		    @Param("nbd") LocalDateTime nbd,
		    @Param("nkt") LocalDateTime nkt,
		    @Param("active") Integer active
		);
	
	@Query(
		    value = "SELECT DISTINCT b.* FROM sanphamkiem s " +
		            "JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID " +
		            "JOIN nguoidung n ON p.NV_ID = n.ND_ID " +
		            "JOIN bienthe b ON b.BT_ID = s.BT_ID " +
		            "JOIN sanpham l ON l.SP_ID = b.SP_ID " +
		            "WHERE (l.SP_TEN LIKE CONCAT('%', :mavach, '%') OR b.BT_MAVACH LIKE CONCAT('%', :mavach, '%')) " +
		            "AND n.ND_TEN LIKE CONCAT('%', :ten, '%') " +
		            "AND p.PKH_NGAYLAP >= :nbd " +
		            "AND p.PKH_NGAYLAP <= :nkt " +
		            "AND ( :active = 0 OR " +
		            "      (:active = 1 AND p.PKH_DAXACNHAN = true) OR " +
		            "      (:active = -1 AND p.PKH_DAXACNHAN = false) ) " +
		            "ORDER BY p.PKH_NGAYLAP ASC",
		    countQuery = "SELECT COUNT(DISTINCT b.BT_ID) FROM sanphamkiem s " +
		                 "JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID " +
		                 "JOIN nguoidung n ON p.NV_ID = n.ND_ID " +
		                 "JOIN bienthe b ON b.BT_ID = s.BT_ID " +
		                 "JOIN sanpham l ON l.SP_ID = b.SP_ID " +
		                 "WHERE (l.SP_TEN LIKE CONCAT('%', :mavach, '%') OR b.BT_MAVACH LIKE CONCAT('%', :mavach, '%')) " +
		                 "AND n.ND_TEN LIKE CONCAT('%', :ten, '%') " +
		                 "AND p.PKH_NGAYLAP >= :nbd " +
		                 "AND p.PKH_NGAYLAP <= :nkt " +
		                 "AND ( :active = 0 OR " +
		                 "      (:active = 1 AND p.PKH_DAXACNHAN = true) OR " +
		                 "      (:active = -1 AND p.PKH_DAXACNHAN = false) )",
		    nativeQuery = true
		)
		Page<BienThe> findDistinctBienTheByDieuKien(
		    Pageable pageable,
		    @Param("ten") String ten,
		    @Param("mavach") String mavach,
		    @Param("nbd") LocalDateTime nbd,
		    @Param("nkt") LocalDateTime nkt,
		    @Param("active") Integer active
		);
	
	@Query(value = "SELECT b.BT_ID FROM bienthe b JOIN sanphamkiem s ON s.BT_ID=b.BT_ID JOIN phieukiemhang p ON p.PKH_ID=s.PKH_ID \r\n"
			+ "WHERE p.PKH_DAXACNHAN=0 AND p.PKH_ID != :id",nativeQuery = true)
	public List<Integer> getBienTheDangTrongChoKhauHao(long id);

}
