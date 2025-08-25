package com.example.e_commerce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "NENDA")
public class NenDa {
    @Id
    @Column(name = "NENDA_ID")
    private Integer id;
    @Column(name = "NENDA_TEN")
    private String ten;
    @Column(name = "NENDA_ANH")
    private String anhGioiThieu;
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "ROUTINE_NENDA",
        joinColumns = @JoinColumn (name = "NENDA_ID"),
        inverseJoinColumns = @JoinColumn(name = "RT_ID")
    )
    private List<RouTine> rouTines;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getAnhGioiThieu() { return anhGioiThieu; }
    public void setAnhGioiThieu(String anhGioiThieu) { this.anhGioiThieu = anhGioiThieu; }
}