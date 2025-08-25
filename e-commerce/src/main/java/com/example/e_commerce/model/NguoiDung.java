package com.example.e_commerce.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "nguoidung")
public class NguoiDung {
    
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "ND_ID")
	    private long id;

	    @NotBlank(message = "Mật khẩu không được để trống")
	    @Size(min = 6, max = 50, message = "Mật khẩu phải từ 6 đến 50 ký tự")
	    @Column(name = "ND_MATKHAU", nullable = false)
	    private String matkhau;

	    @NotBlank(message = "Số điện thoại không được để trống")
	    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ")
	    @Column(name = "ND_SDT", nullable = false, unique = true)
	    private String sodienthoai;

	    @NotBlank(message = "Tên không được để trống")
	    @Size(max = 100, message = "Tên tối đa 100 ký tự")
	    @Column(name = "ND_TEN", nullable = false)
	    private String ten;

	    @NotBlank(message = "Email không được để trống")
	    @Email(message = "Email không hợp lệ")
	    @Size(max = 100, message = "Email tối đa 100 ký tự")
	    @Column(name = "ND_EMAIL", nullable = false, unique = true)
	    private String email;

	    @NotBlank(message = "Địa chỉ không được để trống")
	    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
	    @Column(name = "ND_DIACHI", nullable = false)
	    private String diachi;
    
    
//    @ManyToMany
//	@JoinTable(
//	    name = "ROUTINE_LIKE",
//	    joinColumns = @JoinColumn(name = "ND_ID", referencedColumnName = "id"),
//	    inverseJoinColumns = @JoinColumn(name = "RT_ID", referencedColumnName = "id")
//	)
//	private List<RouTine> rouTinesLiked;
//
//	// Những routine mà khách hàng đã unlike
//	@ManyToMany
//	@JoinTable(
//	    name = "ROUTINE_UNLIKE",
//	    joinColumns = @JoinColumn(name = "ND_ID", referencedColumnName = "id"),
//	    inverseJoinColumns = @JoinColumn(name = "RT_ID", referencedColumnName = "id")
//	)
//	private List<RouTine> rouTinesUnliked;
//	
//	
//	
//	
//	public List<RouTine> getRouTinesLiked() {
//		return rouTinesLiked;
//	}
//
//	public void setRouTinesLiked(List<RouTine> rouTinesLiked) {
//		this.rouTinesLiked = rouTinesLiked;
//	}
//
//	public List<RouTine> getRouTinesUnliked() {
//		return rouTinesUnliked;
//	}
//
//	public void setRouTinesUnliked(List<RouTine> rouTinesUnliked) {
//		this.rouTinesUnliked = rouTinesUnliked;
//	}
	

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}
