package com.example.e_commerce.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.mapper.FlashSaleMapper;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.KhuyenMai;
import com.example.e_commerce.model.KhuyenMaiTangKem;
import com.example.e_commerce.model.SanPhamChinh;
import com.example.e_commerce.model.SanPhamPhu;
import com.example.e_commerce.repository.BienTheRepository;
import com.example.e_commerce.repository.DealRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DealService {
	@Autowired
	private DealRepository dealRepository;

	@Autowired
	private BienTheSerVice bienTheSerVice;

	@Autowired
	private BienTheDealChinhService bienTheDealChinhService;

	@Autowired
	private BienTheDealPhuService bienTheDealPhuService;

	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;
	@Transactional
	public void save(Deal deal) {
		if (!deal.getThoiGianNgung().isAfter(deal.getThoiGianApDung())) {
			throw new GeneralException("Thời gian áp dụng deal phải lớn hơn thời gian kết thúc",
					HttpStatus.BAD_REQUEST);
		}
		if (deal.getSoLuongGioiHan() <= 0) {
			throw new GeneralException("Vui lòng cung cấp số lượng deal lớn hơn 0", HttpStatus.BAD_REQUEST);
		}
		dealRepository.save(deal);
	}

	public void CheckDuplicateDealOfBienThe(Deal deal, int idBienThe, String tenBienThe) {
		List<Deal> khuyenMai = dealRepository.getKhuyenMaiDealChinhOfBienThe(idBienThe);
		khuyenMai.addAll(dealRepository.getKhuyenMaiDealPhuOfBienThe(idBienThe));
		khuyenMai.stream().filter(d->d.getId()!=deal.getId()).forEach((d) -> {
			
			if (TimeValidator.isOverlap(deal.getThoiGianApDung(), deal.getThoiGianNgung(), d.getThoiGianApDung(),
					d.getThoiGianNgung())) {
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

				throw new GeneralException("Deal bị trùng thời gian với deal khác của biến thể: " + tenBienThe,
						HttpStatus.BAD_REQUEST);

			}

		});

	}
	public void CheckDuplicateDealOfBienTheV2(LocalDateTime bd, LocalDateTime kt, int idBienThe, String tenBienThe) {
	    List<Deal> khuyenMai = new ArrayList<>();
	    khuyenMai.addAll(dealRepository.getKhuyenMaiDealChinhOfBienThe(idBienThe));
	    khuyenMai.addAll(dealRepository.getKhuyenMaiDealPhuOfBienThe(idBienThe));
	    khuyenMai.forEach(d -> {
	        if (TimeValidator.isOverlap(d.getThoiGianApDung(), d.getThoiGianNgung(), bd, kt)) {
	            throw new GeneralException("Biến thể: " +tenBienThe+" đã được sử dụng trong chương trình deal đang áp dụng", HttpStatus.BAD_REQUEST);
	        }
	    });
	}

	public List<Deal> getDealChinhOfBienThe(int idBienThe) {
		return dealRepository.getKhuyenMaiDealChinhOfBienThe(idBienThe);
	}

	public List<Deal> getDealPhuOfBienThe(int idBienThe) {
		return dealRepository.getKhuyenMaiDealPhuOfBienThe(idBienThe);
	}

	@Transactional
	public void save(FlashSaleDTO dealDto) {
		FlashSaleService flashSaleService=ServiceLocator.getBean(FlashSaleService.class);
		Deal deal = FlashSaleMapper.getDeal(dealDto);
		List<BienTheDealChinh> bienTheDealChinh = FlashSaleMapper.getDealChinh(dealDto);
		List<BienTheDealPhu> bienTheDealPhu = FlashSaleMapper.getDealPhu(dealDto);
		save(deal);
		if(bienTheDealChinh==null || bienTheDealChinh.size()==0) {
			throw new GeneralException("Bạn chưa cung cấp sản phẩm chính nào",HttpStatus.BAD_REQUEST);
		}
		if(bienTheDealPhu==null || bienTheDealPhu.size()==0) {
			throw new GeneralException("Bạn chưa cung cấp sản phẩm phụ nào",HttpStatus.BAD_REQUEST);
		}
		// check trùng thời gian
		bienTheDealChinh.forEach(dd -> {
			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			dd.setBienThe(bienThe);
			CheckDuplicateDealOfBienThe(deal, bienThe.getId(), bienThe.getTen());
		});
		bienTheDealPhu.forEach(dd -> {
			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			dd.setBienThe(bienThe);
			CheckDuplicateDealOfBienThe(deal, bienThe.getId(), bienThe.getTen());
			flashSaleService.CheckDuplicateDealOfBienTheV2
			(dealDto.getNgayBatDau(), 
					dealDto.getNgayKetThuc(),
					bienThe.getId(),
					bienThe.getTen());
			
		});
		// save từng cái
		
		bienTheDealChinh.forEach(data -> {
			data.setDeal(deal);
			bienTheDealChinhService.save(data);
		});
		bienTheDealPhu.forEach(data -> {
			data.setDeal(deal);
			bienTheDealPhuService.save(data);
		});
	}

	public Deal getById(long id) {
		return dealRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Deal không tồn tại"));
	}

	public FlashSaleDTO getDealToClient(long idDeal) {
		return FlashSaleMapper.mapDealFromDealToDto(getById(idDeal));
	}

	public void update(FlashSaleDTO dealDto, long id) {
		Deal deal = getById(id);
		deal.setTenChuongTrinh(dealDto.getTenChuongTrinh());
		List<BienTheDealChinh> bienTheDealChinh = FlashSaleMapper.getDealChinhForupdate(dealDto);
		if(dealDto.getSoLuongGioiHan()<deal.getSoLuongDaDung()) {
			throw new GeneralException("Số lượng khuyến mãi không thể nhỏ hơn số lượng đa dùng",HttpStatus.BAD_REQUEST);
		}
		System.out.println(deal.getThoiGianApDung().equals(dealDto.getNgayBatDau())==false);
		if (deal.getThoiGianApDung().isBefore(LocalDateTime.now()) && 
			    deal.getThoiGianApDung().equals(dealDto.getNgayBatDau())==false) {
			    
			        throw new GeneralException(
			            "Deal đã bắt đầu, bạn không thể chỉnh sửa thời gian bắt đầu",
			            HttpStatus.BAD_REQUEST);
			       
			}
		
		if (dealDto.getNgayBatDau().isBefore(LocalDateTime.now())&& 
			    deal.getThoiGianApDung().equals(dealDto.getNgayBatDau())==false) {
		    throw new GeneralException("Không thể chỉnh thời gian kết thúc về trước ngày hiện tại", HttpStatus.BAD_REQUEST);
		}
		if (dealDto.getNgayKetThuc().isBefore(LocalDateTime.now())&& 
			    deal.getThoiGianNgung().equals(dealDto.getNgayKetThuc())==false) {
		    throw new GeneralException("Không thể chỉnh thời gian kết thúc về trước ngày hiện tại", HttpStatus.BAD_REQUEST);
		}

		if (dealDto.getNgayKetThuc().isBefore(dealDto.getNgayBatDau())) {
		    throw new GeneralException("Ngày kết thúc không được nhỏ hơn ngày bắt đầu", HttpStatus.BAD_REQUEST);
		}

		
		
		deal.setSoLuongGioiHan(dealDto.getSoLuongGioiHan());

		if (!deal.getThoiGianApDung().equals(dealDto.getNgayBatDau())) {
		    if (deal.getSoLuongDaDung() != 0) {
		        throw new GeneralException("Không thể chỉnh thời gian bắt đầu khi đã có lượt sử dụng", HttpStatus.BAD_REQUEST);
		    }
		    if (dealDto.getNgayBatDau().isBefore(LocalDateTime.now())) {
			    throw new GeneralException("Không thể chỉnh thời gian bắt đầu về trước ngày hiện tại", HttpStatus.BAD_REQUEST);
			}
		    deal.setThoiGianApDung(dealDto.getNgayBatDau());
		}


		deal.setThoiGianNgung(dealDto.getNgayKetThuc());
		save(deal);
		
		List<BienTheDealPhu> bienTheDealPhu = FlashSaleMapper.getDealPhuforupdate(dealDto);
		FlashSaleService flashSaleService= ServiceLocator.getBean(FlashSaleService.class);
		List<BienTheDealChinh> bienTheDealChinhs = deal.getBienTheDealChinh();
		List<BienTheDealPhu> bienTheDealPhus = deal.getBienTheDealPhu();
		List<Integer> idchinh = bienTheDealChinhs.stream().map(data -> {
			return data.getBienThe().getId();
		}).collect(Collectors.toList());
		List<Integer> idphu = bienTheDealPhus.stream().map(data -> {
			return data.getBienThe().getId();
		}).collect(Collectors.toList());
		// sử lý deal chính mới
		bienTheDealChinh.stream().filter(d->{
			if(d.getSoLuongTu()>0) {
				return  true;
			}
			else {
				return false;
			}
		}).filter(data -> {
			return !idchinh.contains(data.getId().getBtId());
		}).forEach(dd -> {
			
			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			CheckDuplicateDealOfBienThe(deal,dd.getId().getBtId(), bienThe.getTen());
			dd.setBienThe(bienThe);
			CheckDuplicateDealOfBienThe(deal, bienThe.getId(), bienThe.getTen());
			
			bienTheDealChinhService.save(dd);
		});
		// sử lý deal phụ mới
		bienTheDealPhu.stream().filter(d->{
			if(d.getToiDaTrenDonVi()>0||d.getGiaTriGiam()>0) {
				return  true;
			}
			else {
				return false;
			}
		}).filter(data -> {
			return !idphu.contains(data.getId().getBtId());
		}). forEach(dd -> {

			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			CheckDuplicateDealOfBienThe(deal,dd.getId().getBtId(), bienThe.getTen());
			dd.setBienThe(bienThe);
			CheckDuplicateDealOfBienThe(deal, bienThe.getId(), bienThe.getTen());
			flashSaleService.CheckDuplicateDealOfBienTheV2
			(dealDto.getNgayBatDau(), 
					dealDto.getNgayKetThuc(),
					bienThe.getId(),
					bienThe.getTen());
			dd.setDeal(deal);
			dd.setDeal(deal);
			bienTheDealPhuService.save(dd);
		});
		bienTheDealChinhs.forEach(data -> {
			
			Optional<BienTheDealChinh> optionalDd = bienTheDealChinh.stream()
					.filter(d -> d.getId().getBtId() == data.getBienThe().getId()).findFirst();
			optionalDd.ifPresent(dd -> {
				
				 if(deal.getSoLuongDaDung()<=0) {
					 if (dd.getSoLuongTu() <= 0) {
							
							data.setConSuDung(false);
							data.setSoLuongTu(0);
							bienTheDealChinhService.saveForUpdate(data);
						} else {
							if(dd.isConSuDung()) {
				        		CheckDuplicateDealOfBienThe(deal,data.getBienThe().getId(), data.getBienThe().getTen());
				        	}
							data.setSoLuongTu(dd.getSoLuongTu());
							data.setConSuDung(dd.isConSuDung());
							
						}
				 }
				 else {
					 data.setConSuDung(dd.isConSuDung());
				 }
				 bienTheDealChinhService.saveForUpdate(data);
				
			});
		});

		// Xử lý biến thể deal phụ cũ
		bienTheDealPhus.forEach(data -> {

			
		    Optional<BienTheDealPhu> optionalDd = bienTheDealPhu.stream()
		            .filter(d -> d.getId().getBtId() == data.getBienThe().getId())
		            .findFirst();
		    
		    optionalDd.ifPresent(dd -> {
		    	
		       if(deal.getSoLuongDaDung()<=0) {
		    	   if (dd.getToiDaTrenDonVi() <= 0 || dd.getGiaTriGiam() <= 0) {
			            data.setToiDaTrenDonVi(dd.getToiDaTrenDonVi());
			            data.setConSuDung(false);
			            data.setGiaTriGiam(dd.getGiaTriGiam());
			        } else {
			        	if(dd.isConSuDung()) {
			        		CheckDuplicateDealOfBienThe(deal,data.getBienThe().getId(), data.getBienThe().getTen());
			        		flashSaleService.CheckDuplicateDealOfBienTheV2
			    			(dealDto.getNgayBatDau(), 
			    					dealDto.getNgayKetThuc(),
			    					data.getBienThe().getId(),
			    					data.getBienThe().getTen());
			    			dd.setDeal(deal);
			        	}
//			        	
			            data.setToiDaTrenDonVi(dd.getToiDaTrenDonVi());
			            data.setConSuDung(dd.isConSuDung());
			            data.setGiaTriGiam(dd.getGiaTriGiam());
			        }
		       }
		       else {
		    	   if(dd.isConSuDung()!=data.isConSuDung()) {
		    		   data.setConSuDung(dd.isConSuDung());
		    	   }
		       }
		        bienTheDealPhuService.saveForUpdate(data);
		    });
		});


	}
	public Page<Map<String, Object>> getByStatus(
			int trang, LocalDateTime bd, LocalDateTime kt, int status
			){
		Page<Deal> flashSales;
		
		if(status==2) {
		flashSales=	dealRepository.getDealBeforNow(bd, kt, PageRequest.of(trang, 5));
		}
		else if(status==1) {
			System.out.println("đi vào trong đây");
			flashSales=	dealRepository.getDealInNow(bd, kt, PageRequest.of(trang, 5));
			}
		else if (status==3) {
			flashSales=	dealRepository.getDealAfterNow(bd, kt, PageRequest.of(trang, 5));
			}
		else{
			flashSales=	dealRepository.findAll(PageRequest.of(trang, 5));
			}
		return flashSales.map(data -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("thoiGianApDung", data.getThoiGianApDung());
	        map.put("thoiGianNgung", data.getThoiGianNgung());
	        map.put("conSuDung", data.isConSuDung());
	        map.put("id", data.getId());

	        long soLuongSanPhamChinh = data.getBienTheDealChinh().stream()
	            .map(dat -> dat.getBienThe().getSanPham().getId())
	            .distinct()
	            .count();
	        long soLuongSanPhamPhu = data.getBienTheDealPhu().stream()
		            .map(dat -> dat.getBienThe().getSanPham().getId())
		            .distinct()
		            .count();
	        map.put("soLuongDaDung", data.getSoLuongDaDung());
	        map.put("tongLuotToiDa", data.getSoLuongGioiHan());
	        map.put("sanPhamChinh", soLuongSanPhamChinh);
	        map.put("sanPhamPhu", soLuongSanPhamPhu);

	        return map;
	    });
		
	}
	public void setActiveBonus(long id,boolean active) {
		Deal f=getById(id);
		if (f.getThoiGianNgung().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể điều chỉnh do khuyến mãi đã hết hạn",HttpStatus.BAD_REQUEST);
		}
		FlashSaleService flashSaleService= ServiceLocator.getBean(FlashSaleService.class);
		if(active==true) {
			List<BienTheDealChinh> sanPhamChinhs=f.getBienTheDealChinh();
			List<BienTheDealPhu> sanPhamPhus=f.getBienTheDealPhu();
			
			sanPhamPhus.forEach(d->{
				CheckDuplicateDealOfBienThe(f, d.getBienThe().getId(),d.getBienThe().getTen());
				flashSaleService.CheckDuplicateDealOfBienTheV2
    			(f.getThoiGianApDung(), 
    					f.getThoiGianNgung(),
    					d.getBienThe().getId(),
    					d.getBienThe().getTen());
			});
			sanPhamChinhs.forEach(d->{
				CheckDuplicateDealOfBienThe(f, d.getBienThe().getId(),d.getBienThe().getTen());
			});
		}
		f.setConSuDung(active);
		save(f);
	}

	public Map<String, Object> thongKeDeal(long idDeal) {
	    Deal f = getById(idDeal);
	    List<Integer> vv = Arrays.asList(1, 2, 3, 6, 12,13);
	    SanPhamService sanPhamService = ServiceLocator.getBean(SanPhamService.class);

	    List<Integer> danhSachIdChinh = f.getBienTheDealChinh().stream()
	            .map(d -> d.getBienThe().getId()).collect(Collectors.toList());
	    List<Integer> danhSachIdPhu = f.getBienTheDealPhu().stream()
	            .map(d -> d.getBienThe().getId()).collect(Collectors.toList());
	    
	    List<Integer> danhSachSuDung = new ArrayList<>();
	    danhSachSuDung.addAll(danhSachIdPhu);
	    danhSachSuDung.addAll(danhSachIdChinh);
	    
	    
	    List<BienThe> bb= danhSachSuDung.stream().map(d->{
	    	return bienTheSerVice.getById(d);
	    }).collect(Collectors.toList());

	    List<ChiTietHoaDon> allChiTietHoaDon = chiTietHoaDonService.getHoaDonOfSanPham(
	            danhSachSuDung, f.getThoiGianApDung(), f.getThoiGianNgung(), vv
	    );

	    List<ChiTietHoaDon> chiTietHoaDons = allChiTietHoaDon.stream()
	            .filter(d -> d.getDanhSachKhuyenMai().stream()
	                    .anyMatch(m -> m.isDealPhu() && m.getSoLuongApDung() > 0))
	            .collect(Collectors.toList());
	   System.out.println("số lượng chi tiết hóa đơn nhận được là: "+chiTietHoaDons.size());

	    Map<String, Object> result = new HashMap<>();
	    LocalDateTime bdNow, ktNow, bdBefore, ktBefore;

	    if (f.getSoLuongDaDung() >= f.getSoLuongGioiHan() && !chiTietHoaDons.isEmpty()) {
	        bdNow = f.getThoiGianApDung();
	        ktNow = chiTietHoaDons.stream()
	                .map(d -> d.getHoaDon().getNgayLap())
	                .max(LocalDateTime::compareTo)
	                .orElse(f.getThoiGianNgung());
	    } else {
	    	   LocalDateTime now = LocalDateTime.now();
	           bdNow = f.getThoiGianApDung();
	           ktNow = f.getThoiGianNgung().isBefore(now) ? f.getThoiGianNgung() : now;
	    }

	    // Duration để tính khoảng thời gian
	    Duration duration = Duration.between(bdNow, ktNow);
	    ktBefore = bdNow.minusSeconds(1);
	    bdBefore = ktBefore.minus(duration);
	    
	    // tính lại danh sách hóa đơn 
	    allChiTietHoaDon = chiTietHoaDonService.getHoaDonOfSanPham(
	            danhSachSuDung, bdNow,ktNow, vv
	    );
	    
	    List<ChiTietHoaDon> chiTietHoaDonConLai = allChiTietHoaDon.stream()
	    	    .filter(d -> !chiTietHoaDons.contains(d))
	    	    .collect(Collectors.toList());
	    System.out.println("SỐ LƯƠNGJ CHI TIẾT NOW: "+allChiTietHoaDon.size());
	    System.out.println("SỐ LƯƠNGJ CHI TIẾT NOW: "+chiTietHoaDons.size());
	    System.out.println("SỐ LƯƠNGJ CHI TIẾT NOW: "+chiTietHoaDonConLai.size());

	    List<ChiTietHoaDon> danhSachTruocDo= chiTietHoaDonService.getHoaDonOfSanPham(danhSachSuDung, bdBefore,ktBefore, vv);
		
		//LẤY CÁC HÓA ĐƠN TRONG BA CHI TIẾT HÓA ĐƠN
		List<HoaDon>hoadonApDung= chiTietHoaDons.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList());
		List<HoaDon>hoaDonKhongapDung= chiTietHoaDonConLai.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList());
		List<HoaDon>hoaDonTruocDo= danhSachTruocDo.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList());
		//TÍNH CÁC THÔNG SỐ VỚI HÓA ĐƠN CÓ ÁP DỤNG
		long soLuongSanPhamChinhIn=hoadonApDung.stream().flatMap(d->d.getChiTietHoaDons().stream())
				.filter(d->danhSachIdChinh.contains(d.getBienThe().getId()))
				.map(d->d.getSoLuong()).count();
		long soLuongSanPhamPhuIn=hoadonApDung.stream().flatMap(d->d.getChiTietHoaDons().stream())
				.filter(d->danhSachIdPhu.contains(d.getBienThe().getId()))
				.map(d->d.getSoLuong()).count();
		int sl1=hoadonApDung.size()==0 ?1 :hoadonApDung.size();
		result.put("trungBinhSanPhamChinhTrenHoaDonNOW", soLuongSanPhamChinhIn/ sl1);
		result.put("trungBinhSanPhamPhuTrenHoaDonNOW", soLuongSanPhamPhuIn/sl1);
		List<HoaDon> dsall= allChiTietHoaDon.stream().map(m->m.getHoaDon()).distinct().collect(Collectors.toList());
		List<HoaDon> hoaDonApDung= chiTietHoaDons.stream().map(d->d.getHoaDon()).distinct().collect(Collectors.toList());
		System.out.println("DANH SÁCH HÓA ĐƠN ALL: "+dsall.size());
		System.out.println("DANH SÁCH HÓA ĐƠN ALL: "+hoaDonApDung.size());
		result.put("soLuongHoaDonApDungNOW", hoadonApDung.size());
			// tính giá vốn và gốc của nó
		List<Map<String, Object>> giaVonNow = new ArrayList<>();
	    AtomicReference<Float> tongGiaVonNow = new AtomicReference<>(0f);
	    AtomicLong tongSoLuongBanNow = new AtomicLong();
	    AtomicLong tongSoLuotBanNow = new AtomicLong();
	  	AtomicReference<Float> tongDoanhSoNow =  new AtomicReference<>(0f);
		danhSachSuDung.forEach(d -> {
	            Map<String, Object> bienThe = sanPhamService.getBienTheForThongKeFlashSale(
	                    d,bdNow, ktNow,
	                    chiTietHoaDons.stream()
	                            .filter(Objects::nonNull)
	                            .map(ChiTietHoaDon::getId)
	                            .collect(Collectors.toList())
	            );
	            if (bienThe != null && bienThe.get("tongGiaGoc") != null) {
	            	giaVonNow.add(bienThe);
	            	tongSoLuotBanNow.addAndGet(((Number) bienThe.get("tongSoLuotBan")).longValue());
	            	tongSoLuongBanNow.addAndGet(((Number) bienThe.get("tongSoLuongBan")).longValue());
	            	tongDoanhSoNow.updateAndGet(t -> t + ((Number) bienThe.get("tongDoanhSo")).floatValue());
	            	tongGiaVonNow.updateAndGet(t -> t + ((Number) bienThe.get("tongGiaGoc")).floatValue());

	            }
	    });
	    result.put("tongGiaVonNow", tongGiaVonNow.get());
	    result.put("tongSoLuongBanNow",allChiTietHoaDon.stream().mapToLong(d->d.getSoLuong()).sum());
	  	result.put("tongSoLuotBanNow", tongSoLuotBanNow.get());
	  	result.put("tongDoanhSoNow", tongDoanhSoNow.get());
	    
	  //TÍNH CÁC THÔNG SỐ VỚI HÓA ĐƠN CÓ ÁP DỤNG
	  long soLuongSanPhamChinhInCungKy=hoaDonKhongapDung.stream().flatMap(d->d.getChiTietHoaDons().stream())
	  				.filter(d->danhSachIdChinh.contains(d.getBienThe().getId()))
	  				.map(d->d.getSoLuong()).count();
	  long soLuongSanPhamPhuInCungKy=hoaDonKhongapDung.stream().flatMap(d->d.getChiTietHoaDons().stream())
	  				.filter(d->danhSachIdPhu.contains(d.getBienThe().getId()))
	  				.map(d->d.getSoLuong()).count();
	  int sl1CungKy=hoaDonKhongapDung.size()==0 ?1 :hoaDonKhongapDung.size();
	  		result.put("trungBinhSanPhamChinhTrenHoaDonCungKy", soLuongSanPhamChinhInCungKy/ sl1CungKy);
	  		result.put("trungBinhSanPhamPhuTrenHoaDonCungKy", soLuongSanPhamPhuInCungKy/sl1CungKy);
	  		result.put("soLuongHoaDonApDungCungKy", dsall.size()-hoadonApDung.size());
	  			// tính giá vốn và gốc của nó
	  		
	  		
	  	    AtomicReference<Float> tongGiaVonCungKy = new AtomicReference<>(0f);
	  	    AtomicLong tongSoLuongBanCungKy = new AtomicLong();
	  	    AtomicLong tongSoLuotBanCungKy = new AtomicLong();
		  	AtomicReference<Float> tongDoanhSoCungKy =  new AtomicReference<>(0f);
	  		danhSachSuDung.forEach(d -> {
	  	            Map<String, Object> bienThe = sanPhamService.getBienTheForThongKeFlashSale(
	  	                    d,bdNow, ktNow,
	  	                    chiTietHoaDonConLai.stream()
	  	                            .filter(Objects::nonNull)
	  	                            .map(ChiTietHoaDon::getId)
	  	                            .collect(Collectors.toList())
	  	            );
	  	            if (bienThe != null && bienThe.get("tongGiaGoc") != null) {
	  	            	tongGiaVonCungKy.updateAndGet(t -> t + ((Number) bienThe.get("tongGiaGoc")).floatValue());
	  	            	tongDoanhSoCungKy.updateAndGet(t -> t + ((Number) bienThe.get("tongDoanhSo")).floatValue());
	  	            	tongSoLuotBanCungKy.addAndGet(((Number) bienThe.get("tongSoLuotBan")).longValue());
	  	            	tongSoLuongBanCungKy.addAndGet(((Number) bienThe.get("tongSoLuongBan")).longValue());

	  	            }
	  	    });
	  	    result.put("tongGiaVonCungKy", tongGiaVonCungKy.get());
	  	    result.put("tongSoLuongBanCungKy", tongSoLuongBanCungKy.get());
		  	result.put("tongSoLuotBanCungKy", tongSoLuotBanCungKy.get());
		  	result.put("tongDoanhSoCungKy", tongDoanhSoCungKy.get());
	    
	  	  //TÍNH CÁC THÔNG SỐ VỚI HÓA ĐƠN CÓ ÁP DỤNG
	  	  long soLuongSanPhamChinhInCungTruocDo=hoaDonTruocDo.stream().flatMap(d->d.getChiTietHoaDons().stream())
	  	  				.filter(d->danhSachIdChinh.contains(d.getBienThe().getId()))
	  	  				.map(d->d.getSoLuong()).count();
	  	  long soLuongSanPhamPhuInTruocDo=hoaDonTruocDo.stream().flatMap(d->d.getChiTietHoaDons().stream())
	  	  				.filter(d->danhSachIdPhu.contains(d.getBienThe().getId()))
	  	  				.map(d->d.getSoLuong()).count();
	  	  int sl1TruocDo=hoaDonTruocDo.size()==0 ?1 :hoaDonTruocDo.size();
	  	  		result.put("trungBinhSanPhamChinhTrenHoaDonTruocDo", soLuongSanPhamChinhInCungTruocDo/ sl1TruocDo);
	  	  		result.put("trungBinhSanPhamPhuTrenHoaDonTruocDo", soLuongSanPhamPhuInTruocDo/sl1TruocDo);
	  	  		result.put("soLuongHoaDonApDungTruocDo", hoaDonTruocDo.size());
	  	  			// tính giá vốn và gốc của nó
	  	  	    AtomicReference<Float> tongGiaVonTruocDo= new AtomicReference<>(0f);
	  	  	 AtomicLong tongSoLuongBanTruocDo = new AtomicLong();
	  	  	AtomicLong tongSoLuotBanTruocDo = new AtomicLong();
			  	AtomicReference<Float> tongDoanhSoTruocDo =  new AtomicReference<>(0f);
	  	  		danhSachSuDung.forEach(d -> {
	  	  	            Map<String, Object> bienThe = sanPhamService.getBienTheForThongKeFlashSale(
	  	  	                    d,bdBefore, ktBefore,
	  	  	                    danhSachTruocDo.stream()
	  	  	                            .filter(Objects::nonNull)
	  	  	                            .map(ChiTietHoaDon::getId)
	  	  	                            .collect(Collectors.toList())
	  	  	            );
	  	  	            if (bienThe != null && bienThe.get("tongGiaGoc") != null) {
	  	  	            tongGiaVonTruocDo.updateAndGet(t -> t + ((Number) bienThe.get("tongGiaGoc")).floatValue());
	  	  	        tongDoanhSoTruocDo.updateAndGet(t -> t + ((Number) bienThe.get("tongDoanhSo")).floatValue());
	  	  	        tongSoLuotBanTruocDo.addAndGet(((Number) bienThe.get("tongSoLuotBan")).longValue());
	  	  	        tongSoLuongBanTruocDo.addAndGet(((Number) bienThe.get("tongSoLuongBan")).longValue());

	  	  	            }
	  	  	    });
	  	  	    result.put("tongGiaVonTruocDo", tongGiaVonTruocDo.get());
	  	  	    result.put("tongSoLuongBanTruocDo", tongSoLuongBanTruocDo.get());
	  	  	    result.put("tongSoLuotBanTruocDo", tongSoLuotBanTruocDo.get());
	  	  	    result.put("tongDoanhSoTruocDo", tongDoanhSoTruocDo.get());
	  	  	List<Map<String, Object>> thonTinPhanLoai = bb.stream()
		            .map(p -> p.getSanPham().getId())
		            .distinct()
		            .map(l -> {
		                com.example.e_commerce.model.SanPham ss = sanPhamService.getSanPhamById(l);
		                Map<String, Object> end = new HashMap<>();
		                if (ss != null) {
		                    end.put("ten", ss.getTen() != null ? ss.getTen() : "");
		                    end.put("anhBia", ss.getAnhBia() != null ? ss.getAnhBia() : "");
		                    end.put("thongTinMuaHang", bb.stream()
		                            .filter(d -> d.getSanPham().getId()==l)
		                            .map(d -> sanPhamService.getGiaOfChiTietHoaDon(
		                                    d.getId(), bdNow, ktNow,
		                                    chiTietHoaDons.stream()
		                                            .filter(Objects::nonNull)
		                                            .map(ChiTietHoaDon::getId)
		                                            .collect(Collectors.toList())
		                            ,Arrays.asList(1,2,3,6,12)))
		                            .collect(Collectors.toList()));
		                    end.put("thongTinBienThe", giaVonNow.stream()
		                            .filter(k -> k != null && k.get("idSanPham") != null && 
		                                    ((long) k.get("idSanPham") == ss.getId()))
		                            .collect(Collectors.toList()));
		                }
		                return end;
		            })
		            .filter(Objects::nonNull)
		            .collect(Collectors.toList());
		    result.put("thonTinPhanLoai", thonTinPhanLoai);

		return result;
		
		
		
		
		
	}
	
//	public boolean kiemTraXemCoConDuocUpdateKhon(long iddeal, int idBienThe) {
//		ArrayList<Integer> idBienT= new ArrayList<Integer>();
//		idBienT.add(idBienThe);
//		List<ChiTietHoaDon>
//		
//	}
}
