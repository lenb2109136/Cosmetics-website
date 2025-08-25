package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.e_commerce.model.DongHop;
import com.example.e_commerce.repository.DongHopRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DongHopService {
	@Autowired
	private DongHopRepository dongHopRepository;
	
	public DongHop getDongHopById(int id) {
		return dongHopRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy đóng hộp sản phẩm"));
	}
	
	public List<DongHop> getAllDongHopById() {
		return dongHopRepository.findAll();
	}
	
	
	public DongHop getToiUu() {
		return getDongHopById(1);
	}
	
	
}
