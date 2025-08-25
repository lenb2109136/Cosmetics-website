package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.SanPhamKiem;
import com.example.e_commerce.repository.SanPhamKiemRepository;

@Service
public class SanPhamKiemService {
	
	@Autowired
	private SanPhamKiemRepository sanPhamKiemRepository;
	@Transactional
	public void save(SanPhamKiem sanPhamKiem) {
		sanPhamKiemRepository.save(sanPhamKiem);
	}
	
	public List<SanPhamKiem> getSanPhamKiemOfBienThe(
			LocalDateTime bd,
			LocalDateTime kt,
			int id){
		return sanPhamKiemRepository.getSanPhamKiemOfBienThe(bd,kt,id);
	}
	public List<SanPhamKiem> getSanPhamKiemOfBienTheCondition(
			LocalDateTime bd,
			LocalDateTime kt,
			int id,
			int active){
		if(active==-1) {
			return sanPhamKiemRepository.getSanPhamKiemOfBienTheChuaXacNhan(bd,kt,id);
		}
		else if(active==1){
			return sanPhamKiemRepository.getSanPhamKiemOfBienThe(bd,kt,id);
		}
		else {
			return sanPhamKiemRepository.getSanPhamKiemOfBienTheAll(bd, kt, id);
		}
	}
	
}
