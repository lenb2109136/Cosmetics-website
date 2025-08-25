package com.example.e_commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="ANHCHAT")
public class AnhChat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AC_ID")
	private long id;

	@Column(name = "AC_DUONGDAN")
	private String duongDan;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "TN_ID")
	private TinNhan tinNhan;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDuongDan() {
		return duongDan;
	}

	public void setDuongDan(String duongDan) {
		this.duongDan = duongDan;
	}

	public TinNhan getTinNhan() {
		return tinNhan;
	}

	public void setTinNhan(TinNhan tinNhan) {
		this.tinNhan = tinNhan;
	}
	
	
}
