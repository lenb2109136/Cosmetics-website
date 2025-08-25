package com.example.e_commerce.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.TruyCap;
import com.example.e_commerce.repository.TruyCapRepository;

@Service
public class TruyCapService {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private NguoiDungService nguoiDungService;
	
	@Autowired
	private TruyCapRepository truyCapRepository;
	public List<TruyCap> getTruyCapBySanPhamInTime(LocalDateTime bd, LocalDateTime kt, SanPham sp){
		return truyCapRepository.getTruyCap(sp.getId(),bd,kt.plusDays(1));
	}
	public List<TruyCap> getTruyCaKhacCuaTruyCap(long idtc, long idsp, long idkh,LocalDateTime ngayTruyCap){
		return truyCapRepository.getTruyCap(idtc, idsp, idkh,ngayTruyCap);
	}
	
	@Transactional
	public void add(long id) {
		TruyCap l= truyCapRepository.getTruyCapGanNhat(id,jwtService.getIdUser());
		if(l==null) {
			
		}
		else {
			System.out.println("đi vào đây 2");
			LocalDateTime now = LocalDateTime.now();
		    LocalDateTime truyCap = l.getNgayGioiTruyCap();

		    long diffInSeconds = Duration.between(truyCap, now).getSeconds();

		    if (diffInSeconds < 150) {
		    	System.out.println("ĐI VÀO KHÔNG LƯU ");
		    	return ;
		    }
		}
		System.out.println("ĐI VÀO LƯU");
		SanPhamService sanPhamService = ServiceLocator.getBean(SanPhamService.class);
		SanPham s= sanPhamService.getSanPhamById(id);
		NguoiDung n= nguoiDungService.getById(jwtService.getIdUser());
		TruyCap t= new TruyCap();
		t.setKhachHang((KhachHang)n);
		t.setSanPham(s);
		t.setNgayGioiTruyCap(LocalDateTime.now());
		truyCapRepository.save(t);
	}
}
