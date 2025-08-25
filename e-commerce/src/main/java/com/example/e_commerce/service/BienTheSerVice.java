package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.repository.BienTheRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BienTheSerVice {
	
	@Autowired
	BienTheRepository bienTheRepository;
	
	@Transactional
	public BienThe save(BienThe bienThe) {
		return bienTheRepository.save(bienThe);
	}
	public BienThe getById(int id) {
		System.out.println("ID TRUYỀN VÀO LÀ: "+id);
		return bienTheRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy biến thể"));
	}
	
	public List<ChiTietHoaDon> get(int id, List<Long> ds){
		return bienTheRepository.getSoLuongTrongDon(id, ds);
	}
	public BienThe getByMavach(String id){
		return bienTheRepository.getByMaVach(id);
	}
	
	
	public int soLuongHuHao(int id) {
		Integer sl= bienTheRepository.soLuongDaKhauHao(id);
		if(sl==null) {
			return 0;
		}
		else {
			return sl;
		}
	}
	
	
	
}
