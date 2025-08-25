package com.example.e_commerce.DTO.request.FlashSaledto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.KhuyenMaiTangKem;
import com.example.e_commerce.service.DealService;
import com.example.e_commerce.service.FlashSaleService;
import com.example.e_commerce.service.KhuyenMaiTangKemSerivce;
import com.example.e_commerce.service.ServiceLocator;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FlashSaleDTO {
	//bổ sung cho phần deal
//	@NotBlank(message = "Vui lòng cung cấp tên chương trình")
	private String tenChuongTrinh;
	@NotNull(message = "Thời điểm ngưng áp dụng chưa được cung cấp")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime ngayKetThuc;
	@NotNull(message = "Thời điểm áp dụng chưa được cung cấp")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime ngayBatDau;
//	@NotNull(message = "Khung giờ bắt đầu chưa được cung cấp")
	@JsonFormat(pattern = "HH:mm")
	LocalTime thoiGianBatDau;
//	@NotNull(message = "Khung giờ kết thúc chưa được cung cấp")
	@JsonFormat(pattern = "HH:mm")
	LocalTime thoiGianKetThuc;
//	@Size(min = 1, message = "Bạn phải cung cấp nhiều hơn một phân loại trong khuyến mãi")
	List<SanPhamFlashSale> data =new ArrayList<SanPhamFlashSale>();
	//Bổ sung them cho deal ( danh sách các deal phụ)
//	@Size(min = 1, message = "Bạn phải cung cấp nhiều hơn một phân loại trong deal phụ")
	List<SanPhamFlashSale> dataPhu =new ArrayList<SanPhamFlashSale>();
	
	int soLuongDaDung;
	int soLuongGioiHan;
	 boolean canUpdate=true;
	 
	
	public boolean isCanUpdate() {
		return canUpdate;
	}
	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}
	public void validate() {
		if(ngayBatDau ==null || ngayKetThuc==null) {
			throw new GeneralException("Vui lòng cung cấp ngày chạy flashsale",HttpStatus.BAD_REQUEST);
		}
		if (ngayBatDau.isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Thời gian bắt đầu không thể nhỏ hơn hiện tại", HttpStatus.BAD_REQUEST);
		}

		if(thoiGianBatDau ==null || thoiGianKetThuc==null) {
			throw new GeneralException("Vui lòng cung cấp thời gian chạy flashsale",HttpStatus.BAD_REQUEST);
		}
		if(getThoiGianKetThuc().isBefore(getThoiGianBatDau())) {
			throw new GeneralException("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		long v=data.stream().filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.giaGiam>0&&f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).count();
		
	}
	public void validatedDealForUpdate(long id) {
		if(!getNgayKetThuc().isAfter(getNgayBatDau())) {
			throw new GeneralException("Thời gian bắt đầu phải lớn hơn thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		
		DealService dealService=ServiceLocator.getBean(DealService.class);
		Deal deal=dealService.getById(id);
		
		List<Long> idSPCHINH=deal.getBienTheDealChinh().stream().map(data->{
			return data.getBienThe().getSanPham().getId();
		}).distinct().collect(Collectors.toList());
		List<Long> idSPPHU=deal.getBienTheDealPhu().stream().map(data->{
			return data.getBienThe().getSanPham().getId();
		}).distinct().collect(Collectors.toList());
		data.stream().filter(d->!idSPCHINH.contains(d.getId())).filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				System.out.println("qua : "+f.soLuongKhuyenMai);
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			System.out.println("Count: "+count);
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm chính: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());
		dataPhu.stream().filter(d->!idSPPHU.contains(d.getId())).filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.giaGiam>0&&f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Deal giảm giá sản phẩm: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());;
	}
	
	public void validatedBonusForUpdate(long id) {
		if(!getNgayKetThuc().isAfter(getNgayBatDau())) {
			throw new GeneralException("Thời gian bắt đầu phải lớn hơn thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		KhuyenMaiTangKemSerivce khuyenMaiTangKemSerivce=ServiceLocator.getBean(KhuyenMaiTangKemSerivce.class);
		KhuyenMaiTangKem khuyenMaiTangKem=khuyenMaiTangKemSerivce.getById(id);
		List<Long> idSPCHINH=khuyenMaiTangKem.getSanPhamChinh().stream().map(data->{
			return data.getBienThe().getSanPham().getId();
		}).distinct().collect(Collectors.toList());
		List<Long> idSPPHU=khuyenMaiTangKem.getSanPhamPhu().stream().map(data->{
			return data.getBienThe().getSanPham().getId();
		}).distinct().collect(Collectors.toList());
		data.stream().filter(d->!idSPCHINH.contains(d.getId())).filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm chính: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());
		dataPhu.stream().filter(d->!idSPPHU.contains(d.getId())).filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm tặng kèm: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());;
	}
	
	public void validatedDeal() {
		if(getNgayBatDau().isBefore(LocalDateTime.now())) {
			throw new GeneralException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại",HttpStatus.BAD_REQUEST);
		}
		if(!getNgayKetThuc().isAfter(getNgayBatDau())) {
			throw new GeneralException("Thời gian bắt đầu phải lớn hơn thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		
		data.stream().filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				System.out.println("qua : "+f.soLuongKhuyenMai);
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			System.out.println("Count: "+count);
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm chính: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());
		dataPhu.stream().filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.giaGiam>0&&f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Deal giảm giá sản phẩm: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());;
	}
	
	public void validatedBonus() {
		if(!getNgayKetThuc().isAfter(getNgayBatDau())) {
			throw new GeneralException("Thời gian bắt đầu phải lớn hơn thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		
		data.stream().filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Sản phẩm chính: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());
		dataPhu.stream().filter((d)->{
			long count =d.getBienThe().stream().filter(f->{
				if(f.soLuongKhuyenMai>0) {return true;}
				else {
					return false;
				}
				
			}).count();
			if(count>0) {
				return true;
			}
			else { throw new GeneralException("Deal giảm giá sản phẩm: "+ d.getTen()+ " Chưa chọn phân loại nào" ,HttpStatus.BAD_REQUEST);}
		}).collect(Collectors.toList());;
	}
	public int getSoLuongDaDung() {
		return soLuongDaDung;
	}
	public void setSoLuongDaDung(int soLuongDaDung) {
		this.soLuongDaDung = soLuongDaDung;
	}
	public void ValidateForUpdate(long id) {
		FlashSaleService flashSaleService= ServiceLocator.getBean(FlashSaleService.class);
		FlashSale flash= flashSaleService.getById(id);
		List<Long> dsSanPham=flash.getBienTheFlashSale().stream().map((d)->{
			return d.getBienThe().getSanPham().getId();
		}).distinct().collect(Collectors.toList());
		data.stream().filter((d)->{
			return !dsSanPham.contains(d.getId());
		}).forEach((f)->{
			long l=f.bienThe.stream().filter((df)->{
				if(df.getGiaGiam()>0&&df.getSoLuongKhuyenMai()>0) {
					return true;
				}
				else {
					return false;
				}
			}).count();
			if(l<=0) {
				throw new GeneralException("Sản phẩm thêm bổ sung: "+f.getTen()+" chưa cung cấp phân loại nào phù hợp" ,HttpStatus.BAD_REQUEST);
			}
		});
	}
	public List<SanPhamFlashSale> getData() {
		return data;
	}

	public void setData(List<SanPhamFlashSale> data) {
		this.data = data;
	}

	
	public int getSoLuongGioiHan() {
		return soLuongGioiHan;
	}
	public void setSoLuongGioiHan(int soLuongGioiHan) {
		this.soLuongGioiHan = soLuongGioiHan;
	}
	public LocalDateTime getNgayKetThuc() {
		return ngayKetThuc;
	}
	
	
	
	
	public String getTenChuongTrinh() {
		return tenChuongTrinh;
	}
	public void setTenChuongTrinh(String tenChuongTrinh) {
		this.tenChuongTrinh = tenChuongTrinh;
	}
	public List<SanPhamFlashSale> getDataPhu() {
		return dataPhu;
	}
	public void setDataPhu(List<SanPhamFlashSale> dataPhu) {
		this.dataPhu = dataPhu;
	}
	public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}
	public LocalDateTime getNgayBatDau() {
		return ngayBatDau;
	}
	public void setNgayBatDau(LocalDateTime ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}
	public LocalTime getThoiGianBatDau() {
		return thoiGianBatDau;
	}
	public void setThoiGianBatDau(LocalTime thoiGianBatDau) {
		this.thoiGianBatDau = thoiGianBatDau;
	}
	public LocalTime getThoiGianKetThuc() {
		return thoiGianKetThuc;
	}
	public void setThoiGianKetThuc(LocalTime thoiGianKetThuc) {
		this.thoiGianKetThuc = thoiGianKetThuc;
	}
	
	
}
