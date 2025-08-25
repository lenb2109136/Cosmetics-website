package com.example.e_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pushnotifycation")
public class Subscription {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long PS_ID;
	
	private String endpoint;
	
	private String p256dh;
	
	private String auth;
	
	@ManyToOne
	@JoinColumn(name = "ND_ID")
	private NguoiDung nguoiDung;

	public long getPS_ID() {
		return PS_ID;
	}

	public void setPS_ID(long pS_ID) {
		PS_ID = pS_ID;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getP256dh() {
		return p256dh;
	}

	public void setP256dh(String p256dh) {
		this.p256dh = p256dh;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public NguoiDung getNguoiDung() {
		return nguoiDung;
	}

	public void setNguoiDung(NguoiDung nguoiDung) {
		this.nguoiDung = nguoiDung;
	}
	
	
}
