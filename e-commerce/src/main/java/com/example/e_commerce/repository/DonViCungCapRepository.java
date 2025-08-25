package com.example.e_commerce.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.e_commerce.model.DonViCungCap;
import com.example.e_commerce.model.ThongSo;

public interface DonViCungCapRepository extends JpaRepository<DonViCungCap, Long>{
	@Query(value = "SELECT \r\n"
			+ "COUNT(b.SP_ID)\r\n"
			+ "FROM donvicuncap d\r\n"
			+ "JOIN phieunhap p ON p.DVCC_ID=d.DVCC_ID\r\n"
			+ "JOIN chitietphieunhap c ON c.PN_ID=p.PN_ID\r\n"
			+ "JOIN bienthe b ON b.BT_ID=c.BT_ID\r\n"
			+ "WHERE p.PN_NGAYNHAPHANG >:nbd and p.PN_NGAYNHAPHANG<:nkt AND d.DVCC_ID=:id",nativeQuery = true)
	public Integer getSumProduct(
			LocalDate nbd, LocalDate nkt, Long id
			);
	@Query(value = "SELECT COUNT(p.PN_ID) " +
            "FROM donvicuncap d JOIN phieunhap p ON p.DVCC_ID = d.DVCC_ID " +
            "WHERE p.PN_NGAYNHAPHANG > :nbd AND p.PN_NGAYNHAPHANG < :nkt AND d.DVCC_ID = :id",
    nativeQuery = true)
public Integer getSumPhieuNhap(LocalDate nbd, LocalDate nkt, Long id);
	public Page<DonViCungCap> findByTenContainingIgnoreCase(String keyword, Pageable pageable);
	
	
}
