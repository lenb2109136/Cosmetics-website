package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.QuyCachDongGoiLanNhap;
import com.example.e_commerce.repository.BienTheDongGoiLanNhapRepository;

@Service
public class QuyCachDongGoiLanNhapService {
	@Autowired
	private BienTheDongGoiLanNhapRepository bienTheDongGoiLanNhapRepository;

	@Transactional
	public void save(QuyCachDongGoiLanNhap quyCachDongGoiLanNhap) {
		bienTheDongGoiLanNhapRepository.save(quyCachDongGoiLanNhap);
	}
	}

