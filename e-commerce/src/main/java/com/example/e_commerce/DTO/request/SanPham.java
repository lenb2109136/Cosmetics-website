package com.example.e_commerce.DTO.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.ThongSo;
import com.example.e_commerce.model.ThongSoCuThe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SanPham {
	private List<BienThe> bienTheKhongLe = new ArrayList<BienThe>();
	private List<ThongSoCuThe> thongSo;
	private int khoiLuong;

	List<ItemQuyCachDongGoi> dongGoiNhap = new ArrayList<ItemQuyCachDongGoi>();

	public List<ItemQuyCachDongGoi> getDongGoiNhap() {
		return dongGoiNhap;
	}

	public void setDongGoiNhap(List<ItemQuyCachDongGoi> dongGoiNhap) {
		this.dongGoiNhap = dongGoiNhap;
	}

	String maVach;
	@NotBlank
	private String ten;

	@Min(value = 0)
	private int thuongHieu;
	@NotBlank
	private String moTa;
	@NotBlank
	private String thanhPhan;
	@NotBlank
	private String cachDung;
	@NotNull
	@Min(value = 0)
	private int danhMuc;
	@NotNull
	@Min(value = 0)
	private float gia = 0;

	List<Long> anhGioiThieuXoa;

	private float thue;

	public String getMaVach() {
		return maVach;
	}

	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}

	public int getKhoiLuong() {
		return khoiLuong;
	}

	public void setKhoiLuong(int khoiLuong) {
		this.khoiLuong = khoiLuong;
	}

	public void check() {
		if (ten == null || ten.length() == 0) {
			throw new GeneralException("Tên sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (thuongHieu == 0) {
			throw new GeneralException("Thương hiệu sản phẩm chưa được cung cấp", HttpStatus.BAD_REQUEST);
		}
		if (danhMuc == 0) {
			throw new GeneralException("Danh mục sản phẩm chưa được cung cấp", HttpStatus.BAD_REQUEST);
		}
		if (moTa == null || moTa.length() == 0) {
			throw new GeneralException("Mô tả sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (thanhPhan == null || thanhPhan.length() == 0) {
			throw new GeneralException("Thành phần sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (cachDung == null || cachDung.length() == 0) {
			throw new GeneralException("Cách dùng sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		bienTheKhongLe.forEach((data) -> {
			if (data.getGia() == 0) {
				throw new GeneralException("Giá phân loại phải lớn hơn 0", HttpStatus.BAD_REQUEST);
			}
			if (data.getMaVach()==null|| data.getMaVach().equals("")) {
				throw new GeneralException("Vui lòng cung cấp thông tin mã vạch cho phân loại", HttpStatus.BAD_REQUEST);
			}
			if (data.getKhoiLuong()<1) {
				throw new GeneralException("Vui lòng cung cấp khối lượng cho phân loại", HttpStatus.BAD_REQUEST);
			}
			if (data.getTen() == null || data.getTen().length() == 0) {
				throw new GeneralException("Tên phân loại không được để trống", HttpStatus.BAD_REQUEST);
			}
		});
		
		long sl=bienTheKhongLe.stream().map(d->d.getTen().trim()).distinct().count();
		if(sl != bienTheKhongLe.size()) {
			System.out.println("Tên của biến thể bị trungf vui lòng điều chỉnh lại");
		}
		// check thông tin mã vạch :
		if (bienTheKhongLe.size() != 0) {
			List<String> allMaVach = Stream
					.concat(
						bienTheKhongLe.stream().map(d -> d.getMaVach()),
						bienTheKhongLe.stream()
							.flatMap(d -> d.getDongGoiNhap().stream())
							.map(d -> d.getMaVach())
					)
					.map(s -> s.replaceAll("\\s+", "")) // loại bỏ khoảng trắng
					.collect(Collectors.toList());

			// Kiểm tra có mã vạch chứa chữ không
			boolean hasLetter = allMaVach.stream().anyMatch(s -> s.matches(".*[a-zA-Z]+.*"));
			if (hasLetter) {
				throw new GeneralException("Mã vạch không hợp lệ: không được chứa chữ cái", HttpStatus.BAD_REQUEST);
			}

			// Kiểm tra trùng
			boolean hasDuplicate = allMaVach.size() != allMaVach.stream().distinct().count();
			if (hasDuplicate) {
				throw new GeneralException("Mã vạch của các biến thể hoặc quy cách đóng gói bị trùng nhau", HttpStatus.BAD_REQUEST);
			}

		} else {
			List<String> allMaVach = dongGoiNhap.stream()
					.map(d -> d.getMaVach())
					.map(s -> s.replaceAll("\\s+", "")) // loại bỏ khoảng trắng
					.collect(Collectors.toList());

			allMaVach.add(maVach.replaceAll("\\s+", "")); // thêm mã chính vào

			// Kiểm tra có mã vạch chứa chữ không
			boolean hasLetter = allMaVach.stream().anyMatch(s -> s.matches(".*[a-zA-Z]+.*"));
			if (hasLetter) {
				throw new GeneralException("Mã vạch không hợp lệ: không được chứa chữ cái", HttpStatus.BAD_REQUEST);
			}

			// Kiểm tra trùng
			boolean hasDuplicate = allMaVach.size() != allMaVach.stream().distinct().count();
			if (hasDuplicate) {
				throw new GeneralException("Mã vạch của các biến thể hoặc quy cách đóng gói bị trùng nhau", HttpStatus.BAD_REQUEST);
			}
		}


	}

	public void checkForUpdate(List<MultipartFile> anhPhanLoaiNew) {
		if (ten == null || ten.length() == 0) {
			throw new GeneralException("Tên sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (thuongHieu == 0) {
			throw new GeneralException("Thương hiệu sản phẩm chưa được cung cấp", HttpStatus.BAD_REQUEST);
		}
		if (danhMuc == 0) {
			throw new GeneralException("Danh mục sản phẩm chưa được cung cấp", HttpStatus.BAD_REQUEST);
		}
		if (moTa == null || moTa.length() == 0) {
			throw new GeneralException("Mô tả sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (thanhPhan == null || thanhPhan.length() == 0) {
			throw new GeneralException("Thành phần sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
		if (cachDung == null || cachDung.length() == 0) {
			throw new GeneralException("Cách dùng sản phẩm không được để trống", HttpStatus.BAD_REQUEST);
		}
//		if (anhPhanLoaiNew!=null&& anhPhanLoaiNew.size() != getBienTheKhongLe().size()) {
//			throw new GeneralException("Vui lòng cung cấp đầy đủ ảnh cho phân loại", HttpStatus.BAD_REQUEST);
//		}
		
		bienTheKhongLe.forEach(c->{
			if(c.getKhoiLuong()==0) {
				throw new GeneralException("Khối lượng phân loại phải lớn hơn không", HttpStatus.BAD_REQUEST);
			}
		});
		
		

	}

	public List<Long> getAnhGioiThieuXoa() {
		return anhGioiThieuXoa;
	}

	public void setAnhGioiThieuXoa(List<Long> anhGioiThieuXoa) {
		this.anhGioiThieuXoa = anhGioiThieuXoa;
	}

	public float getThue() {
		return thue;
	}

	public void setThue(float thue) {
		this.thue = thue;
	}

	public SanPham() {
	}

	public List<ThongSoCuThe> getThongSo() {
		return thongSo;
	}

	public void setThongSo(List<ThongSoCuThe> thongSo) {
		this.thongSo = thongSo;
	}

	public List<BienThe> getBienTheKhongLe() {
		return bienTheKhongLe;
	}

	public void setBienTheKhongLe(List<BienThe> bienTheKhongLe) {
		this.bienTheKhongLe = bienTheKhongLe;
	}

	public float getGia() {
		return gia;
	}

	public void setGia(float gia) {
		this.gia = gia;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public int getThuongHieu() {
		return thuongHieu;
	}

	public void setThuongHieu(int thuongHieu) {
		this.thuongHieu = thuongHieu;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public String getThanhPhan() {
		return thanhPhan;
	}

	public void setThanhPhan(String thanhPhan) {
		this.thanhPhan = thanhPhan;
	}

	public String getCachDung() {
		return cachDung;
	}

	public void setCachDung(String cachDung) {
		this.cachDung = cachDung;
	}

	public int getDanhMuc() {
		return danhMuc;
	}

	public void setDanhMuc(int danhMuc) {
		this.danhMuc = danhMuc;
	}
}
