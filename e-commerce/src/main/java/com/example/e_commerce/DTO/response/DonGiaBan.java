package com.example.e_commerce.DTO.response;

import java.util.List;

import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList.Member2.Item;

public class DonGiaBan {
	List<ItemDonGia> donGia;
	private float tongDonGia;
	public List<ItemDonGia> getDonGia() {
		return donGia;
	}
	public void setDonGia(List<ItemDonGia> donGia) {
		this.donGia = donGia;
	}
	public float getTongDonGia() {
		return tongDonGia;
	}
	public void setTongDonGia(float tongDonGia) {
		this.tongDonGia = tongDonGia;
	}
	public void themItem(int soLuong, float gia) {
		ItemDonGia item = new ItemDonGia();
		item.setSoLuong(soLuong);
		item.setGia(gia);
		donGia.add(item);

		tongDonGia += soLuong * gia; 
	}

	public void tinhTongDonGia() {
		float tong = 0;
		for (ItemDonGia item : donGia) {
			tong += item.getSoLuong() * item.getGia();
		}
		this.tongDonGia = tong;
	}
	
}

class ItemDonGia{
	int soLuong;
	float gia;
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
	
	
}
