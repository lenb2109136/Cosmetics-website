package com.example.e_commerce.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.example.e_commerce.DTO.response.PhieuNhapDTO;
import com.example.e_commerce.DTO.response.SanPhamPhieuNhap;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.ChiTietPhieuNhap;
import com.example.e_commerce.model.DonViCungCap;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.PhieuNhap;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.service.BienTheSerVice;
import com.example.e_commerce.service.DonViCungCapService;
import com.example.e_commerce.service.JwtService;
import com.example.e_commerce.service.NguoiDungService;
import com.example.e_commerce.service.ServiceLocator;

public class PhieuNhapMapper {
	public static PhieuNhapDTO MappToPhieuNhapDTO(PhieuNhap phieuNhap) {
		PhieuNhapDTO phieuNhapDTO= new PhieuNhapDTO();
		phieuNhapDTO.setDiaChiCongTy(phieuNhap.getDonViCungCap().getDiaChi());
		phieuNhapDTO.setDiaChiNhanVien(phieuNhap.getNguoiDung().getDiachi());
		phieuNhapDTO.setIdPhieu(phieuNhap.getId());
		phieuNhapDTO.setMaSoThueCongTy(phieuNhap.getDonViCungCap().getMaSoThue());
		phieuNhapDTO.setNgayNhap(phieuNhap.getNgayNhapHang());
		phieuNhapDTO.setSoDienThoaiCongTy(phieuNhap.getDonViCungCap().getSoDienThoai());
		phieuNhapDTO.setSoDienThoaiNhanVien(phieuNhap.getNguoiDung().getSodienthoai());
		phieuNhapDTO.setTenCongTy(phieuNhap.getDonViCungCap().getTen());
		phieuNhapDTO.setTenNhanVien(phieuNhap.getNguoiDung().getTen());
		phieuNhapDTO.setThueVAT(phieuNhap.getThueVAT());
		phieuNhapDTO.setTongTien(phieuNhap.getTongTien());
		Map<Long, SanPhamPhieuNhap> detailPhieuNhap= new HashMap<Long, SanPhamPhieuNhap>();
		ChiTietPhieuNhap p;
		BienThe bienThe;
		SanPham sanPham;
		for(int i=0;i<phieuNhap.getChiTietPhieuNhap().size();i++) {
			System.out.println("duyêth lần: "+i);
			 p =phieuNhap.getChiTietPhieuNhap().get(i);
			 bienThe = p.getBienThe();
			 sanPham=bienThe.getSanPham();
			 SanPhamPhieuNhap sppn= detailPhieuNhap.get(sanPham.getId());
			 if(sppn!=null) {
				 sppn.getBienTheNhap().add(phieuNhap.getChiTietPhieuNhap().get(i));
			 }
			 else {
				 sppn = new SanPhamPhieuNhap();
				 sppn.addBienTheNhap(phieuNhap.getChiTietPhieuNhap().get(i));
				 sppn.setIdSanPham(sanPham.getId());
				 sppn.setTenSanPham(sanPham.getTen());
				 detailPhieuNhap.put(sanPham.getId(), sppn);
			 }
		}
		phieuNhapDTO.setSanPham(detailPhieuNhap.values());
		return phieuNhapDTO;
		
		
	}
	
	public static void mapperPhieuNhapDTOToPhieuNhap(PhieuNhapDTO phieuNhapDTO) {
		
		if(phieuNhapDTO.getSanPham().size()==0) {
			throw new GeneralException("Phiếu nhập phải cung cấp nhiều hơn một sản phẩm",HttpStatus.BAD_REQUEST);
		}
		long t=phieuNhapDTO.getSanPham().stream().map(d->d.getIdSanPham()).distinct().count();
		if(t<phieuNhapDTO.getSanPham().size()) {
			throw new GeneralException("Sản phẩm cung cấp bị trùng lặp",HttpStatus.BAD_REQUEST);
		}
		ArrayList<SanPhamPhieuNhap> s= new ArrayList<SanPhamPhieuNhap>(phieuNhapDTO.getSanPham());
		for(int i=0;i<s.size();i++) {
			if(s.get(0).getDonGia()<=0 || s.get(i).getSoLuong()<=0) {
				throw new GeneralException("Vui lòng kiểm tra giá và số lương của sản phẩm thứ: "+i,HttpStatus.BAD_REQUEST);
			}
		}
	}
}
