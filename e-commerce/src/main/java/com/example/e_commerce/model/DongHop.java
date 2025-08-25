package com.example.e_commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DONGHOP")
public class DongHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DH_ID")
    private Integer id;

    @Column(name = "DH_TEN", length = 50)
    private String ten;

    @Column(name = "DH_MOTA", length = 50)
    private String moTa;

    @Column(name = "DH_CHIEUDAI")
    private int chieuDai;

    @Column(name = "DH_CHIEURONG")
    private int chieuRong;

    @Column(name = "DH_CHIEUCAO")
    private int chieuCao;

    @Column(name = "DH_CANNANGBOSUNG")
    private int canNangBoSung;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public int getChieuDai() {
		return chieuDai;
	}

	public void setChieuDai(int chieuDai) {
		this.chieuDai = chieuDai;
	}

	public int getChieuRong() {
		return chieuRong;
	}

	public void setChieuRong(int chieuRong) {
		this.chieuRong = chieuRong;
	}

	public int getChieuCao() {
		return chieuCao;
	}

	public void setChieuCao(int chieuCao) {
		this.chieuCao = chieuCao;
	}

	public int getCanNangBoSung() {
		return canNangBoSung;
	}

	public void setCanNangBoSung(int canNangBoSung) {
		this.canNangBoSung = canNangBoSung;
	}

	
    
}
