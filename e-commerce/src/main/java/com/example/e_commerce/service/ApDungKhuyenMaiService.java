package com.example.e_commerce.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.ApDungKhuyenMai;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.KhuyenMai;
import com.example.e_commerce.model.embeded.EApDungKhuyenMai;
import com.example.e_commerce.repository.AoDungKhuyenMaiReposiToRy;

@Service
public class ApDungKhuyenMaiService {
	@Autowired
	private AoDungKhuyenMaiReposiToRy apDungKhuyenMaiRepository;
	
	@Transactional
	public void save(HoaDon hoaDon, BienThe bienThe, KhuyenMai khuyenMai, int soLuong, float tyLe, LocalDateTime ngayApDung) {
		ApDungKhuyenMai apDungKhuyenMai= new ApDungKhuyenMai();
		apDungKhuyenMai.setBienThe(bienThe);
		apDungKhuyenMai.setHoaDon(hoaDon);
		apDungKhuyenMai.setKhuyenMai(khuyenMai);
		apDungKhuyenMai.setSoLuongApDung(soLuong);
		apDungKhuyenMai.setThoiDiemApDung(ngayApDung);
		apDungKhuyenMai.setTyLeApDung(tyLe);
		apDungKhuyenMaiRepository.save(apDungKhuyenMai);
	}
	public void saveAlready(ApDungKhuyenMai apDungKhuyenMai) {
		apDungKhuyenMaiRepository.save(apDungKhuyenMai);
	}
	
	@Transactional
	public void delete(EApDungKhuyenMai id) {
		apDungKhuyenMaiRepository.deleteById(id);
	}
}
