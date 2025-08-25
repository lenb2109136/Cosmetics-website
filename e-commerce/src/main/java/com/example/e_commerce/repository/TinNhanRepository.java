package com.example.e_commerce.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.TinNhan;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface TinNhanRepository extends JpaRepository<TinNhan, Long> {
	@Query("""
			    SELECT kh, tn
			    FROM KhachHang kh
			    LEFT JOIN TinNhan tn ON tn.khachHang = kh AND tn.ngayGioNhan = (
			        SELECT MAX(tn2.ngayGioNhan)
			        FROM TinNhan tn2
			        WHERE tn2.khachHang = kh
			    )
			    ORDER BY tn.ngayGioNhan DESC NULLS LAST
			""")
	List<Object[]> getKhachHangVaTinNhanMoiNhat();

	@Query(value = "SELECT * FROM tinnhan t WHERE t.TN_STATUSNHANVIEN=0", nativeQuery = true)
	public List<TinNhan> getTinNhanChuaNhanNhanVien();

	@Query(value = "SELECT * FROM tinnhan t " + "WHERE t.KH_ID = :id " + "AND t.TN_STATUSNHANVIEN IN (:statuses) "
			+ "AND (:isNhanVien = false OR t.NV_ID IS NULL)", nativeQuery = true)
	List<TinNhan> getTinNhanChuaCheckOfNguoiDung(@Param("id") long id, @Param("statuses") List<Integer> statuses,
			@Param("isNhanVien") boolean isNhanVien);
	
	@Query(value = "SELECT * FROM tinnhan t " + "WHERE t.KH_ID = :id " + "AND t.TN_STATUSKHACHHANG IN (:statuses) "
			+ "AND  t.NV_ID IS NOT NULL", nativeQuery = true)
	List<TinNhan> getTinNhanChuaCheckOfNguoiDungKhach(@Param("id") long id, @Param("statuses") List<Integer> statuses
			);
	

	@Query(value = "SELECT * FROM tinnhan t WHERE t.KH_ID=:id AND t.TN_ID < :idtn ORDER BY t.TN_NGAYGIONHAN DESC ", nativeQuery = true)
	public List<TinNhan> getTinNhanOfKhachHang(org.springframework.data.domain.Pageable page, @Param("id") long id,
			@Param("idtn") long idtn);
}
