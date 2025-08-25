package com.example.e_commerce.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.compress.harmony.pack200.NewAttributeBands.Integral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.SanPham;
import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.DTO.request.FlashSaledto.SanPhamFlashSale;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.mapper.FlashSaleMapper;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.embeded.EChiTietHoaDon;
import com.example.e_commerce.repository.FlashSaleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FlashSaleService {
	
	@Autowired
	private FlashSaleRepository flashSaleRepository;
	
	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;
	
	@Autowired
	BienTheFlashSaleService bienTheFlashSaleService;
	
	@Autowired
	BienTheSerVice bienTheSerVice;
	
	@Autowired
	private DealService dealService;
	
	public FlashSale getById(long id) {
		return flashSaleRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Flash sale không tồn tại"));
	}
	
	@Transactional
	public void save(FlashSale flashSale) {
		
		if(flashSale.getThoiGianApDung().isAfter(flashSale.getThoiGianNgung())) {
			throw new GeneralException("Thời gian áp dụng khuyến mãi sau thời gian kết thúc",HttpStatus.BAD_REQUEST);
		}
		flashSaleRepository.save(flashSale);
	}
	
	public List<FlashSale> getFlashSaleBienThe(int id){
		return flashSaleRepository.getFlashSaleTheoBienThe(id);
	}
	public FlashSale getById(Long id) {
		return flashSaleRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Không tìm thấy thông tin flashsale"));
	}
	
	public void CheckDuplicateDealOfBienThe(FlashSale flash, int idBienThe, String tenBienThe) {
		List<FlashSale> flashSale=getFlashSaleBienThe(idBienThe);
		flashSale.stream().filter(dg-> dg.getId()!=flash.getId()).forEach((b)->{
			if(TimeValidator.isOverlap(b.getThoiGianApDung(),
					b.getThoiGianNgung(),
					flash.getThoiGianApDung(), flash.getThoiGianNgung())==true) {
				if(TimeValidator.isOverlap(b.getGioBatDau(),
						b.getGioKetThuc(), flash.getGioBatDau(), flash.getGioKetThuc())==true) {
					throw new GeneralException("Phân loại: "+tenBienThe+ " bị trùng thời gian với flash sale khác", HttpStatus.BAD_REQUEST);
				}
			}
		});
	}
	public void CheckDuplicateDealOfBienTheV2(LocalDateTime bd, LocalDateTime kt, int idBienThe, String tenBienThe) {
	    List<FlashSale> flashSales = getFlashSaleBienThe(idBienThe);
	    flashSales.forEach(b -> {
	        if (TimeValidator.isOverlap(b.getThoiGianApDung(), b.getThoiGianNgung(), bd, kt)) {
	            throw new GeneralException("Phân loại: " + tenBienThe + " đang áp dụng chương trình flashsale", HttpStatus.BAD_REQUEST);
	        }
	    });
	}
	@Transactional
	public void saveFromDTO(FlashSaleDTO flashSaleDTO) {
		
		FlashSale f= FlashSaleMapper.getFlashFromDTO(flashSaleDTO);
		save(f);
		List<BienTheFlashSale> danhSach= FlashSaleMapper.getBienTheFlashFromDTO(flashSaleDTO);
		danhSach=danhSach.stream().filter((d)->{
			if(d.getGiaTriGiam()>0&&d.getSoLuongGioiHan()>0) {
				
			return true;
			}
			return false;
		}).collect(Collectors.toList());
		if(danhSach.size()==0) {
			throw new GeneralException("Vui lòng cung cấp nhiều hơn một phân loại",HttpStatus.BAD_REQUEST);
		}
		for(int i=0;i<danhSach.size();i++) {
			BienThe bienThe= bienTheSerVice.getById(danhSach.get(i).getId().getBtId());
			CheckDuplicateDealOfBienThe(f, bienThe.getId(),bienThe.getTen());
			dealService.CheckDuplicateDealOfBienTheV2(f.getThoiGianApDung(),f.getThoiGianNgung(),bienThe.getId(),bienThe.getTen());
			danhSach.get(i).setBienThe(bienThe);
			danhSach.get(i).setFlashSale(f);
			danhSach.get(i).setConSuDung(true);
			bienTheFlashSaleService.save(danhSach.get(i));
		}
	}
	
	public List<Map<String, Object>> getFlashActiveOfBienThe(BienThe bienThe, List<Long> l){
	    if (l == null) {
	        l = new ArrayList<Long>();
	    }

	    final List<Long> lFinal = l;
	    List<Map<String, Object>> danhSachFlashSale = bienTheFlashSaleService
	            .getBienTheFlashSaleOfBienTHe(bienThe.getId())
	            .stream()
	            .filter(df -> !lFinal.contains(df.getFlashSale().getId()))
	            .map(di -> {
	                if (di.getSoLuongGioiHan() - di.getSoLuongDaDung() > 0
	                        && di.getFlashSale().isConSuDung()
	                        && di.isConSuDung()
	                        && LocalDateTime.now().isAfter(di.getFlashSale().getThoiGianApDung())
	                        && LocalDateTime.now().isBefore(di.getFlashSale().getThoiGianNgung())) {
	                    Map<String, Object> mi = new HashMap<>();
	                    mi.put("thoiGianChay", di.getFlashSale().getThoiGianApDung());
	                    mi.put("thoiGianNgung", di.getFlashSale().getThoiGianNgung());
	                    mi.put("thoiDiemBatDau", di.getFlashSale().getGioBatDau());
	                    mi.put("thoiDiemKetThuc", di.getFlashSale().getGioKetThuc());
	                    mi.put("giaTriGiam", di.getGiaTriGiam());
	                    mi.put("idFS", di.getFlashSale().getId());
	                    return mi;
	                }
	                return null;
	            })
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());

	    return danhSachFlashSale;
	}
	
	@Transactional
	public void unActiveClassifyFlashSaleByProduct(long id, List<Long> ds) {
		flashSaleRepository.unActiveFlashSale(id, ds);
	}
	
	public void UpDate(FlashSaleDTO flashSaleDTO, long id) {
		FlashSale flash= getById(id);
		if(flash.getThoiGianApDung().isBefore(LocalDateTime.now()) && flash.getThoiGianApDung().equals(flashSaleDTO.getNgayBatDau())==false) {
			 throw new GeneralException("Bạn không được điều chỉnh khi flashsale đã chạy",HttpStatus.BAD_REQUEST);
		}
		if (flash.getThoiGianNgung().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể điều chỉnh do flash sale đã hết hạn",HttpStatus.BAD_REQUEST);
		}
		FlashSale flashd= FlashSaleMapper.getFlashFromDTO(flashSaleDTO);
		if(flashd.getThoiGianNgung().isBefore(LocalDateTime.now()) && flash.getThoiGianNgung().equals(flashSaleDTO.getNgayBatDau())==false) {
			throw new GeneralException("Thời điểm kết thúc phải lớn hơn hiện tại",HttpStatus.BAD_REQUEST);
		}
		if(flashd.getThoiGianApDung().isBefore(LocalDateTime.now()) && flash.getThoiGianApDung().equals(flashSaleDTO.getNgayBatDau())==false) {
			throw new GeneralException("Thời điểm bắt đầu phải lớn hơn hiện tại",HttpStatus.BAD_REQUEST);
		}
		flash.setGioBatDau(flashd.getGioBatDau());
		flash.setGioKetThuc(flashd.getGioKetThuc());
		flash.setThoiGianNgung(flashd.getThoiGianNgung());
		flash.setThoiGianApDung(flashd.getThoiGianApDung());
		save(flash);
		List<BienTheFlashSale> bienTheFlashSales= FlashSaleMapper.getBienTheFlashFromDTO(flashSaleDTO);
		List<BienTheFlashSale> bienTheFlashSales2=flash.getBienTheFlashSale();
		List<Integer> idOld = bienTheFlashSales2.stream().map(d->d.getId().getBtId()).collect(Collectors.toList());
		List<BienTheFlashSale> bienTheFlashSalesOld = bienTheFlashSales.stream()
		        .filter(b -> {

		        	return idOld.contains(b.getId().getBtId());
		        })
		        .collect(Collectors.toList());
		List<BienTheFlashSale> bienTheFlashSalesNew = bienTheFlashSales.stream()
		        .filter(b -> !idOld.contains(b.getId().getBtId()))
		        .collect(Collectors.toList());
		
		// biến thể cũ 
	   bienTheFlashSalesOld.forEach(data->{
		   BienTheFlashSale b= bienTheFlashSaleService.getBienTheFlahSale(flash.getId(), data.getId().getBtId());
		  
		   
		   if(data.isConSuDung()==true) {
			   if(data.getGiaTriGiam()<=0||data.getSoLuongGioiHan()<=0) {
				   throw new GeneralException("Vui lòng kiểm tra số lượng và tỉ lệ giảm khuyến mãi của sản phẩm: "+b.getBienThe().getSanPham().getTen()+ 
						   " - "+b.getBienThe().getTen(), HttpStatus.BAD_REQUEST);
			   }
			   if(data.getSoLuongGioiHan()<b.getSoLuongDaDung()) {
				   throw new GeneralException("Số lượng khuyến mãi cập nhật chỉ có thể lớn hơn hoặc bằng số lượng đã sử dụng", HttpStatus.BAD_REQUEST);
			   }
			  CheckDuplicateDealOfBienThe(flash, b.getBienThe().getId(),b.getBienThe().getTen());
			  dealService.CheckDuplicateDealOfBienTheV2(flash.getThoiGianApDung(),flash.getThoiGianNgung(),b.getBienThe().getId(),b.getBienThe().getTen());
		   }
		   
		   b.setConSuDung(data.isConSuDung());
		   b.setSoLuongGioiHan(data.getSoLuongGioiHan());
		   b.setGiaTriGiam(data.getGiaTriGiam());
		   bienTheFlashSaleService.saveNotValidate(b);
	   });
	   //biến thể mới 
	   bienTheFlashSalesNew=bienTheFlashSalesNew.stream().filter((d)->{
			if(d.getGiaTriGiam()>0&&d.getSoLuongGioiHan()>0) {
				
			return true;
			}
			return false;
		}).collect(Collectors.toList());
	   for(int i=0;i<bienTheFlashSalesNew.size();i++) {
			BienThe bienThe= bienTheSerVice.getById(bienTheFlashSalesNew.get(i).getId().getBtId());
			CheckDuplicateDealOfBienThe(flash, bienThe.getId(),bienThe.getTen());
			 dealService.CheckDuplicateDealOfBienTheV2(flash.getThoiGianApDung(),flash.getThoiGianNgung(),bienThe.getId(),bienThe.getTen());
			bienTheFlashSalesNew.get(i).setBienThe(bienThe);
			bienTheFlashSalesNew.get(i).setFlashSale(flash);
			bienTheFlashSalesNew.get(i).setConSuDung(true);
			bienTheFlashSaleService.save(bienTheFlashSalesNew.get(i));
		}
		
	}

	public FlashSaleDTO getFlashSaleById(long id) {
	    FlashSale flashSale = getById(id);
	    
	    FlashSaleDTO flashSaleDTO = FlashSaleMapper.MapToFlashDTOFromFlash(flashSale);
	    if (flashSale.getThoiGianNgung().isBefore(LocalDateTime.now())) {
	        flashSaleDTO.setCanUpdate(false);
	    } else {
	        flashSaleDTO.setCanUpdate(true);
	    }
	    return flashSaleDTO;
	}
	public Page<Map<String, Object>> getByStatus(
			int trang, LocalDateTime bd, LocalDateTime kt, int status
			){
		Page<FlashSale> flashSales;
	
		if(status==2) {
		flashSales=	flashSaleRepository.getFlashSaleBeforNow(bd, kt, PageRequest.of(trang, 5));
		}
		else if(status==1) {

			flashSales=	flashSaleRepository.getFlashSaleInNow(bd, kt, PageRequest.of(trang, 5));
			}
		else if (status==3) {
			flashSales=	flashSaleRepository.getFlashSaleAfterNow(bd, kt, PageRequest.of(trang, 5));
			}
		else{
			flashSales=	flashSaleRepository.findAll(PageRequest.of(trang, 5));
			}
		
		return flashSales.map(data -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("thoiGianApDung", data.getThoiGianApDung());
	        map.put("thoiGianNgung", data.getThoiGianNgung());
	        map.put("gioBatDau", data.getGioBatDau());
	        map.put("gioKetThuc", data.getGioKetThuc());
	        map.put("conSuDung", data.isConSuDung());
	        map.put("id", data.getId());

	        long soLuongSanPham = data.getBienTheFlashSale().stream()
	            .map(dat -> dat.getBienThe().getSanPham().getId())
	            .distinct()
	            .count();

	        long tongSoLuotDung = data.getBienTheFlashSale().stream()
	            .mapToInt(dat -> dat.getSoLuongDaDung())
	            .sum();

	        map.put("soLuongDaDung", tongSoLuotDung);
	        map.put("tongSoSanPham", soLuongSanPham);

	        return map;
	    });
		
	}
	public void setActiveFlashSale(long id,boolean active) {
		FlashSale f=getById(id);
		if (f.getThoiGianNgung().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể điều chỉnh do flash sale đã hết hạn",HttpStatus.BAD_REQUEST);
		}
		if(active==true) {
			List<BienTheFlashSale> danhSachBienThe=f.getBienTheFlashSale();
			
			danhSachBienThe.forEach(d->{
				CheckDuplicateDealOfBienThe(f, d.getBienThe().getId(),d.getBienThe().getTen());
				 dealService.CheckDuplicateDealOfBienTheV2(f.getThoiGianApDung(),f.getThoiGianNgung(),d.getBienThe().getId(),d.getBienThe().getTen());
			});
		}
		f.setConSuDung(active);
		flashSaleRepository.save(f);
	}
	
	public Map<String, Object> getThongKeFlashSaleNew(long fl,FlashSale f,LocalDateTime bd, LocalDateTime kt ) {
	    List<Integer> vv = Arrays.asList(1, 2, 3,6,12,13);
	    SanPhamService sanPhamService = ServiceLocator.getBean(SanPhamService.class);
	    
	  
	    // lấy danh sách các biến thể đang áp dụng flashsale
	    List<Integer> danhSachSuDung = f.getBienTheFlashSale() != null 
	            ? f.getBienTheFlashSale().stream()
	                .map(d -> d.getBienThe() != null ? d.getBienThe().getId() : null)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList())
	            : new ArrayList<>();
	    
	    // copy
	    List<BienTheFlashSale> danhSachSuDung2 = f.getBienTheFlashSale() != null 
	            ? f.getBienTheFlashSale() 
	            : new ArrayList<>();
	    // lấy toàn bộ các hóa đơn có trong khoảng thời gian bắt đầu kết thúc đó
	    List<ChiTietHoaDon> danhSachCacHoaDon=chiTietHoaDonService.getHoaDonOfSanPham(danhSachSuDung, bd, kt, vv);
	    
	    // lọc lại những hóa đơn có trong flashsale
//	    List<ChiTietHoaDon> chiTietHoaDons=danhSachCacHoaDon.stream()
//	    		.filter(d->{
//	    			long a=d.getDanhSachKhuyenMai().stream().map(m->m.getKhuyenMai().getId()==f.getId()).count();
//	    			if(a>0) {
//	    				return true;
//	    			}
//	    			else {
//	    				return false;
//	    			}
//	    		}).collect(Collectors.toList());
	    List<ChiTietHoaDon> chiTietHoaDons=danhSachCacHoaDon;
	    // danh sách các biến cho thống kê
	    Map<String, Object> result = new HashMap<>();
	    result.put("tongSoLuongHoaDon", chiTietHoaDons.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList()).size());
//	    tổng số hóa đơn bán trong kỳ này có áp dụng
	    result.put("tongSoLuongHoaDonCoApDung", chiTietHoaDons.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList()).size());
	    
//	     TÍNH CHO CÁC HÓA ĐƠN CÙNG KỲ NHƯNG CÓ KHUYẾN MÃI
	    AtomicLong soLuongNguoiMuaMoi = new AtomicLong(0);
	    AtomicLong soLuongNguoiMuaLai = new AtomicLong(0);
	    AtomicLong soLuongBanRa = new AtomicLong(0);
	    AtomicReference<Float> tonGiaTriTatCaHoaDon = new AtomicReference<>(0f);
	    Set<Long> tongSoHoaDon = new HashSet<>();
	    Set<Long> khachHangMuaMoi = new HashSet<>();
	    Set<Long> khachHangMuaLai = new HashSet<>();

	    chiTietHoaDons.forEach(d -> {
	        tongSoHoaDon.add(d.getHoaDon().getId());
	        tonGiaTriTatCaHoaDon.updateAndGet(v -> v + d.getTongTien());
	        soLuongBanRa.addAndGet(d.getSoLuong());

	        Long khachHangId = d.getHoaDon().getKhachHang() != null 
	                ? d.getHoaDon().getKhachHang().getId() 
	                : 0L;

	        if (chiTietHoaDonService.getTongSoLuotSuDungTruocDo(
	                d.getBienThe().getId(), bd, vv, khachHangId) > 0) {
	            khachHangMuaLai.add(khachHangId);
	        } else {
	            khachHangMuaMoi.add(khachHangId);
	        }
	    });

	    soLuongNguoiMuaLai.set(khachHangMuaLai.size());
	    soLuongNguoiMuaMoi.set(khachHangMuaMoi.size());
	    result.put("tonSoLuotBan", tongSoHoaDon.size());
	    // tổng số lượng người mua mới
	    result.put("tongSoLuongKhachMuaLai", soLuongNguoiMuaLai.get());
	    // tổng số lượng người mua cũ
	    result.put("tongSoLuongMuaMoi", soLuongNguoiMuaMoi.get());
	    // tổng giá trị tất cả các lần bán của các biến thể này
	    result.put("tongGiaTriTatCaHoaDon", tonGiaTriTatCaHoaDon.get());
	    // tổng số hóa đơn bán trong kỳ này 
	    float giaTriTrungBinh = 0f;
	    int soHoaDon = tongSoHoaDon.size();
	    if (soHoaDon > 0) {
	        giaTriTrungBinh = tonGiaTriTatCaHoaDon.get() / soHoaDon;
	    }
//	     giá trị trung bình trên mỗi đơn
	    result.put("giaTriTrungBinhTrenMoiHoaDon", giaTriTrungBinh);
	    // số,lywong bán ra
	    result.put("soLuongBanRa", soLuongBanRa);
	    
	    
	    // CÙNG KỲ NHƯNG LÀ NHỮNG HÓA ĐƠN KHÔNG ÁP DỤNG KHUYẾN MÃI 
//	    List<ChiTietHoaDon> chiTietHoaDonKhongApDung = danhSachCacHoaDon.stream()
//	    	    .filter(d -> !chiTietHoaDons.contains(d))
//	    	    .collect(Collectors.toList());
//	    AtomicLong soLuongNguoiMuaMoiCungKy = new AtomicLong(0);
//	    AtomicLong soLuongNguoiMuaLaiCungKy = new AtomicLong(0);
//	    AtomicReference<Float> tonGiaTriTatCaHoaDonCungKy = new AtomicReference<>(0f);
//	    Set<Long> tongSoHoaDonCungKy = new HashSet<>();
//	    AtomicLong soLuongBanRaCụngKy = new AtomicLong(0);
//	    chiTietHoaDonKhongApDung.forEach(d -> {
//	    	
//	        tongSoHoaDonCungKy.add(d.getHoaDon().getId());
//	    	tonGiaTriTatCaHoaDonCungKy.updateAndGet(v->v+d.getTongTien());
//	    	soLuongBanRaCụngKy.addAndGet(d.getSoLuong());
//	    	// tính lượt mua mới cũ
//	    	if (chiTietHoaDonService.getTongSoLuotSuDungTruocDo(
//                    d.getBienThe().getId(), f.getThoiGianApDung(), vv, 
//                    d.getHoaDon().getKhachHang() != null ? d.getHoaDon().getKhachHang().getId() : 0) > 0) {
//                soLuongNguoiMuaLaiCungKy.addAndGet(1);
//            } else {
//                soLuongNguoiMuaMoiCungKy.addAndGet(1);
//            }
//	    });
//	    // tổng số lượt bán có áp dụng
//	    result.put("tonSoLuotBanCungKy", tongSoHoaDonCungKy.size());
//	    // tổng số lượng người mua mới
//	    result.put("tongSoLuongKhachMuaLaiCungKy", soLuongNguoiMuaLaiCungKy.get());
//	    // tổng số lượng người mua cũ
//	    result.put("tongSoLuongMuaMoiCungKy", soLuongNguoiMuaMoiCungKy.get());
//	    // tổng giá trị tất cả các lần bán của các biến thể này
//	    result.put("tongGiaTriTatCaHoaDonCungKy", tonGiaTriTatCaHoaDonCungKy.get());
//	    // tổng số hóa đơn bán trong kỳ này 
//	    float giaTriTrungBinhCungKy = 0f;
//	    int soHoaDonCungKy = tongSoHoaDonCungKy.size();
//	    if (soHoaDon > 0) {
//	        giaTriTrungBinhCungKy = tonGiaTriTatCaHoaDonCungKy.get() / soHoaDon;
//	    }
//	    result.put("giaTriTrungBinhTrenMoiHoaDonhCungKy", giaTriTrungBinhCungKy);
//	    result.put("soLuongBanRaCungKy", soLuongBanRaCụngKy);
//	    
	    // TÍNH GIÁ VỐN VÀ GỐC CỦA NHỮNG HÓA ĐƠN TRONG KỲ CÓ KHUYẾN MÃI
	    List<Map<String, Object>> giaVon = new ArrayList<>();
	    AtomicReference<Float> tongGiaVon = new AtomicReference<>(0f);
	    System.out.println("số lượng hóa đơn có trong đây: "+chiTietHoaDons.size());
	    chiTietHoaDons.forEach(d->{
	    	System.out.println("số tiền: "+d.getDonGia());
	    	System.out.println("tổng tiền: "+d.getTongTien());
	    });
	    danhSachSuDung2.forEach(d -> {
	        if (d != null && d.getBienThe() != null) {
	            Map<String, Object> bienThe = sanPhamService.getBienTheForThongKeFlashSale(
	                    d.getBienThe().getId(),bd, kt,
	                    chiTietHoaDons.stream()
	                            .filter(Objects::nonNull)
	                            .map(ChiTietHoaDon::getId)
	                            .collect(Collectors.toList())
	            );
	            if (bienThe != null && bienThe.get("tongGiaGoc") != null) {
	            	System.out.println("MAP TRẢ VỀ: "+bienThe);
	            	giaVon.add(bienThe);
	                tongGiaVon.updateAndGet(t -> t + (float) bienThe.get("tongGiaGoc"));
	            }
	        }
	    });
	   
	    result.put("tongGiaVon", tongGiaVon.get());
	    
	    
//	    List<Map<String, Object>> giaVon = new ArrayList<>();
	   
//	    // TÍNH GIÁ VỐN VÀ GỐC CỦA NHỮNG HÓA ĐƠN TRONG KỲ KO ÁP DỤNG KHUYẾN MÃI
//	    AtomicReference<Float> tongGiaVonCungKy = new AtomicReference<>(0f);
//	    AtomicReference<Float> tongGiaVon = new AtomicReference<>(0f);
//	    danhSachSuDung2.forEach(d -> {
//	        if (d != null && d.getBienThe() != null) {
//	            Map<String, Object> bienThe = sanPhamService.getBienTheForThongKeFlashSale(
//	                    d.getBienThe().getId(),bd, f.getThoiGianNgung(),
//	                    chiTietHoaDonKhongApDung.stream()
//	                            .filter(Objects::nonNull)
//	                            .map(ChiTietHoaDon::getId)
//	                            .collect(Collectors.toList())
//	            );
//	            if (bienThe != null && bienThe.get("tongGiaGoc") != null) {
//	            	 giaVon.add(bienThe);
//	            	tongGiaVonCungKy.updateAndGet(t -> t + (float) bienThe.get("tongGiaGoc"));
//	            }
//	        }
//	    });
//	    result.put("tongGiaVonCungKy", tongGiaVonCungKy.get());
	    

	    // Calculate access statistics per product
	    AtomicLong tongLuotTruyCap = new AtomicLong(0);
	    AtomicLong tongLuotTruyCapMoi = new AtomicLong(0);
	    List<Map<String, Object>> thonTinPhanLoai = danhSachSuDung2.stream()
	            .filter(d -> d != null && d.getBienThe() != null && d.getBienThe().getSanPham() != null)
	            .map(p -> p.getBienThe().getSanPham().getId())
	            .distinct()
	            .map(l -> {
	                com.example.e_commerce.model.SanPham ss = sanPhamService.getSanPhamById(l);
	                Map<String, Object> end = new HashMap<>();
	                if (ss != null) {
	                    end.put("ten", ss.getTen() != null ? ss.getTen() : "");
	                    end.put("anhBia", ss.getAnhBia() != null ? ss.getAnhBia() : "");
	                    Map<String, Object> m2 = sanPhamService.getLuuLuongTruyCap(
	                            ss.getId(), bd, kt, 2000, 0
	                    );
	                    if (m2 != null) {
	                        AtomicInteger tonLuongTruyCapMoi = (AtomicInteger) m2.get("tonLuongTruyCapMoi");
	                        AtomicInteger tongLuotTruyCapCu = (AtomicInteger) m2.get("tongLuongTruyCapCu");
	                        long tongTruyCap = (tonLuongTruyCapMoi != null ? tonLuongTruyCapMoi.get() : 0) + 
	                                           (tongLuotTruyCapCu != null ? tongLuotTruyCapCu.get() : 0);
	                        tongLuotTruyCap.addAndGet(tongTruyCap);
	                        tongLuotTruyCapMoi.addAndGet(tonLuongTruyCapMoi != null ? tonLuongTruyCapMoi.get() : 0);
	                        end.put("luotTruyCap", m2);
	                        end.put("tonLuongTruyCapMoi", tonLuongTruyCapMoi != null ? tonLuongTruyCapMoi.get() : 0);
	                        end.put("tongLuotTruyCapCu", tongLuotTruyCapCu != null ? tongLuotTruyCapCu.get() : 0);
	                    } else {
	                        end.put("luotTruyCap", new HashMap<String, Object>());
	                        end.put("tonLuongTruyCapMoi", 0);
	                        end.put("tongLuotTruyCapCu", 0);
	                    }
	                    end.put("thongTinMuaHang", danhSachSuDung2.stream()
	                            .filter(d -> d != null && d.getBienThe() != null && 
	                                    d.getBienThe().getSanPham() != null && 
	                                    d.getBienThe().getSanPham().getId() == ss.getId())
	                            .map(d -> sanPhamService.getGiaOfChiTietHoaDon(
	                                    d.getBienThe().getId(), bd, kt,
	                                    chiTietHoaDons.stream()
	                                            .filter(Objects::nonNull)
	                                            .map(ChiTietHoaDon::getId)
	                                            .collect(Collectors.toList())
	                            ,Arrays.asList(1,2,3,6,12)))
	                            .collect(Collectors.toList()));
	                    end.put("thongTinBienThe", giaVon.stream()
	                            .filter(k -> k != null && k.get("idSanPham") != null && 
	                                    ((long) k.get("idSanPham") == ss.getId()))
	                            .collect(Collectors.toList()));
	                }
	                return end;
	            })
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());
	    result.put("thonTinPhanLoai", thonTinPhanLoai);

	    // Calculate start and end dates
	    

	    result.put("tongSoLuongBanRa", chiTietHoaDons.stream()
	            .filter(Objects::nonNull)
	            .mapToInt(ChiTietHoaDon::getSoLuong)
	            .sum());
	    result.put("ngayBatDau", bd);
	    result.put("ngayKetThuc", kt);
	    result.put("tongLuotTruyCap", tongLuotTruyCap.get());
	    result.put("tongLuotTruyCapMoi", tongLuotTruyCapMoi.get());

	    return result;
	}
	
	
	public Map<String, Object> getThongTinThongKe(long fl) {
	    List<Integer> vv = Arrays.asList(1, 2, 3, 6, 12,13);
	    FlashSale f = getById(fl);
	    if (f == null) return new HashMap<>();

	    List<Integer> danhSachSuDung = f.getBienTheFlashSale() != null
	        ? f.getBienTheFlashSale().stream()
	            .map(d -> d.getBienThe() != null ? d.getBienThe().getId() : null)
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList())
	        : new ArrayList<>();

	    List<ChiTietHoaDon> danhSachCacHoaDon = chiTietHoaDonService.getHoaDonOfSanPham(
	        danhSachSuDung, f.getThoiGianApDung(), f.getThoiGianNgung(), vv
	    );

	    List<ChiTietHoaDon> chiTietHoaDons = danhSachCacHoaDon.stream()
	        .filter(d -> d.getDanhSachKhuyenMai().stream()
	            .anyMatch(m -> m.getKhuyenMai().getId() == f.getId()))
	        .collect(Collectors.toList());

	    int tongSoLuongFlashSale = f.getBienTheFlashSale().stream()
	        .mapToInt(BienTheFlashSale::getSoLuongGioiHan)
	        .sum();

	    int tongDaBan = chiTietHoaDons.stream()
	        .mapToInt(ChiTietHoaDon::getSoLuong)
	        .sum();

	    LocalDateTime bdNow = f.getThoiGianApDung();
	    LocalDateTime ktNow;

	    if (tongDaBan >= tongSoLuongFlashSale) {
	        Optional<LocalDateTime> maxThoiGian = chiTietHoaDons.stream()
	            .map(d -> d.getHoaDon().getNgayLap())
	            .max(LocalDateTime::compareTo);
	        ktNow = maxThoiGian.orElse(f.getThoiGianNgung());
	    } else {
	        ktNow = f.getThoiGianNgung();
	    }

	    // Tính khoảng thời gian và giai đoạn trước đó để so sánh
	    Duration duration = Duration.between(bdNow, ktNow);
	    LocalDateTime ktBefor = bdNow.minusSeconds(1);
	    LocalDateTime bdBefor = ktBefor.minus(duration);
	    System.out.println("thời gian bắt đầu : "+bdNow);
	    System.out.println("thời gian kết thúc : "+ktNow);

	    // Tạo map kết quả
	    Map<String, Object> map = new HashMap<>();
	    map.put("dataNew", getThongKeFlashSaleNew(f.getId(), f, bdNow, ktNow));
	    map.put("dataOld", getThongKeFlashSaleNew(f.getId(), f, bdBefor, ktBefor));
	    map.put("bdf", f.getThoiGianApDung());
	    map.put("ktf", f.getGioKetThuc());

	    boolean som = ktNow.isBefore(f.getThoiGianNgung());
	    map.put("som", som);

	    // Nếu kết thúc sớm thì lấy thời điểm lớn hơn giữa ktNow và ktBefor
	    LocalDateTime thoiGianKetThucSom = null;
	    if (som) {
	        thoiGianKetThucSom = ktNow.isAfter(ktBefor) ? ktNow : ktBefor;
	    }
	    map.put("thoiGianKetThucSom", thoiGianKetThucSom);

	    return map;
	}

}





	

