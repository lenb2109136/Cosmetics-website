package com.example.e_commerce.DTO.response;

public class CartItemLast {
	int soLuong;
	float GiaGiam;
	long idDeal;
	float phanTramDealGiam;
	float phanTramFlashsale;
	String tenDeal;
	long idFlashsale;
	
	
	public float getPhanTramDealGiam() {
		return phanTramDealGiam;
	}
	public void setPhanTramDealGiam(float phanTramDealGiam) {
		this.phanTramDealGiam = phanTramDealGiam;
	}
	public float getPhanTramFlashsale() {
		return phanTramFlashsale;
	}
	public void setPhanTramFlashsale(float phanTramFlashsale) {
		this.phanTramFlashsale = phanTramFlashsale;
	}
	public String getTenDeal() {
		return tenDeal;
	}
	public void setTenDeal(String tenDeal) {
		this.tenDeal = tenDeal;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public float getGiaGiam() {
		return GiaGiam;
	}
	public void setGiaGiam(float giaGiam) {
		GiaGiam = giaGiam;
	}
	public long getIdDeal() {
		return idDeal;
	}
	public void setIdDeal(long idDeal) {
		this.idDeal = idDeal;
	}
	public long getIdFlashsale() {
		return idFlashsale;
	}
	public void setIdFlashsale(long idFlashsale) {
		this.idFlashsale = idFlashsale;
	}
	
	
}
