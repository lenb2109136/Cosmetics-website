package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.embeded.EBienTheFlashSale;
import com.example.e_commerce.repository.BienTheFlashSaleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BienTheFlashSaleService {
	@Autowired
	private BienTheFlashSaleRepository bienTheFlashSaleRepository;
	
	
	@Transactional
	public void save(BienTheFlashSale bienTheFlashSale) {
		if(bienTheFlashSale.getGiaTriGiam()<=0) {
			throw new GeneralException("Tỉ lệ giảm flashsale phải lớn hơn không",HttpStatus.BAD_REQUEST);
		}
		if(bienTheFlashSale.getSoLuongGioiHan()<=0) {
			throw new GeneralException("Số lượng sản phẩm áp dụng phải lớn",
					HttpStatus.BAD_REQUEST);
		}
		bienTheFlashSaleRepository.save(bienTheFlashSale);
	}
	@Transactional
	public void saveNotValidate(BienTheFlashSale bienTheFlashSale) {
		
		bienTheFlashSaleRepository.save(bienTheFlashSale);
	}
	public List<BienTheFlashSale> getBienTheFlashSaleOfBienTHe(int id) {
		return bienTheFlashSaleRepository.getBienTheFlashSaleTheoBienThe(id);
	}
	
	public BienTheFlashSale getBienTheFlahSale(long idflash, int idbienthe) {
	    EBienTheFlashSale id = new EBienTheFlashSale();
	    id.setBtId(idbienthe);
	    id.setKmId(idflash);
	    return bienTheFlashSaleRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phân loại cần cập nhật"));
	}
	
	public List<BienTheFlashSale> getBienTheFlahSaleDangApDungOfSanPham(List<Integer> idSanPham) {
	    return bienTheFlashSaleRepository.getFlashSaleOfSanPhamDangApDung(idSanPham);
	}
	

}