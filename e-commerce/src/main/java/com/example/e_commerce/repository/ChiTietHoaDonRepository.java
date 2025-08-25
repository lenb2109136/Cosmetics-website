package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.embeded.EChiTietHoaDon;

@Repository
public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon,EChiTietHoaDon>{
	@Query(value = "SELECT c.* FROM hoadon h "
	        + "JOIN chitiethoadon c ON c.HD_ID = h.HD_ID "
	        + "WHERE c.BT_ID IN (:sp) AND h.HD_NGAYLAP >= :bd AND h.HD_NGAYLAP <= :kt AND h.TT_ID IN (:dsTrangThai)", nativeQuery = true)
	public List<ChiTietHoaDon> getChiTietHoaDonOfSanPham(
	        @Param("sp") List<Integer> sp,
	        @Param("bd") LocalDateTime bd,
	        @Param("kt") LocalDateTime kt,
	        @Param("dsTrangThai") List<Integer> dsTrangThai
	);
	
	@Query(value = "SELECT SUM(c.CTHD_SOLUONG) FROM chitiethoadon c JOIN hoadon p ON c.HD_ID = p.HD_ID " +
            "WHERE c.BT_ID = :id AND p.HD_NGAYLAP <= :nkt AND p.HD_NGAYLAP >= :nbd AND p.TT_ID IN :ds",
    nativeQuery = true)
	public Long soluongBanRa(@Param("id") int id,
    @Param("nbd") LocalDateTime nbd,
    @Param("nkt") LocalDateTime nkt,
    @Param("ds") List<Integer> ds);
	
	@Query(value = "SELECT SUM(c.CTHD_SOLUONG) FROM chitiethoadon c JOIN hoadon p ON c.HD_ID = p.HD_ID " +
            "WHERE c.BT_ID IN (:id) AND p.HD_NGAYLAP <= :nkt AND p.HD_NGAYLAP >= :nbd AND p.TT_ID IN :ds",
    nativeQuery = true)
	public Long soluongBanRa(@Param("id") List<Integer> id,
    @Param("nbd") LocalDateTime nbd,
    @Param("nkt") LocalDateTime nkt,
    @Param("ds") List<Integer> ds);
	
	@Query(value = "SELECT c.* FROM chitiethoadon c " +
            "JOIN hoadon p ON c.HD_ID = p.HD_ID " +
            "WHERE c.BT_ID = :id AND p.HD_NGAYLAP BETWEEN :nbd AND :nkt " +
            "AND p.TT_ID IN :ds " +
            "ORDER BY p.HD_NGAYLAP ASC", nativeQuery = true)
public List<ChiTietHoaDon> getChiTietHoaDonOfBienThe(
    @Param("id") int id,
    @Param("nbd") LocalDateTime nbd,
    @Param("nkt") LocalDateTime nkt,
    @Param("ds") List<Integer> ds);


	@Query(value = "SELECT c.* FROM chitiethoadon c " +
	        "JOIN hoadon p ON c.HD_ID = p.HD_ID JOIN apdungkhuyenmai a ON a.BT_ID=c.BT_ID " +
	        "WHERE c.BT_ID IN (:id) " +
	        "AND p.TT_ID IN :ds AND a.KM_ID= :idfl " +
	        "ORDER BY p.HD_NGAYLAP ASC", nativeQuery = true)
	public List<ChiTietHoaDon> getChiTietHoaDonOfBienTheWhereIdBienTheIn(
	    @Param("id") List<Integer> id,  
	    @Param("ds") List<Integer> ds,
	    @Param("idfl") long idFlashsale
			);
	@Query(value = "SELECT c.* FROM chitiethoadon c " +
	        "JOIN hoadon p ON c.HD_ID = p.HD_ID  JOIN apdungkhuyenmai a ON a.BT_ID=c.BT_ID " +
	        "WHERE c.BT_ID IN (:id) " +
	        "AND p.TT_ID IN :ds AND a.KM_ID= :idfl " +
	        "ORDER BY p.HD_NGAYLAP ASC", nativeQuery = true)
	public List<ChiTietHoaDon> getChiTietHoaDonOfBienTheWhereIdBienTheInOfDeal(
	    @Param("id") List<Integer> id,  
	    @Param("ds") List<Integer> ds,
	    @Param("idfl") long idFlashsale
			);
	
	
	@Query(value = "SELECT count(*) FROM chitiethoadon c " +
	        "JOIN hoadon p ON c.HD_ID = p.HD_ID " +
	        "WHERE c.BT_ID = :id AND p.KH_ID=:idkh AND p.HD_NGAYLAP <:bd " +
	        "AND p.TT_ID IN :ds " +
	        "ORDER BY p.HD_NGAYLAP ASC", nativeQuery = true)
	public Long getChiTietHoaDonOfBienTheWhereIdBienTheInNguoiDungUse(
	    @Param("id") int id,  
	    @Param("bd") LocalDateTime bd,
	    @Param("ds") List<Integer> ds,
	    @Param("idkh") long idkh
	    );

	
	
	@Query(value = "SELECT c.* FROM chitiethoadon c " +
	        "JOIN hoadon h ON h.HD_ID = c.HD_ID " +
	        "JOIN bienthe b ON b.BT_ID = c.BT_ID " +
	        "JOIN sanpham sp ON sp.SP_ID = b.SP_ID " +
	        "JOIN khachhang k ON k.ND_ID = h.KH_ID " +
	        "WHERE k.ND_ID = :id AND h.HD_NGAYLAP >= :bd AND h.HD_NGAYLAP <= :kt AND sp.SP_ID = :idsp", nativeQuery = true)
	public List<ChiTietHoaDon> getChiTietHoaDonOfSanPhamInTime(
	        @Param("id") Long id,
	        @Param("bd") LocalDateTime bd,
	        @Param("kt") LocalDateTime kt,
	        @Param("idsp") Long idsp
	);
	@Query(value = "SELECT COUNT(*) FROM chitiethoadon c JOIN hoadon h ON h.HD_ID=c.HD_ID JOIN khachhang "
			+ "k ON h.KH_ID=k.ND_ID WHERE h.TT_ID=1 AND k.ND_ID=:idkh AND c.BT_ID IN (:id)",nativeQuery = true)
	public Long kiemTraKhachHangDaMuaThanhConChua(@Param("id") List<Integer> id,@Param("idkh") long idkh);

	
}
