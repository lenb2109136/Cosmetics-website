package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.KhuyenMai;
import com.example.e_commerce.model.embeded.EBienTheDealChinh;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface BienTheDealChinhReposiory extends JpaRepository<BienTheDealChinh,EBienTheDealChinh>{

	@Query(value = """
		    SELECT DISTINCT b.*
		    FROM bienthedealchinh b
		    JOIN khuyenmai d ON b.KM_ID = d.KM_ID
		    JOIN bienthe bb ON bb.BT_ID = b.BT_ID
		    JOIN deal d2 ON d2.KM_ID = d.KM_ID
		    WHERE b.BT_ID IN (:idBienThe)
		      AND NOW() >= d.KM_THOIGIANAPDUNG
		      AND NOW() <= d.KM_THOIGIANNGUNG
		      AND d.KM_CONSUDUNG = TRUE
		      AND b.BTDC_CONSUDUNG = TRUE
		      AND d2.D_SOLUONGGIOIHAN > d2.D_SOLUONGDADUNG
		      AND bb.BT_SOLUONG > 0
		      AND b.BTDC_SOLUONGTU <= bb.BT_SOLUONG
		    """, nativeQuery = true)
		public List<BienTheDealChinh> getDealChinhOfSanPham(@Param("idBienThe") List<Integer> idBienThe);

}
