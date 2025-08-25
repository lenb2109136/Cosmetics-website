package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.repository.KhachHangRepository;

@Service
public class KhachHangService {
	@Autowired
	private KhachHangRepository khachHangRepository;
	
	@Transactional
	public void save(KhachHang k) {
		 khachHangRepository.save(k);
	}
	
	public KhachHang getKhachHang(long id) {
		return khachHangRepository.getById(id);
	}
}
