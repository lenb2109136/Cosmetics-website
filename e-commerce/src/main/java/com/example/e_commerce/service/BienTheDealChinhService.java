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
public class BienTheDealChinhService {
	
	@Autowired
	private BienTheDealChinhReposiory bienTheDealChinhReposiory;
	
	@Transactional
	public void save(BienTheDealChinh bienTheDealChinh) {
		if(bienTheDealChinh.getSoLuongTu()<=0) {
			throw new GeneralException("Số lượng để áp dụng deal (phân loại chính ) phải lớn hơn 0",HttpStatus.BAD_REQUEST);
		}
		bienTheDealChinhReposiory.save(bienTheDealChinh);
	}
	@Transactional
	public void saveForUpdate(BienTheDealChinh bienTheDealChinh) {
		bienTheDealChinhReposiory.save(bienTheDealChinh);
	}
	
	public List<BienTheDealChinh> getBienTheDealChinhOfSanPham(List<Integer> idBienThe){
		return bienTheDealChinhReposiory.getDealChinhOfSanPham(idBienThe);
	}
}
