package com.example.e_commerce.model;

import java.util.List;

import jakarta.persistence.*;
@Entity
@Table(name = "STEP")
public class Step {
	 @Id
	    @Column(name = "ST_ID_STEP")
	    private Integer id;

	    @Column(name = "ST_TEN")
	    private String ten;

	    @Column(name = "ST_ANHGIOTHI")
	    private String anhGioiThieu;

    
    
    
    // Getters and Setters
    
    public Integer getId() { return id; }
    
	public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getAnhGioiThieu() { return anhGioiThieu; }
    public void setAnhGioiThieu(String anhGioiThieu) { this.anhGioiThieu = anhGioiThieu; }
}