package com.example.e_commerce.service;

import java.awt.print.Pageable;
import java.beans.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudinary.api.exceptions.GeneralError;
import com.example.e_commerce.DTO.request.FilterOrderEmployee;
import com.example.e_commerce.DTO.request.GHNOrder;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.ApDungKhuyenMai;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.repository.HoaDonOnlineRepository;
import com.example.e_commerce.repository.HoaDonRepository;
import com.example.e_commerce.repository.HoaDonTaiQuayRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HoaDonSerVice {
	@Autowired
	private HoaDonRepository hoaDonRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	HoaDonOnlineRepository hoaDonOnlineRepository;
	
	@Autowired
	private ZaloPayService zaloPayService;
	
	@Autowired
	private GHNService ghnService;
	
	@Autowired
	TrangThaiService trangThaiService;
	
	@Autowired
	private TrangThaiHoaDonService trangThaiHoaDonService;
	
	@Autowired
	private ApDungKhuyenMaiService apDungKhuyenMaiService;
	
	@Autowired
	private DealService dealService;
	
	@Autowired
	private BienTheFlashSaleService bienTheFlashSaleService;
	
	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;
	
	@Autowired
	private BienTheSerVice bienTheSerVice;
	
	@Autowired
	private MomoService momoService;
	public HoaDon getById(long id) {
		return hoaDonRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy hóa đơn cần tìm"));
	}
	public Page<Map<String, Object>> getHoaDonOfTrangThai(long tt, FilterOrderEmployee filterOrderEmployee, int trang) {
		List<Long> a= new ArrayList<Long>();
		if(tt==1000) {
			a.add(12l);
			a.add(13l);
		}
		else {
			a.add(tt);
		}
//		if(filterOrderEmployee.getNgayLap()==null) {
//			filterOrderEmployee.setNgayLap(LocalDate.now());
//		}
		org.springframework.data.domain.Pageable p= PageRequest.of(trang, 8);
		LocalDateTime ngayKT=null;
			 
			if (filterOrderEmployee.getNgayLap() != null) {
			    ngayKT = filterOrderEmployee.getNgayLap().plusDays(1).atStartOfDay();
			}

	    Page<HoaDonOnline> hoaDons = hoaDonRepository.getHoaDonByStatusV2(
	    		jwtService.getIdUser(), 
	    		filterOrderEmployee.getNgayLap()==null? null:(filterOrderEmployee.getNgayLap().atStartOfDay()),ngayKT,
	    		a,
	    		filterOrderEmployee.getMaHoaDon(),
	    		p);
		
	    return hoaDons.map(d -> {
	        Map<String, Object> i = new HashMap<>();
	        i.put("id", d.getId());
	        i.put("ngayLap", d.getNgayLap());
	        i.put("tongTien", (int) (Math.ceil(d.getTongTien() / 1000.0) * 1000));
	        i.put("tongSoLuongHang", d.getChiTietHoaDons().stream().mapToInt(f -> f.getSoLuong()).sum());
	        i.put("tongSoMatHang", d.getChiTietHoaDons().size());
	        i.put("isThanhToan", d.isDaThanhTona());
	        i.put("isHoanHang", d.isDaHoanHang());
	        i.put("daHoanTien", !d.isDaThanhTona() || d.isDaHoanHang());
	        i.put("isChange", ischange(d.getId()));
	        if (d.getTrangThai().getId() != 2 && d.getTrangThai().getId() != 3) {
	            i.put("thoiGianDuKienGiao", d.getThoiGianDuKienGiao());
	            i.put("tongPhiGiaoHang", d.getTongPhiGHN());
	        }

	        AtomicReference<Float> tongTienThuChiTiet = new AtomicReference<>(0f);
	        i.put("danhSachMatHang", d.getChiTietHoaDons().stream().map(m -> {
	            Map<String, Object> ma = new HashMap<>();
	            ma.put("tenSanPham", m.getBienThe().getSanPham().getTen());
	            ma.put("soLuong", m.getSoLuong());
	            ma.put("donGia", m.getDonGia());
	            ma.put("tenPhanLoai", m.getBienThe().getTen());
	            tongTienThuChiTiet.updateAndGet(v -> v + m.getTongTien());
	            return ma;
	        }).collect(Collectors.toList()));

	        i.put("tongGiamGia", (int) Math.ceil(d.getTongTien() - tongTienThuChiTiet.get()));
	        return i;
	    });
	}
	
	
	
	
	@Transactional
	public void Update(HoaDon hoaDon) {
		hoaDonRepository.save(hoaDon);
	}
	
	@Transactional
	public void huyDonTaiQuay(long id) {
		hoaDonRepository.deleteByHoaDonIdAndPhieuChuaXacNhan(id);
		HoaDon hoaDon= hoaDonRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy hoá đơn",HttpStatus.BAD_REQUEST));
		hoaDon.getChiTietHoaDons().forEach(d->{
			BienThe b = d.getBienThe();
			b.setSoLuongKho(b.getSoLuongKho() + d.getSoLuong());
			bienTheSerVice.save(b);
			List<ApDungKhuyenMai> a= d.getDanhSachKhuyenMai();
			a.forEach(m -> {
				if (m.isDealPhu() == false) {
//					apDungKhuyenMaiService.delete(m.getId());
				} else {
					if (m.getKhuyenMai() instanceof Deal) {
						Deal dd = (Deal) m.getKhuyenMai();
						dd.setSoLuongDaDung(dd.getSoLuongDaDung() - m.getSoLuongApDung());
						dealService.save(dd);
					} else {
						((FlashSale) m.getKhuyenMai()).getBienTheFlashSale().forEach(l -> {
							if (l.getBienThe().getId() == d.getBienThe().getId()) {
								BienTheFlashSale bf = l;
								bf.setSoLuongDaDung(l.getSoLuongDaDung() - m.getSoLuongApDung());
								bienTheFlashSaleService.save(bf);
							}
						});
					}
				}
			});
		});
	}
	
	@Transactional
	public void HuyDon2(long id) {
		HoaDon hoaDon= hoaDonRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy hoá đơn",HttpStatus.BAD_REQUEST));
		huyDonTaiQuay(id);
		TrangThai trangThai = trangThaiService.getById(9);
		
		trangThaiHoaDonService.Save(hoaDon, trangThai,"Quý khách đã không thanh toán hóa đơn trong thời gian yêu cầu");
		hoaDon.setTrangThai(trangThai);
		hoaDonRepository.save(hoaDon);

	}
	@Transactional
	public void HuyDon3(long id) {
		HoaDon hoaDon= hoaDonRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy hoá đơn",HttpStatus.BAD_REQUEST));
		huyDonTaiQuay(id);
		TrangThai trangThai = trangThaiService.getById(9);
		trangThaiHoaDonService.Save(hoaDon, trangThai," ");
		hoaDon.setTrangThai(trangThai);
		hoaDonRepository.save(hoaDon);

	}
//	@Transactional
//	public void listenMomo(long id, long transId, int code) throws SchedulerException {
//		OrderClassSchedule o= ServiceLocator.getBean(OrderClassSchedule.class);
//		HoaDonTaiQuayRepository hoaDonTaiQuayRepository=ServiceLocator.getBean(HoaDonTaiQuayRepository.class);
//		HoaDonOnlineRepository hoaDonOnlineRepository=ServiceLocator.getBean(HoaDonOnlineRepository.class);
//		HoaDon hh = hoaDonRepository.findById(id)
//				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
//		System.out.println("huhu");
//		if(hh instanceof HoaDonTaiQuay){
//			System.out.println("huhu");
//			HoaDonTaiQuay h=(HoaDonTaiQuay)hh;
//			if (code == 0) {
//				if (h.getHinhThucThanhToan().getId() == 1 && h.getTrangThai().getId()==2) {
//					TrangThai trangThai = trangThaiService.getById(1);
//					h.setTransId(transId);
//					h.setTrangThai(trangThai);
//					h.setDaThanhTona(true);
//					
//					hoaDonTaiQuayRepository.save(h);
//					trangThaiHoaDonService.Save(h, trangThai,"");
//				}
//			}
//		}
//		else {
//			HoaDonOnline h=(HoaDonOnline) hh;
//			if (code == 0 && h.getTrangThai().getId()==2) {
//					o.deleteJobById(h.getId());
//					h.setDaThanhTona(true);
//					h.setTransId(transId);
//					// thực hiện đẩy cái đơn lên GHN 
//					HoaDonOnlineService hoaDonOnlineService= ServiceLocator.getBean(HoaDonOnlineService.class);
//					
//					GHNOrder ghnOrder=hoaDonOnlineService.getGHNorderFromCartItemForCreateOrder(h);
//					Map<String, Object> re= ghnService.CreateGHNOrder(ghnOrder);
//					
//			        if(re !=null) {
//			        	String maDonHang = (String) re.get("maDonHang");
//				        String thoiGianGiaoHangStr = (String) re.get("thoiGianGiaoHang");
//				        Number tongTienGiao = (Number) re.get("tongTienGiao");
//				        Number tienBaoHiem = (Number) re.get("tienBaoHiem");
//				        System.out.println("ĐI VÔ ĐÂY1");
//				        LocalDateTime thoiGianGiaoHang = null;
//				        if (thoiGianGiaoHangStr != null) {
//				            thoiGianGiaoHang = LocalDateTime.parse(thoiGianGiaoHangStr, DateTimeFormatter.ISO_DATE_TIME);
//				        }
//
//				        h.setGhnCode(maDonHang);
//				        h.setThoiGianDuKienGiao(thoiGianGiaoHang);
//				        h.setTongPhiGHN(tongTienGiao != null ? tongTienGiao.intValue() : 0);
//				        h.setPhiBaoHiemGHN(tienBaoHiem != null ? tienBaoHiem.intValue() : 0);
//				        h.setDaLenDonGHN(true);
//				        TrangThai t= trangThaiService.getById(13);
//				        trangThaiHoaDonService.Save(h, t,"");
//				        h.setTrangThai(t);
//						hoaDonOnlineRepository.save(h);
//			        }
//			        else {
//			        	System.out.println("ĐI VÔ ĐÂY");
//			        	h.setDaLenDonGHN(false);
//						hoaDonOnlineRepository.save(h);
//			        }
//			}
//		}
//	}
	
	public void checkThanhToan(HoaDon hoaDon) {
		try {
			langNgheZaloPay(hoaDon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void checkThanhToan(HoaDon hoaDon) {
//		int a= hoadon
//	}
//	
	
	@Transactional
	public void langNgheZaloPay(HoaDon hh) throws SchedulerException {
		OrderClassSchedule o= ServiceLocator.getBean(OrderClassSchedule.class);
		int code=3;
		Long zpTransId=0l;
		Map<String, Object> response = null;
		try {
			response = zaloPayService.queryOrder(hh.getTransId());
		    code = (Integer) response.get("return_code");
		    zpTransId = response.containsKey("zp_trans_id") ? (Long) response.get("zp_trans_id") : null;
		} catch (Exception e) {
			
		}
		System.out.println(response);
		HoaDonTaiQUayService hoaDonTaiQUayService=ServiceLocator.getBean(HoaDonTaiQUayService.class);
		if(hh instanceof HoaDonTaiQuay){
			HoaDonTaiQuay h=(HoaDonTaiQuay)hh;
			if (code == 1) {
				if (h.getHinhThucThanhToan().getId() == 1 && h.getTrangThai().getId()==2) {
					TrangThai trangThai = trangThaiService.getById(1);
					h.setTrangThai(trangThai);
					h.setDaThanhTona(true);
					h.setHD_zpTrans_Id(zpTransId);
					hoaDonRepository.save(h);
					trangThaiHoaDonService.Save(h, trangThai,"Đã thanh toán đơn hàng ");
				}
			}
			else if( code==2) {
				hoaDonTaiQUayService.HuyDon(hh.getId());
			}
		}
		else {
			HoaDonOnline h=(HoaDonOnline) hh;
			if (code == 1) {
					o.deleteJobById(h.getId());
					h.setDaThanhTona(true);
					// thực hiện đẩy cái đơn lên GHN 
					HoaDonOnlineService hoaDonOnlineService= ServiceLocator.getBean(HoaDonOnlineService.class);
					
					GHNOrder ghnOrder=hoaDonOnlineService.getGHNorderFromCartItemForCreateOrder(h);
					Map<String, Object> re= ghnService.CreateGHNOrder(ghnOrder);
					
			        if(re !=null) {
			        	String maDonHang = (String) re.get("maDonHang");
				        String thoiGianGiaoHangStr = (String) re.get("thoiGianGiaoHang");
				        Number tongTienGiao = (Number) re.get("tongTienGiao");
				        Number tienBaoHiem = (Number) re.get("tienBaoHiem");
//				        Number khoiLuong = (Number) re.get("khoiLuong");
				        
				        LocalDateTime thoiGianGiaoHang = null;
				        if (thoiGianGiaoHangStr != null) {
				            thoiGianGiaoHang = LocalDateTime.parse(thoiGianGiaoHangStr, DateTimeFormatter.ISO_DATE_TIME);
				        }

				        h.setGhnCode(maDonHang);
				        h.setThoiGianDuKienGiao(thoiGianGiaoHang);
				        h.setTongPhiGHN(tongTienGiao != null ? tongTienGiao.intValue() : 0);
				        h.setPhiBaoHiemGHN(tienBaoHiem != null ? tienBaoHiem.intValue() : 0);
				        h.setTongKhoiLuong(ghnOrder.getWeight());
				        
				        h.setDaLenDonGHN(true);
				        h.setHD_zpTrans_Id(zpTransId);
				        TrangThai t= trangThaiService.getById(13);
				        trangThaiHoaDonService.Save(h, t,"");
				        h.setTrangThai(t);
						hoaDonRepository.save(h);
			        }
			        else {
			        	h.setDaLenDonGHN(false);
						hoaDonRepository.save(h);
			        }
			}
		}
	}
	public boolean ischange(long id) {
		return hoaDonRepository.kiemTraCoDangDuocXoaKhong(id)==0;
	}
}