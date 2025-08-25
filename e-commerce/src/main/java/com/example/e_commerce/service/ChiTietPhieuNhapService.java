package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.example.e_commerce.DTO.request.BienThe;
import com.example.e_commerce.model.ChiTietPhieuNhap;
import com.example.e_commerce.repository.ChiTietPhieuNhapRepository;

@Service
public class ChiTietPhieuNhapService {
	@Autowired
	ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

	public Page<Object[]> getBienTheInChiTietPhieuNhap(LocalDateTime nbd, LocalDateTime nkt, long id, Pageable page) {
		return chiTietPhieuNhapRepository.getBienTheDonViCungCap(nbd, nkt, id, page);
	}

	public Integer GetTongSoLuongNhap(int id, LocalDateTime bd, LocalDateTime kt) {
		return chiTietPhieuNhapRepository.TongSoLuongNhapTaiThoiDiem(id, kt, bd);
	}
	
	public Optional<ChiTietPhieuNhap> findLatestPhieuNhapByBienTheIdAndNgayNhap(int bienTheId, LocalDateTime ngayNhap) {
        return chiTietPhieuNhapRepository.findLatestByBienTheIdAndNgayNhap(bienTheId, ngayNhap);
    }
	
	
	public List<ChiTietPhieuNhap> getAllChiTietPhieuNhapBienThe(int idBienThe, LocalDateTime nn){
		return	chiTietPhieuNhapRepository.getAllChiTietPhieuNhapBienThe(idBienThe,nn);
	}
	
	public void Save(ChiTietPhieuNhap c) {
		chiTietPhieuNhapRepository.save(c);
	}
	public boolean kiemTraTonTai(int soLuong, int id, int iddg) {
	    return chiTietPhieuNhapRepository.demBanGhiTrung(soLuong, id, iddg) > 0;
	}

}
