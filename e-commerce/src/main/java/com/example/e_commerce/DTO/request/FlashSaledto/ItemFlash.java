package com.example.e_commerce.DTO.request.FlashSaledto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import jakarta.validation.constraints.Min;

public class ItemFlash{
	List<Map<String, Object>> danhSachFlasSale=new ArrayList<Map<String,Object>>();
	List<Map<String, Object>> dealChinh=new ArrayList<Map<String,Object>>();
	List<Map<String, Object>> dealPhu=new ArrayList<Map<String,Object>>();
	float gia;
	float giaGiam;
	String hinhAnh;
	@Min(value = 0,message = "Biến thể cung cấp không hợp lệ")
	int id;
	String ten;
	long soLuongKho;
	int soLuongConLai;
	int soLuongKhuyenMai;
	boolean conSuDung=false;
	boolean isReady=false;
	boolean notUpdate=false;
	
	
	
	
	public boolean isNotUpdate() {
		return notUpdate;
	}
	public void setNotUpdate(boolean notUpdate) {
		this.notUpdate = notUpdate;
	}
	public List<Map<String, Object>> getDealChinh() {
		return dealChinh;
	}
	public void setDealChinh(List<Map<String, Object>> dealChinh) {
		this.dealChinh = dealChinh;
	}
	public List<Map<String, Object>> getDealPhu() {
		return dealPhu;
	}
	public void setDealPhu(List<Map<String, Object>> dealPhu) {
		this.dealPhu = dealPhu;
	}
	public boolean isConSuDung() {
		return conSuDung;
	}
	
	
	
	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
	public int getSoLuongConLai() {
		return soLuongConLai;
	}
	public void setSoLuongConLai(int soLuongConLai) {
		this.soLuongConLai = soLuongConLai;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Map<String, Object>> getDanhSachFlasSale() {
		return danhSachFlasSale;
	}
	public void setDanhSachFlasSale(List<Map<String, Object>> danhSachFlasSale) {
		this.danhSachFlasSale = danhSachFlasSale;
	}
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
	public float getGiaGiam() {
		return giaGiam;
	}
	public void setGiaGiam(float giaGiam) {
		this.giaGiam = giaGiam;
	}
	public String getHinhAnh() {
		return hinhAnh;
	}
	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}
	
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public long getSoLuongKho() {
		return soLuongKho;
	}
	public void setSoLuongKho(long soLuongKho) {
		this.soLuongKho = soLuongKho;
	}
	public int getSoLuongKhuyenMai() {
		return soLuongKhuyenMai;
	}
	public void setSoLuongKhuyenMai(int soLuongKhuyenMai) {
		this.soLuongKhuyenMai = soLuongKhuyenMai;
	}
	
	
}

