package com.example.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "khuyenmai")
@Inheritance(strategy = InheritanceType.JOINED)
public class KhuyenMai {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "KM_ID")
	private long id;
	@Column(name = "KM_CONSUDUNG")
	private boolean conSuDung;
	@Column(name = "KM_THOIGIANAPDUNG")
	private LocalDateTime thoiGianApDung;
	@Column(name = "KM_THOIGIANNGUNG")
	private LocalDateTime thoiGianNgung;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isConSuDung() {
		return conSuDung;
	}
	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
	public LocalDateTime getThoiGianApDung() {
		return thoiGianApDung;
	}
	public void setThoiGianApDung(LocalDateTime thoiGianApDung) {
		this.thoiGianApDung = thoiGianApDung;
	}
	public LocalDateTime getThoiGianNgung() {
		return thoiGianNgung;
	}
	public void setThoiGianNgung(LocalDateTime thoiGianNgung) {
		this.thoiGianNgung = thoiGianNgung;
	}
	
	
}
