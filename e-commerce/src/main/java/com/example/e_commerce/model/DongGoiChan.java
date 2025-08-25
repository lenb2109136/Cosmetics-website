package com.example.e_commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donggoichan")
public class DongGoiChan {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "TENQUYCACH")
	private String tenQuyCach;
	
	@Column(name="DUONGDAN")
	private String duongDan;
	
	

	public String getDuongDan() {
		return duongDan;
	}

	public void setDuongDan(String duongDan) {
		this.duongDan = duongDan;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTenQuyCach() {
		return tenQuyCach;
	}

	public void setTenQuyCach(String tenQuyCach) {
		this.tenQuyCach = tenQuyCach;
	}
	
	
}
