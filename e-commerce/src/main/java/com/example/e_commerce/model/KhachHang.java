package com.example.e_commerce.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "khachhang")
public class KhachHang extends NguoiDung {
	@Column(name = "KH_DIEMTICHLUY")
	private float diemTichLuy;

	

	public float getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(float diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}	
}
