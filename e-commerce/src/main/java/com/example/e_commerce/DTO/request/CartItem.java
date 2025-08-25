package com.example.e_commerce.DTO.request;

public class CartItem {
	private int idBienThe;
	private int soLuong;
	private String DiaChi;
	
	public String getDiaChi() {
		return DiaChi;
	}
	public void setDiaChi(String diaChi) {
		DiaChi = diaChi;
	}
	public int getIdBienThe() {
		return idBienThe;
	}
	public void setIdBienThe(int idBienThe) {
		this.idBienThe = idBienThe;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
}
