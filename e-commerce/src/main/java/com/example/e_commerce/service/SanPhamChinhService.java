package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.SanPhamChinh;
import com.example.e_commerce.model.SanPhamPhu;
import com.example.e_commerce.repository.SanPhamChinhRepository;
import com.example.e_commerce.repository.SanPhamPhuRepository;

@Service
public class SanPhamChinhService {
	@Autowired
	private SanPhamChinhRepository sanPhamPhuRepository;
	@Transactional
	public void save(SanPhamChinh sanPhamChinh) {
		sanPhamPhuRepository.save(sanPhamChinh);
	}
}
