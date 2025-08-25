package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.DonGiaBanHang;
import com.example.e_commerce.repository.DonGiaBanHangRepository;

@Service
public class DonGiaBanHangService {
	@Autowired
	private DonGiaBanHangRepository donGiaBanHangRepository;
	
	@Transactional
	public DonGiaBanHang save(DonGiaBanHang donGiaBanHang) {
		return donGiaBanHangRepository.save(donGiaBanHang);
	}
}
