package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.KhuyenMaiTangKem;

@Repository
public interface KhuyenMaiTangKemRepository extends JpaRepository<KhuyenMaiTangKem, Long>{
	@Query(value = "SELECT k.*,d.KMTK_GIOIHAN,d.KMTK_SOLUONGDADUNG FROM khuyenmaitangkem d JOIN khuyenmai k ON k.KM_ID=d.KM_ID JOIN sanphamchinh b ON b.KM_ID=d.KM_ID \r\n"
			+ "			WHERE k.KM_CONSUDUNG=TRUE AND b.SPC_CONSUDUNG=TRUE AND k.KM_THOIGIANNGUNG>NOW() AND b.BT_ID=:id",nativeQuery = true)
	public List<KhuyenMaiTangKem> getKhuyenMaiTangKemChinhOfBienThe(int id);
	@Query(value = "SELECT k.*,d.KMTK_GIOIHAN,d.KMTK_SOLUONGDADUNG FROM khuyenmaitangkem d JOIN khuyenmai k ON k.KM_ID=d.KM_ID JOIN sanphamtangkem b ON b.KM_ID=d.KM_ID \r\n"
			+ "			WHERE k.KM_CONSUDUNG=TRUE AND b.SPTK_CONSUDUNG=TRUE AND k.KM_THOIGIANNGUNG>NOW()  AND b.BT_ID=:id",nativeQuery = true)
	public List<KhuyenMaiTangKem> getKhuyenMaiTangKemPhuOfBienThe(int id);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM khuyenmaitangkem f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANAPDUNG >= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<KhuyenMaiTangKem> getFlashSaleBeforNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM khuyenmaitangkem f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG >= NOW() AND k.KM_THOIGIANAPDUNG <= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<KhuyenMaiTangKem> getFlashSaleInNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM khuyenmaitangkem f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG < NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd)) " ,nativeQuery = true)
	public Page<KhuyenMaiTangKem> getFlashSaleAfterNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	
			
}
