package com.example.e_commerce.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Routine")
public class RouTine {
	@Id
    @Column(name = "RT_ID")
    private Integer id;

    @Column(name = "RT_TEN")
    private String ten;

    @Column(name = "RT_ANHGIOTHI")
    private String anhGioiThieu;

    @Column(name = "RT_TAG")
    private String tag;

    @Column(name = "RT_MOTA", columnDefinition = "TEXT")
    private String moTa;
    
    @ManyToOne
    @JoinColumn(name = "ND_ID")
    private KhachHang khachHang;

    // Getters and Setters
    @ManyToMany(mappedBy = "rouTines")
    private List<NenDa> nenDas;
    
//    @ManyToMany(mappedBy = "rouTinesLiked")
//    private List<NguoiDung> likedByUsers;
//
//    @ManyToMany(mappedBy = "rouTinesUnliked")
//    private List<NguoiDung> unlikedByUsers;
//    
    
    
    public List<NenDa> getNenDas() {
		return nenDas;
	}
	public void setNenDas(List<NenDa> nenDas) {
		this.nenDas = nenDas;
	}
	
	
	public KhachHang getKhachHang() {
		return khachHang;
	}
	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}
	public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getAnhGioiThieu() { return anhGioiThieu; }
    public void setAnhGioiThieu(String anhGioiThieu) { this.anhGioiThieu = anhGioiThieu; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}