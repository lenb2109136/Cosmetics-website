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
import com.example.e_commerce.model.KhuyenMai;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long>{
	@Query(value = "SELECT k.*,d.* FROM deal d JOIN khuyenmai k ON k.KM_ID=d.KM_ID JOIN bienthedealchinh b "
			+ "ON b.KM_ID=d.KM_ID \r\n"
			+ "WHERE d.D_SOLUONGGIOIHAN-d.D_SOLUONGDADUNG>0 AND k.KM_CONSUDUNG=TRUE "
			+ "AND b.BTDC_CONSUDUNG=TRUE AND k.KM_THOIGIANNGUNG>NOW() AND b.BT_ID=:id",nativeQuery = true)
	public List<Deal> getKhuyenMaiDealChinhOfBienThe(int id);
	@Query(value = "SELECT k.*,d.* FROM deal d JOIN khuyenmai k ON k.KM_ID=d.KM_ID "
			+ "JOIN bienthedealphu b ON b.KM_ID=d.KM_ID \r\n"
			+ "WHERE d.D_SOLUONGGIOIHAN-d.D_SOLUONGDADUNG>0 AND k.KM_CONSUDUNG=TRUE "
			+ "AND b.BTDP_CONSUDUNG=TRUE AND k.KM_THOIGIANNGUNG>NOW() AND b.BT_ID=:id",nativeQuery = true)
	public List<Deal> getKhuyenMaiDealPhuOfBienThe(int id);
	
	
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM deal f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANAPDUNG >= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<Deal> getDealBeforNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM deal f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG >= NOW() AND k.KM_THOIGIANAPDUNG <= NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd))" ,nativeQuery = true)
	public Page<Deal> getDealInNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
	@Query(value = "SELECT f.*, k.KM_THOIGIANAPDUNG, k.KM_THOIGIANNGUNG, k.KM_CONSUDUNG " +
            "FROM deal f " +
            "JOIN khuyenmai k ON k.KM_ID = f.KM_ID " +
            "WHERE k.KM_THOIGIANNGUNG < NOW() " +
            "AND ((k.KM_THOIGIANNGUNG <=:kt AND k.KM_THOIGIANNGUNG >= :bd) OR (k.KM_THOIGIANAPDUNG <=:kt AND k.KM_THOIGIANAPDUNG >= :bd)) " ,nativeQuery = true)
	public Page<Deal> getDealAfterNow(LocalDateTime bd, LocalDateTime kt,Pageable pageable);
}
