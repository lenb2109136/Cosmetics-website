package com.example.e_commerce.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.ThueVATSanPham;
import com.example.e_commerce.model.embeded.EThueVATSanPham;
import com.example.e_commerce.repository.ThueVATSanPhamRepository;

@Service
public class ThueVATSanPhamSerVice {
	@Autowired
	ThueVATSanPhamRepository thueVATSanPhamRepository;
	
	@Transactional
	public ThueVATSanPham save(SanPham sanPham,float tiLe) {
		
		ThueVATSanPham vat= new ThueVATSanPham();
		vat.setSanPham(sanPham);
		vat.setThoiDiem(LocalDateTime.now());
		thueVATSanPhamRepository.save(vat);
		return vat;
	}
	public ThueVATSanPham getThueVATAtTime(long idsp, LocalDateTime thoiDiem) {
		return thueVATSanPhamRepository.getThueVATSanPhamAtTime(thoiDiem, idsp);
	}
}
