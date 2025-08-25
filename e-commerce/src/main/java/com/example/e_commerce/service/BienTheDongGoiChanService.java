package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.repository.BienTheDongGoiRepository;

@Service
public class BienTheDongGoiChanService {
	@Autowired
	private BienTheDongGoiRepository bienTheDongGoiRepository;
	
	public BienTheDongGoiChan getBienTheDongGoiByMaVach(String maVach) {
		return bienTheDongGoiRepository.getBienTheDongGoiChanByMaVach(maVach);
	}
	@Transactional
	public void save(BienTheDongGoiChan b) {
		bienTheDongGoiRepository.save(b);
	}
	
	@Transactional
	public void delete(BienTheDongGoiChan v) {
		bienTheDongGoiRepository.delete(v);
	}
}
