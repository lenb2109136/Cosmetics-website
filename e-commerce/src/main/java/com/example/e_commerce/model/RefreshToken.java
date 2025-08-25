package com.example.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refreshtoken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "R_ID")
    private Long id;

    @Column(name = "R_NGAYTAO", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "R_NGAYHETHAN", nullable = false)
    private LocalDateTime ngayHetHan;

    @Column(name = "R_REFRESH", nullable = false, length = 255)
    private String refresh;

    @Column(name = "R_BITHUHOI", nullable = false)
    private boolean biThuHoi = false;

    @ManyToOne()
    @JoinColumn(name = "ND_ID", nullable = false)
    private NguoiDung nguoiDung;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao) {
		this.ngayTao = ngayTao;
	}

	public LocalDateTime getNgayHetHan() {
		return ngayHetHan;
	}

	public void setNgayHetHan(LocalDateTime ngayHetHan) {
		this.ngayHetHan = ngayHetHan;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	public boolean isBiThuHoi() {
		return biThuHoi;
	}

	public void setBiThuHoi(boolean biThuHoi) {
		this.biThuHoi = biThuHoi;
	}

	public NguoiDung getNguoiDung() {
		return nguoiDung;
	}

	public void setNguoiDung(NguoiDung nguoiDung) {
		this.nguoiDung = nguoiDung;
	}
    
}
