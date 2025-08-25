package com.example.e_commerce.DTO.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class PhieuNhapDTO {
	public long idPhieu;
	public LocalDateTime ngayNhap;
	public float thueVAT;
	public float tongTien;
	public String tenNhanVien;
	public long idDonViCungCap;
	public String diaChiNhanVien;
	public String soDienThoaiNhanVien;
	public String tenCongTy;
	public String soDienThoaiCongTy;
	public String maSoThueCongTy;
	public String diaChiCongTy;
	// thông tin đơn hàng
	
	
	
	Collection<SanPhamPhieuNhap> sanPham;
	public long getIdDonViCungCap() {
		return idDonViCungCap;
	}
	public void setIdDonViCungCap(long idDonViCungCap) {
		this.idDonViCungCap = idDonViCungCap;
	}
	public long getIdPhieu() {
		return idPhieu;
	}
	public void setIdPhieu(long idPhieu) {
		this.idPhieu = idPhieu;
	}
	public LocalDateTime getNgayNhap() {
		return ngayNhap;
	}
	public void setNgayNhap(LocalDateTime ngayNhap) {
		this.ngayNhap = ngayNhap;
	}
	public float getThueVAT() {
		return thueVAT;
	}
	public void setThueVAT(float thueVAT) {
		this.thueVAT = thueVAT;
	}
	public float getTongTien() {
		return tongTien;
	}
	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}
	public String getTenNhanVien() {
		return tenNhanVien;
	}
	public void setTenNhanVien(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;
	}
	public String getDiaChiNhanVien() {
		return diaChiNhanVien;
	}
	public void setDiaChiNhanVien(String diaChiNhanVien) {
		this.diaChiNhanVien = diaChiNhanVien;
	}
	public String getSoDienThoaiNhanVien() {
		return soDienThoaiNhanVien;
	}
	public void setSoDienThoaiNhanVien(String soDienThoaiNhanVien) {
		this.soDienThoaiNhanVien = soDienThoaiNhanVien;
	}
	public String getTenCongTy() {
		return tenCongTy;
	}
	public void setTenCongTy(String tenCongTy) {
		this.tenCongTy = tenCongTy;
	}
	public String getSoDienThoaiCongTy() {
		return soDienThoaiCongTy;
	}
	public void setSoDienThoaiCongTy(String soDienThoaiCongTy) {
		this.soDienThoaiCongTy = soDienThoaiCongTy;
	}
	public String getMaSoThueCongTy() {
		return maSoThueCongTy;
	}
	public void setMaSoThueCongTy(String maSoThueCongTy) {
		this.maSoThueCongTy = maSoThueCongTy;
	}
	public String getDiaChiCongTy() {
		return diaChiCongTy;
	}
	public void setDiaChiCongTy(String diaChiCongTy) {
		this.diaChiCongTy = diaChiCongTy;
	}
	public Collection<SanPhamPhieuNhap> getSanPham() {
		return sanPham;
	}
	public void setSanPham(Collection<SanPhamPhieuNhap> sanPham) {
		this.sanPham = sanPham;
	}
	
	
}

	
