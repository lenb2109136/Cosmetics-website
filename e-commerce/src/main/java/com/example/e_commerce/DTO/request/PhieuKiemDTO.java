package com.example.e_commerce.DTO.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

public class PhieuKiemDTO{
	List<Long> danhSachHoaDonBiHuy;
	int idSanPham;
	int soLuong;
	String ghiChu;
	long  soLuongThucTe;
	String sanPham;
	String tenBienThe;
	long sanPham_Cha;
	int soLuongDaDat;
	String maVach;
	long phieuKiemDaCo=0;
	
	
	
	
	public long getPhieuKiemDaCo() {
		return phieuKiemDaCo;
	}
	public void setPhieuKiemDaCo(long phieuKiemDaCo) {
		this.phieuKiemDaCo = phieuKiemDaCo;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public int getSoLuongDaDat() {
		return soLuongDaDat;
	}
	public void setSoLuongDaDat(int soLuongDaDat) {
		this.soLuongDaDat = soLuongDaDat;
	}
	public long getSanPham_Cha() {
		return sanPham_Cha;
	}
	public void setSanPham_Cha(long sanPham_Cha) {
		this.sanPham_Cha = sanPham_Cha;
	}
	public String getTenBienThe() {
		return tenBienThe;
	}
	public void setTenBienThe(String tenBienThe) {
		this.tenBienThe = tenBienThe;
	}
	public String getSanPham() {
		return sanPham;
	}
	public void setSanPham(String sanPham) {
		this.sanPham = sanPham;
	}
	public long getSoLuongThucTe() {
		return soLuongThucTe;
	}
	public void setSoLuongThucTe(long soLuongThucTe) {
		this.soLuongThucTe = soLuongThucTe;
	}
	List<Map<String,Object>> result= new ArrayList<Map<String,Object>>();
	
	
	public List<Map<String, Object>> getResult() {
		return result;
	}
	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}
	public int getIdSanPham() {
		return idSanPham;
	}
	public void setIdSanPham(int idSanPham) {
		this.idSanPham = idSanPham;
	}
	
	
	public List<Long> getDanhSachHoaDonBiHuy() {
		return danhSachHoaDonBiHuy;
	}
	public void setDanhSachHoaDonBiHuy(List<Long> danhSachHoaDonBiHuy) {
		this.danhSachHoaDonBiHuy = danhSachHoaDonBiHuy;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	
}