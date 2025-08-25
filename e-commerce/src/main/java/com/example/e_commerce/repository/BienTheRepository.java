package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.ChiTietHoaDon;

@Repository
public interface BienTheRepository extends JpaRepository<BienThe, Integer>{
	@Query(value = "SELECT c.* FROM bienthe b " +
            "JOIN chitiethoadon c ON c.BT_ID = b.BT_ID " +
            "JOIN hoadon h ON h.HD_ID = c.HD_ID " +
            "WHERE b.BT_ID = :id AND h.TT_ID IN (:ds)", 
    nativeQuery = true)
	List<ChiTietHoaDon> getSoLuongTrongDon(@Param("id") int id, @Param("ds") List<Long> ds);
	
	
	@Query(value = "SELECT * FROM bienthe b WHERE b.BT_MAVACH=:id", 
    nativeQuery = true)
	public BienThe getByMaVach(@Param("id") String id);
	
	@Query(value = "SELECT s.PKH_SOLUONG FROM  sanphamkiem s JOIN phieukiemhang p ON p.PKH_ID=s.PKH_ID \r\n"
			+ "WHERE p.PKH_DAXACNHAN=0 AND s.BT_ID=:id", nativeQuery = true)
	public Integer soLuongDaKhauHao(int id);
}
