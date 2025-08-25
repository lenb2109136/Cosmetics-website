package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.PhanHoi;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.embeded.EPhanHoi;
import com.example.e_commerce.repository.PhanHoiRepository;

@Service
public class PhanHoiService {
	@Autowired
	private PhanHoiRepository phanHoiRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;

	public List<Object[]> getGeneralPhanHoi(long idSanPham) {
		return phanHoiRepository.getGeneralPhanHoiSanPham(idSanPham);
	}
	
	public void themPhanHoi(
			long idsp,
			String noiDung,
			 int soSao
			) {
		SanPhamService sanPhamService=ServiceLocator.getBean(SanPhamService.class);
	    NguoiDungService nguoiDungService =ServiceLocator.getBean(NguoiDungService.class);
		SanPham sanPham= sanPhamService.getSanPhamById(idsp);
		NguoiDung nguoiDung=nguoiDungService.getById(jwtService.getIdUser());
		PhanHoi phanHoi= new PhanHoi();
		phanHoi.setId(new EPhanHoi());
		phanHoi.setKhachHang((KhachHang)nguoiDung);
		phanHoi.setNoiDungPhanHoi(noiDung);
		phanHoi.setSanPham(sanPham);
		phanHoi.setSoSao(soSao);
		phanHoi.setThoiGianPhanHoi(LocalDateTime.now());
		phanHoiRepository.save(phanHoi);
	}
}
