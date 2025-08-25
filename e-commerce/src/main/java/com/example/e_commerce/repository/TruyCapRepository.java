package com.example.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.TruyCap;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface TruyCapRepository extends JpaRepository<TruyCap, Long>{
	
	@Query(value = "SELECT p FROM TruyCap p WHERE p.ngayGioiTruyCap >= :bd AND p.ngayGioiTruyCap<=:kt AND p.sanPham=:sp")
	public List<TruyCap> getTruyCapBySanPhamInTime(LocalDateTime bd, LocalDateTime kt, SanPham sp);
	
	@Query(value = "SELECT * FROM truycap WHERE TC_ID!=:idtc AND SP_ID=:idsp AND KH_ID=:idkh AND ngaygiotruycap<= :ng",nativeQuery = true)
	public List<TruyCap> getTruyCap(long idtc, long idsp,long idkh,LocalDateTime ng);
	@Query(value = "SELECT * " +
            "FROM truycap " +
            "WHERE  SP_ID = :idsp " +
            "  AND KH_ID = :idkh " +
            
            "ORDER BY ngaygiotruycap DESC " +
            "LIMIT 1", 
    nativeQuery = true)
	
	
	public TruyCap getTruyCapGanNhat(
                       	@Param("idsp") long idsp,
                       @Param("idkh") long idkh
                    );
	
	@Query(value = "SELECT * FROM truycap t WHERE t.SP_ID=:id AND t.NGAYGIOTRUYCAP >=:nbd AND t.NGAYGIOTRUYCAP<=:nkt", 
    nativeQuery = true)
	public List<TruyCap> getTruyCap(
                        long id,
                       LocalDateTime nbd,
                       LocalDateTime nkt
                    );

	
}
