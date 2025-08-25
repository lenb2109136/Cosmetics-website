package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.repository.NguoiDungRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NguoiDungService {
@Autowired
private NguoiDungRepository nguoiDungRepository;

@Autowired
private JwtService jwtService;

public NguoiDung getById(long id) {
	return nguoiDungRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy thông tin người dùng"));
}

public KhachHang getKhachHangBySDT(String sdt) {
	return nguoiDungRepository.getKhachHangBySDT(sdt);
}

public void update(NguoiDung nguoiDung) {
	NguoiDung nguoiDung2= getById(jwtService.getIdUser());
	nguoiDung2.setTen(nguoiDung.getTen());
	nguoiDung2.setSodienthoai(nguoiDung.getSodienthoai());
	nguoiDung2.setMatkhau(nguoiDung.getMatkhau());
	nguoiDung2.setEmail(nguoiDung.getEmail());
	nguoiDung2.setDiachi(nguoiDung.getDiachi());
	nguoiDungRepository.save(nguoiDung2);
}

}
