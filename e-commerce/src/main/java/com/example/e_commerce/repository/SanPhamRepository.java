package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.model.QuyCachDongGoiLanNhap;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.SanPhamKiem;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long>{
	@Modifying
	@Query(value = "DELETE FROM sanpham_thongsocuthe WHERE SP_ID IN (:idsp) AND TSCT_ID IN (:idtsct)", nativeQuery = true)
	public void deleteSanPhamThongSoBySanPham(@Param("idsp") List<Long> idsp, @Param("idtsct") List<Long> idtsct);
	
	
	
//	@Query(value = "SELECT DISTINCT b.* FROM sanpham s " +
//	        "JOIN bienthe b ON b.SP_ID = s.SP_ID " +
//	        "WHERE s.SP_ID = :id AND b.BT_TEN = 'DEFAULT_SYSTEM_NAME_CLASSIFY'", 
//	       nativeQuery = true)
//	BienThe getBienTheOfSanPhamDefault(
//	        @Param("id") Long id
//	);
//
//	@Query(value = "SELECT DISTINCT b.* FROM sanpham s " +
//	        "JOIN bienthe b ON b.SP_ID = s.SP_ID " +
//	        "WHERE s.SP_ID = :id AND b.BT_CONSUDUNG = :conSuDung AND b.BT_TEN != 'DEFAULT_SYSTEM_NAME_CLASSIFY'", 
//	       nativeQuery = true)
//	List<BienThe> getBienTheOfSanPhamNotDefault(
//	        @Param("id") Long id,
//	        @Param("conSuDung") boolean conSuDung
//	);
	
	@Query(value = "SELECT * FROM sanpham WHERE SP_CONDUNG = :c AND (:id = 0 OR DMM_ID = :id) AND SP_TEN LIKE %:ten%", nativeQuery = true)
	Page<SanPham> getAllSanPham(@Param("c") boolean c, @Param("id") int id, @Param("ten") String ten, Pageable page);
	@Query(value = "SELECT * FROM sanpham WHERE (:id = 0 OR DMM_ID = :id) AND SP_TEN LIKE %:ten%", nativeQuery = true)
	Page<SanPham> getAllSanPhamNotCondition(@Param("id") int id, @Param("ten") String ten, Pageable page);
	@Query(value = "SELECT DISTINCT s.* FROM sanpham s JOIN bienthe b " +
	        "ON b.SP_ID = s.SP_ID " +
	        "WHERE s.DMM_ID IN (:id)\r\n"
	        + " " +
	        "AND s.SP_TEN LIKE CONCAT('%', :ten, '%') " +
	        "AND s.SP_CONDUNG = :c " +
	        "AND ( " +
	        "  :isHetHang = 0 " +
	        "  OR (:isHetHang = 1 AND b.BT_SOLUONG >= :sl AND b.BT_SOLUONG <= :max) " +
	        "  OR (:isHetHang = 2 AND b.BT_SOLUONG = 0) " +
	        ")",
	        countQuery = "SELECT COUNT(DISTINCT s.SP_ID) FROM sanpham s JOIN bienthe b " +
	        "ON b.SP_ID = s.SP_ID " +
	        "WHERE s.DMM_ID IN (:id) " +
	        "AND s.SP_TEN LIKE CONCAT('%', :ten, '%') " +
	        "AND s.SP_CONDUNG = :c " +
	        "AND ( " +
	        "  :isHetHang = 0 " +
	        "  OR (:isHetHang = 1 AND b.BT_SOLUONG >= :sl AND b.BT_SOLUONG <= :max) " +
	        "  OR (:isHetHang = 2 AND b.BT_SOLUONG = 0) " +
	        ")",
	        nativeQuery = true)
	Page<SanPham> getSanPhamFilter(@Param("c") boolean c,
	                                @Param("id") List<Integer> id,
	                                @Param("ten") String ten,
	                                @Param("sl") int sl,
	                                @Param("max") int max,
	                                @Param("isHetHang") int isHetHang,
	                                Pageable page);
	@Query("SELECT c.bienThe, c.phieuNhap.ngayNhapHang, c.soLuong, c.donGia, c.phieuNhap.id, c.phieuNhap.donViCungCap.ten, c.bienThe.sanPham.ten " +
		       "FROM ChiTietPhieuNhap c " +
		       "WHERE c.phieuNhap.ngayNhapHang >= :nbd " +
		       "AND c.phieuNhap.ngayNhapHang <= :nkt " +
		       "AND c.phieuNhap.donViCungCap.ten LIKE CONCAT('%', :tendvcc, '%') " +
		       "AND c.bienThe.sanPham.ten LIKE CONCAT('%', :tensp, '%') " +
		       "ORDER BY c.bienThe.sanPham.ten ASC, c.phieuNhap.donViCungCap.ten ASC")
		Page<Object[]> getBienTheDonViCungCap(
		       @Param("nbd") LocalDateTime nbd, 
		       @Param("nkt") LocalDateTime nkt, 
		       @Param("tendvcc") String tendvcc,
		       @Param("tensp") String tensp,
		       Pageable pageable
		);
		
		@Query(value = "SELECT b.* FROM bienthe b JOIN sanpham s ON s.SP_ID = b.SP_ID"
				+ " WHERE s.SP_TEN LIKE CONCAT('%', :tensp, '%') "
				+ "OR b.BT_MAVACH LIKE CONCAT('%', :tensp, '%')"
				+ "    AND b.BT_CONSUDUNG = 1 \r\n"
				+ "    AND s.SP_CONDUNG = 1 \r\n",nativeQuery = true)
		public List<BienThe> getBienTheByMaVach(@Param("tensp") String tensp);
		
		@Query(value = "SELECT q.*\r\n"
				+ "		FROM bienthequycachdonggoi q\r\n"
				+ "		JOIN bienthe b ON q.BT_ID=b.BT_ID\r\n"
				+ "		JOIN sanpham s ON s.SP_ID = b.SP_ID\r\n"
				+ "		WHERE \r\n"
				+ "		    (\r\n"
				+ "		        s.SP_TEN LIKE CONCAT('%', :tensp, '%') \r\n"
				+ "		        OR q.MAVACH LIKE CONCAT('%', :tensp, '%')\r\n"
				+ "		    )\r\n"
				+ "		    AND b.BT_CONSUDUNG = 1\r\n"
				+ "		    AND s.SP_CONDUNG = 1;",nativeQuery = true)
		public List<BienTheDongGoiChan> getBienTheByMaVachForPhieuNhap(@Param("tensp") String tensp);
		@Query(value = "SELECT q.*\n"
		        + "FROM bienthequycachdonggoi q\n"
		        + "JOIN bienthe b ON q.BT_ID = b.BT_ID\n"
		        + "JOIN sanpham s ON s.SP_ID = b.SP_ID\n"
		        + "WHERE q.MAVACH = :mavach\n"
		        + "AND b.BT_CONSUDUNG = 1\n"
		        + "AND s.SP_CONDUNG = 1;", nativeQuery = true)
		public List<BienTheDongGoiChan> getByMaVachForPhieuNhapCorrect(@Param("mavach") String mavach);


		@Query(value = "SELECT b.* \r\n"
				+ "FROM bienthe b \r\n"
				+ "JOIN sanpham s ON s.SP_ID = b.SP_ID \r\n"
				+ " WHERE s.SP_TEN LIKE CONCAT('%', :tensp, '%') "
				+ "OR b.BT_MAVACH LIKE CONCAT('%', :tensp, '%')"
				+ "    AND b.BT_CONSUDUNG = 1 \r\n"
				+ "    AND s.SP_CONDUNG = 1 \r\n "
				+ "    AND b.BT_SOLUONG > 0 \r\n "
				+ "", nativeQuery = true)
		
		public List<BienThe> getBienTheByMaVachForPhieuKiem(
				@Param("tensp") String tensp);
		@Query(value = """
			    SELECT b.*
			    FROM bienthe b 
			    LEFT JOIN chitiethoadon c ON c.BT_ID = b.BT_ID 
			    LEFT JOIN hoadon h ON h.HD_ID = c.HD_ID 
			    JOIN sanpham s ON s.SP_ID = b.SP_ID 
			    WHERE 
			        (s.SP_TEN = :tensp OR b.BT_MAVACH = :tensp)
			        AND b.BT_CONSUDUNG = 1 
			        AND s.SP_CONDUNG = 1 
			        AND (
			            h.HD_ID IS NULL OR 
			            (
			                (h.TT_ID = 2 OR h.TT_ID = 3 OR h.TT_ID = 13) 
			                AND (COALESCE(c.CTHD_SOLUONG, 0) + COALESCE(b.BT_SOLUONG, 0)) > 0
			            )
			        )
			    """, nativeQuery = true)
			List<BienThe> getBienTheByMaVachCorrect(@Param("tensp") String tensp);


//		@Query(value = "SELECT DISTINCT s.*\n"
//		        + "FROM sanpham s\n"
//		        + "JOIN (\n"
//		        + "    SELECT b.SP_ID, MIN(b.BT_GIA) AS gia_thap_nhat\n"
//		        + "    FROM bienthe b\n"
//		        + "    WHERE b.BT_CONSUDUNG = TRUE AND b.BT_SOLUONG > 0\n"
//		        + "    GROUP BY b.SP_ID\n"
//		        + ") b_min ON b_min.SP_ID = s.SP_ID\n"
//		        + "JOIN thuonghieu t ON t.TH_ID = s.TH_ID\n"
//		        + "JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID\n"
//		        + "WHERE (:idThongSoCuTheSize = 0 OR st.TSCT_ID IN (:idThongSoCuThe))\n"
//		        + "AND s.SP_TEN LIKE CONCAT('%', :ten, '%')\n"
//		        + "AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))\n"
//		        + "AND b_min.gia_thap_nhat > :giaBD\n"
//		        + "AND b_min.gia_thap_nhat <= :giaKetThuc\n"
//		        + "AND s.DMM_ID IN (:idDanhMuc)\n"
//		        + "AND s.SP_CONDUNG = TRUE\n"
//		        + "ORDER BY b_min.gia_thap_nhat ASC",
//		       
//		        countQuery = "SELECT COUNT(DISTINCT s.SP_ID)\n"
//		        + "FROM sanpham s\n"
//		        + "JOIN (\n"
//		        + "    SELECT b.SP_ID, MIN(b.BT_GIA) AS gia_thap_nhat\n"
//		        + "    FROM bienthe b\n"
//		        + "    WHERE b.BT_CONSUDUNG = TRUE AND b.BT_SOLUONG > 0\n"
//		        + "    GROUP BY b.SP_ID\n"
//		        + ") b_min ON b_min.SP_ID = s.SP_ID\n"
//		        + "JOIN thuonghieu t ON t.TH_ID = s.TH_ID\n"
//		        + "JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID\n"
//		        + "WHERE (:idThongSoCuTheSize = 0 OR st.TSCT_ID IN (:idThongSoCuThe))\n"
//		        + "AND s.SP_TEN LIKE CONCAT('%', :ten, '%')\n"
//		        + "AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))\n"
//		        + "AND b_min.gia_thap_nhat > :giaBD\n"
//		        + "AND b_min.gia_thap_nhat <= :giaKetThuc\n"
//		        + "AND s.DMM_ID IN (:idDanhMuc)\n"
//		        + "AND s.SP_CONDUNG = TRUE",
//		        
//		        nativeQuery = true)
//		Page<SanPham> getForViewProductASC(
//		        @Param("idThongSoCuThe") List<Long> idThongSoCuThe,
//		        @Param("idThongSoCuTheSize") int idThongSoCuTheSize,
//		        @Param("idThuongHieu") List<Long> idThuongHieu,
//		        @Param("idThuongHieuSize") int idThuongHieuSize,
//		        @Param("giaBD") float giaBD,
//		        @Param("giaKetThuc") float giaKetThuc,
//		        @Param("idDanhMuc") List<Integer> idDanhMuc,
//		        @Param("ten") String ten,
//		        Pageable pageable);
		@Query(value = """
			    SELECT DISTINCT s.*, b_min.gia_thap_nhat AS debug_price
			    FROM sanpham s
			    JOIN (
			        SELECT b.SP_ID, MIN(ROUND(b.BT_GIA, 2)) AS gia_thap_nhat
			        FROM bienthe b
			        LEFT JOIN (
			            SELECT s.BT_ID, SUM(s.PKH_SOLUONG) AS tong_hao_hut
			            FROM sanphamkiem s
			            JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID
			            WHERE p.PKH_DAXACNHAN = 0
			            GROUP BY s.BT_ID
			        ) hao_hut ON hao_hut.BT_ID = b.BT_ID
			        WHERE b.BT_CONSUDUNG = TRUE
			          AND (b.BT_SOLUONG - IFNULL(hao_hut.tong_hao_hut, 0)) > 0
			          AND b.BT_GIA IS NOT NULL
			          AND b.BT_GIA > 0
			        GROUP BY b.SP_ID
			    ) b_min ON b_min.SP_ID = s.SP_ID
			    JOIN thuonghieu t ON t.TH_ID = s.TH_ID
			    JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
			    WHERE (:idThongSoCuTheSize = 0 OR (
			        s.SP_ID IN (
			            SELECT st2.SP_ID
			            FROM sanpham_thongsocuthe st2
			            WHERE st2.TSCT_ID IN (:idThongSoCuThe)
			            GROUP BY st2.SP_ID
			            HAVING COUNT(DISTINCT st2.TSCT_ID) = :idThongSoCuTheSize
			        )
			    ))
			      AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
			      AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
			      AND b_min.gia_thap_nhat > :giaBD
			      AND b_min.gia_thap_nhat <= :giaKetThuc
			      AND s.DMM_ID IN (:idDanhMuc)
			      AND s.SP_CONDUNG = TRUE
			    ORDER BY b_min.gia_thap_nhat ASC
			""",

			countQuery = """
			    SELECT COUNT(DISTINCT s.SP_ID)
			    FROM sanpham s
			    JOIN (
			        SELECT b.SP_ID
			        FROM bienthe b
			        LEFT JOIN (
			            SELECT s.BT_ID, SUM(s.PKH_SOLUONG) AS tong_hao_hut
			            FROM sanphamkiem s
			            JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID
			            WHERE p.PKH_DAXACNHAN = 0
			            GROUP BY s.BT_ID
			        ) hao_hut ON hao_hut.BT_ID = b.BT_ID
			        WHERE b.BT_CONSUDUNG = TRUE
			          AND (b.BT_SOLUONG - IFNULL(hao_hut.tong_hao_hut, 0)) > 0
			          AND b.BT_GIA IS NOT NULL
			          AND b.BT_GIA > 0
			        GROUP BY b.SP_ID
			    ) b_min ON b_min.SP_ID = s.SP_ID
			    JOIN thuonghieu t ON t.TH_ID = s.TH_ID
			    JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
			    WHERE (:idThongSoCuTheSize = 0 OR (
			        s.SP_ID IN (
			            SELECT st2.SP_ID
			            FROM sanpham_thongsocuthe st2
			            WHERE st2.TSCT_ID IN (:idThongSoCuThe)
			            GROUP BY st2.SP_ID
			            HAVING COUNT(DISTINCT st2.TSCT_ID) = :idThongSoCuTheSize
			        )
			    ))
			      AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
			      AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
			      AND s.DMM_ID IN (:idDanhMuc)
			      AND s.SP_CONDUNG = TRUE
			""",

			nativeQuery = true)
			Page<SanPham> getForViewProductASC(
			    @Param("idThongSoCuThe") List<Long> idThongSoCuThe,
			    @Param("idThongSoCuTheSize") int idThongSoCuTheSize,
			    @Param("idThuongHieu") List<Long> idThuongHieu,
			    @Param("idThuongHieuSize") int idThuongHieuSize,
			    @Param("giaBD") float giaBD,
			    @Param("giaKetThuc") float giaKetThuc,
			    @Param("idDanhMuc") List<Integer> idDanhMuc,
			    @Param("ten") String ten,
			    Pageable pageable);




//		@Query(
//			    value = """
//			        SELECT DISTINCT s.*
//			        FROM sanpham s
//			        JOIN (
//			            SELECT b.SP_ID, MIN(b.BT_GIA) AS gia_thap_nhat
//			            FROM bienthe b
//			            WHERE b.BT_CONSUDUNG = TRUE AND b.BT_SOLUONG > 0
//			            GROUP BY b.SP_ID
//			        ) b_min ON b_min.SP_ID = s.SP_ID
//			        JOIN thuonghieu t ON t.TH_ID = s.TH_ID
//			        JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
//			        WHERE (:idThongSoCuTheSize = 0 OR st.TSCT_ID IN (:idThongSoCuThe))
//			          AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
//			          AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
//			          AND b_min.gia_thap_nhat > :giaBD
//			          AND b_min.gia_thap_nhat <= :giaKetThuc
//			          AND s.DMM_ID IN (:idDanhMuc)
//			          AND s.SP_CONDUNG = TRUE
//			        ORDER BY b_min.gia_thap_nhat DESC
//			        """,
//			    countQuery = """
//			        SELECT COUNT(DISTINCT s.SP_ID)
//			        FROM sanpham s
//			        JOIN (
//			            SELECT b.SP_ID, MIN(b.BT_GIA) AS gia_thap_nhat
//			            FROM bienthe b
//			            WHERE b.BT_CONSUDUNG = TRUE AND b.BT_SOLUONG > 0
//			            GROUP BY b.SP_ID
//			        ) b_min ON b_min.SP_ID = s.SP_ID
//			        JOIN thuonghieu t ON t.TH_ID = s.TH_ID
//			        JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
//			        WHERE (:idThongSoCuTheSize = 0 OR st.TSCT_ID IN (:idThongSoCuThe))
//			          AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
//			          AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
//			          AND b_min.gia_thap_nhat > :giaBD
//			          AND b_min.gia_thap_nhat <= :giaKetThuc
//			          AND s.DMM_ID IN (:idDanhMuc)
//			          AND s.SP_CONDUNG = TRUE
//			        """,
//			    nativeQuery = true
//			)
//			Page<SanPham> getForViewProductDESC(
//			    @Param("idThongSoCuThe") List<Long> idThongSoCuThe,
//			    @Param("idThongSoCuTheSize") int idThongSoCuTheSize,
//			    @Param("idThuongHieu") List<Long> idThuongHieu,
//			    @Param("idThuongHieuSize") int idThuongHieuSize,
//			    @Param("giaBD") float giaBD,
//			    @Param("giaKetThuc") float giaKetThuc,
//			    @Param("idDanhMuc") List<Integer> idDanhMuc,
//			    @Param("ten") String ten,
//			    Pageable pageable
//			);
//		
				@Query(value = """
			    SELECT DISTINCT s.*
			    FROM sanpham s
			    JOIN (
			        SELECT b.SP_ID, MAX(b.BT_GIA) AS gia_cao_nhat
			        FROM bienthe b
			        LEFT JOIN (
			            SELECT s.BT_ID, SUM(s.PKH_SOLUONG) AS tong_hao_hut
			            FROM sanphamkiem s
			            JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID
			            WHERE p.PKH_DAXACNHAN = 0
			            GROUP BY s.BT_ID
			        ) hao_hut ON hao_hut.BT_ID = b.BT_ID
			        WHERE b.BT_CONSUDUNG = TRUE
			          AND (b.BT_SOLUONG - IFNULL(hao_hut.tong_hao_hut, 0)) > 0
			        GROUP BY b.SP_ID
			    ) b_max ON b_max.SP_ID = s.SP_ID
			    JOIN thuonghieu t ON t.TH_ID = s.TH_ID
			    JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
			    WHERE (:idThongSoCuTheSize = 0 OR (
			        s.SP_ID IN (
			            SELECT st2.SP_ID
			            FROM sanpham_thongsocuthe st2
			            WHERE st2.TSCT_ID IN (:idThongSoCuThe)
			            GROUP BY st2.SP_ID
			            HAVING COUNT(DISTINCT st2.TSCT_ID) = :idThongSoCuTheSize
			        )
			    ))
			      AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
			      AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
			      AND b_max.gia_cao_nhat > :giaBD
			      AND b_max.gia_cao_nhat <= :giaKetThuc
			      AND s.DMM_ID IN (:idDanhMuc)
			      AND s.SP_CONDUNG = TRUE
			    ORDER BY b_max.gia_cao_nhat DESC
			""",

			countQuery = """
			    SELECT COUNT(DISTINCT s.SP_ID)
			    FROM sanpham s
			    JOIN (
			        SELECT b.SP_ID
			        FROM bienthe b
			        LEFT JOIN (
			            SELECT s.BT_ID, SUM(s.PKH_SOLUONG) AS tong_hao_hut
			            FROM sanphamkiem s
			            JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID
			            WHERE p.PKH_DAXACNHAN = 0
			            GROUP BY s.BT_ID
			        ) hao_hut ON hao_hut.BT_ID = b.BT_ID
			        WHERE b.BT_CONSUDUNG = TRUE
			          AND (b.BT_SOLUONG - IFNULL(hao_hut.tong_hao_hut, 0)) > 0
			        GROUP BY b.SP_ID
			    ) b_max ON b_max.SP_ID = s.SP_ID
			    JOIN thuonghieu t ON t.TH_ID = s.TH_ID
			    JOIN sanpham_thongsocuthe st ON st.SP_ID = s.SP_ID
			    WHERE (:idThongSoCuTheSize = 0 OR (
			        s.SP_ID IN (
			            SELECT st2.SP_ID
			            FROM sanpham_thongsocuthe st2
			            WHERE st2.TSCT_ID IN (:idThongSoCuThe)
			            GROUP BY st2.SP_ID
			            HAVING COUNT(DISTINCT st2.TSCT_ID) = :idThongSoCuTheSize
			        )
			    ))
			      AND s.SP_TEN LIKE CONCAT('%', :ten, '%')
			      AND (:idThuongHieuSize = 0 OR t.TH_ID IN (:idThuongHieu))
			      AND s.DMM_ID IN (:idDanhMuc)
			      AND s.SP_CONDUNG = TRUE
			""",nativeQuery = true)
			Page<SanPham> getForViewProductDESC(
			    @Param("idThongSoCuThe") List<Long> idThongSoCuThe,
			    @Param("idThongSoCuTheSize") int idThongSoCuTheSize,
			    @Param("idThuongHieu") List<Long> idThuongHieu,
			    @Param("idThuongHieuSize") int idThuongHieuSize,
			    @Param("giaBD") float giaBD,
			    @Param("giaKetThuc") float giaKetThuc,
			    @Param("idDanhMuc") List<Integer> idDanhMuc,
			    @Param("ten") String ten,
			    Pageable pageable);
		@Query(value = "SELECT SUM(c.CTHD_SOLUONG) AS tong_soluong\r\n"
				+ "FROM sanpham s\r\n"
				+ "JOIN bienthe b ON s.SP_ID = b.SP_ID\r\n"
				+ "JOIN chitiethoadon c ON c.BT_ID = b.BT_ID\r\n"
				+ "JOIN hoadon h ON c.HD_ID = h.HD_ID\r\n"
				+ "WHERE s.SP_ID = :id\r\n"
				+ "  AND h.HD_NGAYLAP >= NOW() - INTERVAL 7 DAY;", nativeQuery = true)
		public Long tongBanRa(@Param("id") long id );


		@Modifying
		@Transactional
		@Query(value = "DELETE s " +
		               "FROM sanpham_thongsocuthe s " +
		               "JOIN thongsocuthe t ON s.TSCT_ID = t.TSCT_ID " +
		               "JOIN sanpham s2 ON s2.SP_ID = s.SP_ID " +
		               "WHERE t.TS_ID IN (:idts) AND s2.DMM_ID IN (:iddm)", nativeQuery = true)
		void deleteByThongSoIdsAndDanhMucIds(@Param("idts") List<Long> idts,
		                                     @Param("iddm") List<Integer> iddm);

		@Query(value = """
			    SELECT d.*
			    FROM bienthequycachdonggoi_lannhap d
			    JOIN phieunhap p ON p.PN_ID = d.PN_ID
			    WHERE d.DGC_ID = :dgcId
			      AND d.BT_ID = :btId
			      AND d.BTDGC_SOLUONG = :soLuong
			    ORDER BY p.PN_NGAYNHAPHANG DESC
			    LIMIT 1
			    """, nativeQuery = true)
		public	QuyCachDongGoiLanNhap findLastestNhapHang(
			    @Param("dgcId") int dgcId,
			    @Param("btId") int btId,
			    @Param("soLuong") int soLuong);
		@Query(value = """
			    SELECT p.PKH_ID, s.BT_ID, s.PKH_SOLUONG,s.PKH_GHICHU 
			    FROM sanphamkiem s
			    JOIN phieukiemhang p ON p.PKH_ID = s.PKH_ID
			    WHERE s.BT_ID = :id AND p.PKH_DAXACNHAN = 0
			    """, nativeQuery = true)
			public List<SanPhamKiem> getSanPhamKiem(@Param("id") int id);


}
