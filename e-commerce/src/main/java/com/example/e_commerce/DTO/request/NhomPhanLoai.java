package com.example.e_commerce.DTO.request;

import java.util.List;

public class NhomPhanLoai {
	private int id;
	private String ten;
	private List<DPhanLoai> classifyItems;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public List<DPhanLoai> getClassifyItems() {
		return classifyItems;
	}
	public void setClassifyItems(List<DPhanLoai> classifyItems) {
		this.classifyItems = classifyItems;
	}
	
}
