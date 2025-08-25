package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.SanPhamKiem;


@Repository
public interface SanPhamKiemRepository extends JpaRepository<SanPhamKiem, Long>{
	
	/// đã xác nhận hao mới tình còn ko thì thôi
	@Query(value = "SELECT s.* FROM phieukiemhang p " +
            "JOIN sanphamkiem s ON p.PKH_ID = s.PKH_ID " +
            "WHERE p.PKH_NGAYLAP > :bd AND p.PKH_NGAYLAP <= :kt " +
            "AND s.BT_ID = :id AND  p.PKH_DAXACNHAN=true", nativeQuery = true)
 public List<SanPhamKiem> getSanPhamKiemOfBienThe(
     @Param("bd") LocalDateTime bd,
     @Param("kt") LocalDateTime kt,
     @Param("id") int id);
	
	@Query(value = "SELECT s.* FROM phieukiemhang p " +
            "JOIN sanphamkiem s ON p.PKH_ID = s.PKH_ID " +
            "WHERE p.PKH_NGAYLAP > :bd AND p.PKH_NGAYLAP <= :kt " +
            "AND s.BT_ID = :id AND  p.PKH_DAXACNHAN=false", nativeQuery = true)
 public List<SanPhamKiem> getSanPhamKiemOfBienTheChuaXacNhan(
     @Param("bd") LocalDateTime bd,
     @Param("kt") LocalDateTime kt,
     @Param("id") int id);
	@Query(value = "SELECT s.* FROM phieukiemhang p " +
            "JOIN sanphamkiem s ON p.PKH_ID = s.PKH_ID " +
            "WHERE p.PKH_NGAYLAP >= :bd AND p.PKH_NGAYLAP <= :kt " +
            "AND s.BT_ID = :id ", nativeQuery = true)
 public List<SanPhamKiem> getSanPhamKiemOfBienTheAll(
     @Param("bd") LocalDateTime bd,
     @Param("kt") LocalDateTime kt,
     @Param("id") int id);
	
	
}
