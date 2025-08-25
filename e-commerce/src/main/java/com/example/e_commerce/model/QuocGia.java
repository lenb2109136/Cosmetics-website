package com.example.e_commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quocgia")
public class QuocGia {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QG_ID")
    private Long id;

    @Column(name = "QG_TEN")
    private String ten;

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
    
    
}