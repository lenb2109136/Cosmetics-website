package com.example.e_commerce.DTO.response;

import java.util.ArrayList;
import java.util.List;

public class ParentCartLast {
	int idBienThe;
	List<CartItemLast> cartItemLasts= new  ArrayList<CartItemLast>();
	String tenBienThe;
	String anhGioiThieu;
	float giaGoc;
	float tongTien;
	int tongSoLuong;
	float tongGiaTriGiam;
	
	
	
	
	public float getTongTien() {
		return tongTien;
	}
	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}
	public int getTongSoLuong() {
		return tongSoLuong;
	}
	public void setTongSoLuong(int tongSoLuong) {
		this.tongSoLuong = tongSoLuong;
	}
	public float getTongGiaTriGiam() {
		return tongGiaTriGiam;
	}
	public void setTongGiaTriGiam(float tongGiaTriGiam) {
		this.tongGiaTriGiam = tongGiaTriGiam;
	}
	public String getAnhGioiThieu() {
		return anhGioiThieu;
	}
	public void setAnhGioiThieu(String anhGioiThieu) {
		this.anhGioiThieu = anhGioiThieu;
	}
	public int getIdBienThe() {
		return idBienThe;
	}
	public void setIdBienThe(int idBienThe) {
		this.idBienThe = idBienThe;
	}
	public List<CartItemLast> getCartItemLasts() {
		return cartItemLasts;
	}
	public void setCartItemLasts(List<CartItemLast> cartItemLasts) {
		this.cartItemLasts = cartItemLasts;
	}
	public String getTenBienThe() {
		return tenBienThe;
	}
	public void setTenBienThe(String tenBienThe) {
		this.tenBienThe = tenBienThe;
	}
	public float getGiaGoc() {
		return giaGoc;
	}
	public void setGiaGoc(float giaGoc) {
		this.giaGoc = giaGoc;
	}
	
	public void addItemCart(CartItemLast item) {
		this.cartItemLasts.add(item);
	}
	
	
	
}
