package com.example.e_commerce.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Buoi")
public class Buoi {
    @Id
    private Integer id;

    private String ten;
    private String anhGioiThieu;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getAnhGioiThieu() { return anhGioiThieu; }
    public void setAnhGioiThieu(String anhGioiThieu) { this.anhGioiThieu = anhGioiThieu; }
}
