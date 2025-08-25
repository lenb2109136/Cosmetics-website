package com.example.e_commerce.service;

import java.beans.Transient;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.model.TrangThaiHoaDon;
import com.example.e_commerce.repository.TrangThaiHoaDonRepository;

@Service
public class TrangThaiHoaDonService {
	@Autowired
	private TrangThaiHoaDonRepository trangThaiHoaDonRepository;
	@Transactional
	public void Save(HoaDon hoaDon, TrangThai trangThai,String ghiChu) {
		TrangThaiHoaDon trangThaiHoaDon= new TrangThaiHoaDon();
		trangThaiHoaDon.setHoaDon(hoaDon);
		trangThaiHoaDon.setGhiChu(ghiChu);
		trangThaiHoaDon.setThoiDiem(LocalDateTime.now());
		trangThaiHoaDon.setTrangThai(trangThai);
		trangThaiHoaDonRepository.save(trangThaiHoaDon);
	}
}
