package com.example.e_commerce.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.ThueVATSanPham;
import com.example.e_commerce.model.embeded.EThueVATSanPham;

@Repository
public interface ThueVATSanPhamRepository extends JpaRepository<ThueVATSanPham,EThueVATSanPham >{
	@Query(value = "SELECT *\r\n"
			+ "FROM thuevatsanpham\r\n"
			+ "WHERE SP_ID = :sp_id\r\n"
			+ "  AND TSP_THOIDIEM = (\r\n"
			+ "    SELECT MAX(TSP_THOIDIEM)\r\n"
			+ "    FROM thuevatsanpham\r\n"
			+ "    WHERE SP_ID = :sp_id\r\n"
			+ "      AND TSP_THOIDIEM <= :dattiem\r\n"
			+ "  );\r\n"
			+ "",nativeQuery = true)
	public ThueVATSanPham getThueVATSanPhamAtTime(@Param("dattiem") LocalDateTime dattiem, @Param("sp_id") long sp_id);

}
