package com.example.e_commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trangthai")
public class TrangThai {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TT_ID")
	private long id;
	@Column(name = "TT_TEN")
	private String ten;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	
	
}
