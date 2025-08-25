package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.DongGoiChan;
import com.example.e_commerce.repository.DongGoiChanRepository;

@Service
public class QuyCachNhapHangService {
	@Autowired
	private DongGoiChanRepository dongGoiChanRepository;
	
	@Autowired
	private CloudinaryService cloudinaryService;
	
	public List<DongGoiChan> getAllDongGoi(){
		return dongGoiChanRepository.findAll();
	}
	
	@Transactional
	public void save(String ten, MultipartFile anhMoTa) {
		getAllDongGoi().stream().forEach(d->{
			if(d.getTenQuyCach().equals(ten.trim())) {
				throw new GeneralException("Vui lòng cung cấp tên khác với các quy cách còn lại: ",HttpStatus.BAD_REQUEST);
			}
		});
		DongGoiChan dongGoiChan= new DongGoiChan();
		dongGoiChan.setTenQuyCach(ten);
		if (anhMoTa != null && !anhMoTa.isEmpty()) {
            validateImageFile(anhMoTa); 
            String anhBiaUrl = cloudinaryService.uploadImage(anhMoTa);
            dongGoiChan.setDuongDan(anhBiaUrl);
        }
		dongGoiChan.setTenQuyCach(ten);
		dongGoiChanRepository.save(dongGoiChan);
	}
	@Transactional
	public void update(int id,String ten, MultipartFile anhMoTa) {
		getAllDongGoi().stream().forEach(d->{
			if(d.getTenQuyCach().equals(ten.trim())&&d.getId()!=id) {
				throw new GeneralException("Vui lòng cung cấp tên khác với các quy cách còn lại: ",HttpStatus.BAD_REQUEST);
			}
		});
		
		DongGoiChan dongGoiChan= dongGoiChanRepository.findById(id).orElseThrow(()->{
			throw new GeneralException("Không tìm thấy quy cách cần chỉnh sửa: ",HttpStatus.BAD_REQUEST);
		});
		dongGoiChan.setTenQuyCach(ten);
		if (anhMoTa != null && !anhMoTa.isEmpty()) {
            validateImageFile(anhMoTa); 
            String anhBiaUrl = cloudinaryService.uploadImage(anhMoTa);
            dongGoiChan.setDuongDan(anhBiaUrl);
        }
		dongGoiChan.setTenQuyCach(ten);
		dongGoiChanRepository.save(dongGoiChan);
	}
	
	private void validateImageFile(MultipartFile file) {
	    String contentType = file.getContentType();
	    if (contentType == null || !contentType.startsWith("image/")) {
	        throw new IllegalArgumentException("File không phải là ảnh: " + file.getOriginalFilename());
	    }
	}
}
