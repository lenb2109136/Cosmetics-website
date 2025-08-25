package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.repository.BienTheDealChinhReposiory;
import com.example.e_commerce.repository.BienTheDealPhuRepository;

@Service
public class BienTheDealPhuService {
	@Autowired
	private BienTheDealPhuRepository bienTheDealPhuRepository;
	
	@Transactional
	public void save(BienTheDealPhu bienTheDealPhu) {
		if(bienTheDealPhu.getGiaTriGiam()<=0) {
			throw new GeneralException("Tồn tại trị giảm của deal nhỏ hơn không",HttpStatus.BAD_REQUEST);
		}
		if(bienTheDealPhu.getToiDaTrenDonVi()<=0) {
			throw new GeneralException("Tồn tại số lượng tối đa của deal giảm nhỏ hơn không",HttpStatus.BAD_REQUEST);
		}
		bienTheDealPhuRepository.save(bienTheDealPhu);
	}
	@Transactional
	public void saveForUpdate(BienTheDealPhu bienTheDealPhu) {
		
		bienTheDealPhuRepository.save(bienTheDealPhu);
	}
	
	public List<BienTheDealPhu> getBienTheDealChinhOfSanPham(List<Integer> idBienThe){
		return bienTheDealPhuRepository.getDealPhuOfSanPham(idBienThe);
	}
	
}
