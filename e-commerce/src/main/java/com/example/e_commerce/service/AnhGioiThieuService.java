package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.AnhGioiThieu;
import com.example.e_commerce.repository.AnhGioiThieuRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AnhGioiThieuService {
	@Autowired
	AnhGioiThieuRepository anhGioiThieuRepository;
	@Transactional
	public AnhGioiThieu save(AnhGioiThieu anhGioiThieu) {
		return anhGioiThieuRepository.save(anhGioiThieu);
	}
	
	@Transactional
	public void delete(long id) {
		 anhGioiThieuRepository.deleteByIdCustom(id);
	}
}
