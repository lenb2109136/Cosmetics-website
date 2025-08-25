package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.repository.TrangThaiRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TrangThaiService {
	@Autowired
	private TrangThaiRepository trangThaiRepository;
	
	public TrangThai getById(long id) {
		return trangThaiRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy trạng thái "));
	}
	
	public List<TrangThai> getAllTrangThai(){
		return trangThaiRepository.findAll();
	}
	
	
	public long anhXaGhnStatus(String status) {
		if(status.equals("ready_to_pick") || status.equals("picking")) {
			return 13;
		}
		if(status.equals("delivered")) {
			return 1;
		}
		if(status.equals("picked") ) {
			return 12;
		}
		
		if(status.equals("return") ) {
			return 16;
		}
		if(status.equals("returned") ) {
			return 17;
		}
		return -1;
		
		
		
	}
}
