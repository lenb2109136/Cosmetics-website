package com.example.e_commerce.DTO.request.FlashSaledto;

import java.util.ArrayList;
import java.util.List;

public class SanPhamFlashSale{
	Long id;
	String ten;
	String hinhAnh;
	List<ItemFlash> bienThe= new ArrayList<ItemFlash>();
	boolean ready=true;
	 
	 
	
	
	
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public List<ItemFlash> getBienThe() {
		return bienThe;
	}
	public void setBienThe(List<ItemFlash> bienThe) {
		this.bienThe = bienThe;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public String getHinhAnh() {
		return hinhAnh;
	}
	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}
	
	
}
