package com.example.e_commerce.service;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.persistence.EntityNotFoundException;

import com.itextpdf.layout.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.CartItem;
import com.example.e_commerce.DTO.request.DTOCreateHoaDonTaiQuay;
import com.example.e_commerce.DTO.response.CartItemLast;
import com.example.e_commerce.DTO.response.ParentCartLast;
import com.example.e_commerce.DTO.response.ZaloPayOrder;
import com.example.e_commerce.DTO.response.ZaloPayResponseDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.ApDungKhuyenMai;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.HinhThucThanhToan;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.ThueVATSanPham;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.model.TrangThaiHoaDon;
import com.example.e_commerce.repository.HinhThucThanhToanRepository;

@Service
public class HoaDonTaiQUayService {
	
	@Value("${app_USER}")
    private String app_USER;
	@Autowired
	private HoaDonSerVice hoaDonSerVice;
	
	@Autowired
	private ZaloPayService zaloPayService;
	

	@Autowired
	private com.example.e_commerce.repository.HoaDonTaiQuayRepository hoaDonTaiQuayRepository;

	@Autowired
	private BienTheSerVice bienTheSerVice;

	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;

	@Autowired
	private TrangThaiService trangThaiService;
	@Autowired
	private BienTheDealChinhService bienTheDealChinhService;

	@Autowired
	private BienTheFlashSaleService bienTheFlashSaleService;

	@Autowired
	private BienTheDealPhuService bienTheDealPhuService;

	@Autowired
	private ThueVATSanPhamSerVice thueVATSanPhamSerVice;

	@Autowired
	private TrangThaiHoaDonService trangThaiHoaDonService;

	@Autowired
	private ApDungKhuyenMaiService apDungKhuyenMaiService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private DealService dealService;

	@Autowired
	private FlashSaleService flashSaleService;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private KhachHangService khachHangService;

	@Autowired
	private HoaDonOnlineService hoaDonOnlineService;

	@Autowired
	private MomoService momoService;

	@Autowired
	private HinhThucThanhToanRepository hinhThucThanhToanRepository;

	@Transactional
	public Map<String, Object> DatHangOnline(DTOCreateHoaDonTaiQuay hoaDonTaiQuay) {

		HoaDonTaiQuay h = new HoaDonTaiQuay();
		HinhThucThanhToan hh = hinhThucThanhToanRepository.findById(hoaDonTaiQuay.getHinhThucThanhToan())
				.orElseThrow(() -> {
					return new GeneralException("Bạn hãy cung cấp hình thức thanh toán", HttpStatus.BAD_REQUEST);
				});
		h.setHinhThucThanhToan(hh);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if (nguoiDung instanceof KhachHang) {
			throw new GeneralException("Bạn không có quyền tạo hóa đơn", HttpStatus.OK);
		}
		h.setNhanVien((NhanVien) nguoiDung);
		KhachHang khachHang = null;
		NguoiDung nd =nguoiDungService.getKhachHangBySDT(hoaDonTaiQuay.getSoDienThoai());
		if(nd instanceof KhachHang) {
			khachHang=(KhachHang)nd;
		}
		if (khachHang==null && hoaDonTaiQuay.getSoDienThoai() != null && hoaDonTaiQuay.getSoDienThoai().length() != 0
			&& hoaDonTaiQuay.getTenKhachHang() != null && hoaDonTaiQuay.getTenKhachHang().length() != 0) {
			KhachHang k = new KhachHang();
			k.setTen(hoaDonTaiQuay.getTenKhachHang());
			k.setSodienthoai(hoaDonTaiQuay.getSoDienThoai());
			khachHangService.save(k);
			khachHang=k;
		}
		if (khachHang != null) {
			h.setKhachHang(khachHang);
		}
		h.setNgayLap(LocalDateTime.now());
		hoaDonTaiQuayRepository.save(h);
		List<ParentCartLast> parentCartLasts = hoaDonOnlineService.calculate(hoaDonTaiQuay.getDanhSachMatHang());

		hoaDonTaiQuay.getDanhSachMatHang().stream().forEach(k -> {

			List<BienTheDealChinh> bienTheDealChinh = bienTheDealChinhService
					.getBienTheDealChinhOfSanPham(Arrays.asList(k.getIdBienThe()));
			FilterDeal filterDeal = new FilterDeal();
			if (bienTheDealChinh != null && !bienTheDealChinh.isEmpty()) {

				ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();
				apDungKhuyenMai.setHoaDon(h);
				apDungKhuyenMai.setKhuyenMai(bienTheDealChinh.get(0).getDeal());
				apDungKhuyenMai.setSoLuongApDung(bienTheDealChinh.get(0).getSoLuongTu());
				apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
				apDungKhuyenMai.setTyLeApDung(0);
				apDungKhuyenMai.setBienThe(bienTheDealChinh.get(0).getBienThe());
				apDungKhuyenMai.setDealPhu(false);
				apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);
			}
		});
		AtomicReference<Float> tongTien = new AtomicReference<Float>(0f);
		for (ParentCartLast pr : parentCartLasts) {
			BienThe bienThe = bienTheSerVice.getById(pr.getIdBienThe());
			ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
			chiTietHoaDon.setBienThe(bienThe);
			chiTietHoaDon.setDonGia(bienThe.getGia());
			chiTietHoaDon.setHoaDon(h);
			chiTietHoaDon.setSoLuong(pr.getTongSoLuong());
			chiTietHoaDon.setTongTien(pr.getTongTien());
			chiTietHoaDonService.save(chiTietHoaDon);
			tongTien.updateAndGet(v -> v + pr.getTongTien());

			// lưu danh sách khuyến mãi
			AtomicLong ideal = new AtomicLong(0);
			AtomicLong idflash = new AtomicLong(0);
			AtomicInteger tongSoLuongDeal = new AtomicInteger(0);
			AtomicInteger tongSoLuongFlashsale = new AtomicInteger(0);
			AtomicReference<Float> tyLeKhuyenMaiDeal = new AtomicReference<Float>(0f);
			AtomicReference<Float> tyLeKhuyenMaiFlashSale = new AtomicReference<Float>(0f);
			for (CartItemLast c : pr.getCartItemLasts()) {
				if (c.getIdDeal() != 0) {
					tongSoLuongDeal.addAndGet(c.getSoLuong());
					ideal.set(c.getIdDeal());
					tyLeKhuyenMaiDeal.set(c.getPhanTramDealGiam());
				}
				if (c.getIdFlashsale() != 0) {
					tongSoLuongFlashsale.addAndGet(c.getSoLuong());
					idflash.set(c.getIdFlashsale());
					tyLeKhuyenMaiFlashSale.set(c.getPhanTramFlashsale());
				}
			}

			if (ideal.get() != 0) {

				Deal d = dealService.getById(ideal.get());
				if ((d.getSoLuongGioiHan() - d.getSoLuongDaDung() + tongSoLuongDeal.get()) < 0) {
					throw new GeneralException(
							"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
							HttpStatus.BAD_REQUEST);
				}
				d.setSoLuongDaDung(d.getSoLuongDaDung() + tongSoLuongDeal.get());
				dealService.save(d);
			}
			// trừ vô số flash
			if (idflash.get() != 0) {
				FlashSale f = flashSaleService.getById(idflash.get());
				List<BienTheFlashSale> bienTheFlashSale = f.getBienTheFlashSale().stream()
						.filter(l -> l.getBienThe().getId() == bienThe.getId()).collect(Collectors.toList());
				if (bienTheFlashSale != null && bienTheFlashSale.size() != 0) {
					BienTheFlashSale bienTheFlashSale2 = bienTheFlashSale.get(0);
					if ((bienTheFlashSale2.getSoLuongGioiHan() - bienTheFlashSale2.getSoLuongDaDung()
							+ tongSoLuongFlashsale.get()) < 0) {
						throw new GeneralException(
								"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
								HttpStatus.BAD_REQUEST);
					}
					bienTheFlashSale2
							.setSoLuongDaDung(bienTheFlashSale2.getSoLuongDaDung() + tongSoLuongFlashsale.get());
					bienTheFlashSaleService.save(bienTheFlashSale2);
				}
			}
//			     tính toán lưu apdungkhuyenmai
			if (ideal.get() != 0) {

				ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();
				apDungKhuyenMai.setBienThe(bienThe);

				apDungKhuyenMai.setHoaDon(h);
				apDungKhuyenMai.setKhuyenMai(dealService.getById(ideal.get()));
				apDungKhuyenMai.setSoLuongApDung(tongSoLuongDeal.get());
				apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
				apDungKhuyenMai.setTyLeApDung(tyLeKhuyenMaiDeal.get());
				apDungKhuyenMai.setDealPhu(true);
				apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);

			}
			if (idflash.get() != 0) {

				ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();

				apDungKhuyenMai.setBienThe(bienThe);
				apDungKhuyenMai.setHoaDon(h);
				apDungKhuyenMai.setKhuyenMai(flashSaleService.getById(idflash.get()));
				apDungKhuyenMai.setSoLuongApDung(tongSoLuongFlashsale.get());
				apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
				apDungKhuyenMai.setTyLeApDung(tyLeKhuyenMaiFlashSale.get());
				apDungKhuyenMai.setDealPhu(true);
				apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);

			}

			// trừ số lượng hàng
			bienThe.setSoLuongKho(bienThe.getSoLuongKho() - pr.getTongSoLuong());
			bienTheSerVice.save(bienThe);

		}
		trangThaiHoaDonService.Save(h, trangThaiService.getById(2),"");
		h.setTrangThai(trangThaiService.getById(2));
		float lamTron = (float) (Math.ceil(tongTien.get() / 100) * 100);
		
		h.setTongTien(lamTron);
		float tongTie = h.getTongTien(); // Giả sử getTongTien() trả về float
		long lamTron2 = (long) (Math.ceil(tongTie / 1000.0f) * 1000);

		hoaDonTaiQuayRepository.save(h);
		// tạo thông tin thanh toán;
		Map<String, Object> result = new HashMap<String, Object>();

		if (hh.getId() == 1) {
			result.put("needCheck", false);
		} else {
			result.put("needCheck", true);
		}
		result.put("tongTien", lamTron2);
//		Map<String, String> thongTinThanhToan = new HashMap<String, String>();
		ZaloPayOrder zaloPayResponseDTO=null;
		if (hh.getId() == 1) {
			try {
//				thongTinThanhToan = momoService.createOrderPayment(lamTron2, h.getId());
				
				zaloPayResponseDTO=zaloPayService.createOrder(app_USER, lamTron2, 
						"Thanh toán đơn hàng SKINLY", String.valueOf(h.getId()));
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("Tạo đơn thất bại, thử lại ", HttpStatus.BAD_REQUEST);

			}
		}
		if (hh.getId() == 1) {
//			h.setqRCode(thongTinThanhToan.get("qrCodeUrl"));
			h.setqRCode(zaloPayResponseDTO.getOrderUrl());
			h.setTransId(zaloPayResponseDTO.getAppTransId());
//			h.setHD_zpTransToken(zaloPayResponseDTO.getZpTransToken());
			hoaDonTaiQuayRepository.save(h);
//			result.put("qrCodeUrl", thongTinThanhToan.get("qrCodeUrl"));
			result.put("qrCodeUrl",zaloPayResponseDTO.getOrderUrl());
			
		} else {
			result.put("qrCodeUrl", null);
		}
		
		result.put("idCall", h.getId());
		return result;
	}

	@Transactional
	public void xacNhanDaThanhToan(long id) {
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if (nguoiDung instanceof KhachHang) {
			throw new GeneralException("Bạn không có quyền tạo hóa đơn", HttpStatus.OK);
		}

		HoaDonTaiQuay h = hoaDonTaiQuayRepository.findById(id)
				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
		if (h.getTrangThai().getId() != 2) {
			throw new GeneralException("Bạn không có quyền xác nhận thanh toán với hóa đơn này", HttpStatus.OK);
		}
		if (h.getHinhThucThanhToan().getId() == 1) {
			throw new GeneralException("Bạn không có quyền xác nhận thanh toán với hóa đơn thanh toán online",
					HttpStatus.BAD_REQUEST);
		}
		if (nguoiDung.getId() != h.getNhanVien().getId()) {
			throw new GeneralException("Bạn không có quyền xác nhận thanh toán hóa đơn", HttpStatus.OK);
		}
		TrangThai trangThai = trangThaiService.getById(1);
		h.setTrangThai(trangThai);
		h.setDaThanhTona(true);
		hoaDonTaiQuayRepository.save(h);
		trangThaiHoaDonService.Save(h, trangThai,"");
	}

	@Transactional
	public Map<String, Object> chuyenDoiHinhThuc(long id) {
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if (nguoiDung instanceof KhachHang) {
			throw new GeneralException("Bạn không có quyền tạo hóa đơn", HttpStatus.OK);
		}

		HoaDonTaiQuay h = hoaDonTaiQuayRepository.findById(id)
				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
		if (nguoiDung.getId() != h.getNhanVien().getId()) {
			throw new GeneralException("Bạn không có quyền xác nhận thanh toán hóa đơn", HttpStatus.OK);
		}
		if (h.getTrangThai().getId() != 2) {
			throw new GeneralException("Bạn không có quyền chuyển đổi hình thức thanh toán với các hóa đơn này",
					HttpStatus.OK);
		}
		HinhThucThanhToan hd = null;
		if (h.getHinhThucThanhToan().getId() == 1) {
			hd = hinhThucThanhToanRepository.findById((long) 2).orElseThrow(() -> {
				return new GeneralException("Bạn hãy cung cấp hình thức thanh toán", HttpStatus.BAD_REQUEST);
			});
		} else {
			hd = hinhThucThanhToanRepository.findById((long) 1).orElseThrow(() -> {
				return new GeneralException("Bạn hãy cung cấp hình thức thanh toán", HttpStatus.BAD_REQUEST);
			});
		}
		h.setHinhThucThanhToan(hd);
		hoaDonTaiQuayRepository.save(h);
		float tongTien = h.getTongTien(); 
		long lamTron2 = (long) (Math.ceil(tongTien / 1000.0f) * 1000);
		Map<String, Object> result = new HashMap<String, Object>();
		if (hd.getId() == 1) {
			result.put("needCheck", false);
		} else {
			result.put("needCheck", true);
		}
		result.put("tongTien", lamTron2);
		ZaloPayOrder zaloPayResponseDTO=null;
		Map<String, String> thongTinThanhToan = new HashMap<String, String>();
		if (hd.getId() == 1) {
			try {
				if(h.getqRCode()==null) {
					 zaloPayResponseDTO=zaloPayService.createOrder(app_USER, lamTron2, 
							"Thanh toán đơn hàng SKINLY", String.valueOf(h.getId()));
					 h.setqRCode(zaloPayResponseDTO.getOrderUrl());
						h.setTransId(zaloPayResponseDTO.getAppTransId());
					HinhThucThanhToan hinhThucThanhToan= hinhThucThanhToanRepository.findById(1l).orElseThrow(()->new EntityNotFoundException("Không tìm thấy HTTT"));
					h.setHinhThucThanhToan(hinhThucThanhToan);
					hoaDonTaiQuayRepository.save(h);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException(
						"Bạn không thể thực hiện thay đổi nữa, hãy tạo lại đơn hoặc thanh toán bằng tiền mặt",
						HttpStatus.BAD_REQUEST);
			}
		}
		if (hd.getId() == 1) {
			if(h.getqRCode()!=null) {
				result.put("qrCodeUrl", h.getqRCode());
				HinhThucThanhToan hinhThucThanhToan= hinhThucThanhToanRepository.findById(1l).orElseThrow(()->new EntityNotFoundException("Không tìm thấy HTTT"));
				h.setHinhThucThanhToan(hinhThucThanhToan);
				hoaDonSerVice.Update(h);
			}
//			else {
//				result.put("qrCodeUrl", thongTinThanhToan.get("qrCodeUrl"));
//				h.setqRCode(thongTinThanhToan.get("qrCodeUrl"));
//				hoaDonTaiQuayRepository.save(h);
//			}
		} else {
			HinhThucThanhToan hinhThucThanhToan= hinhThucThanhToanRepository.findById(2l).orElseThrow(()->new EntityNotFoundException("Không tìm thấy HTTT"));
			h.setHinhThucThanhToan(hinhThucThanhToan);
			hoaDonSerVice.Update(h);
			result.put("qrCodeUrl", null);
		}

		result.put("idCall", h.getId());
		return result;
	}

//	@Transactional
//	public void listenMomo(long id, long transId, int code) {
//		HoaDonTaiQuay h = hoaDonTaiQuayRepository.findById(id)
//				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
//		if (code == 0) {
//			System.out.println("có vô 1");
//			if (h.getHinhThucThanhToan().getId() == 1 && h.getTrangThai().getId() != 11) {
//				TrangThai trangThai = trangThaiService.getById(1);
//				h.setTransId(transId);
//				h.setTrangThai(trangThai);
//				
//				h.setDaThanhTona(true);
//				hoaDonTaiQuayRepository.save(h);
//				trangThaiHoaDonService.Save(h, trangThai);
//			}
//			else {
//				System.out.println("có vô nhưng không set 1");
//			}
//		} else {
//			if (h.getTrangThai().getId() != 1 && h.getTrangThai().getId() != 11
//					&& h.getHinhThucThanhToan().getId() == 1) {
//				TrangThai trangThai = trangThaiService.getById(11);
//				h.setTrangThai(trangThai);
//				hoaDonTaiQuayRepository.save(h);
//				trangThaiHoaDonService.Save(h, trangThai);
//			}
//			else {
//				System.out.println("có vô nhưng không set 2");
//			}
//		}
//	}

	@Transactional
	public void HuyDon(long id) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if(hoaDon.getTrangThai().getId()==11) {
			throw new GeneralException("Hóa đơn này đã được hủy",HttpStatus.OK);
		}
		else if(hoaDon.getTrangThai().getId()==1) {
			throw new GeneralException("Hóa đơn đã được thanh toán thành công, vui lòng chọn hoàn hàng nếu khách hàng muốn trả lại hàng",HttpStatus.BAD_REQUEST);
		}
		else if(hoaDon.getTrangThai().getId()!=2) {
			throw new GeneralException("Bạn không thể hủy hóa đơn này",HttpStatus.BAD_REQUEST);
		}
		hoaDonSerVice.huyDonTaiQuay(id);
		TrangThai trangThai = trangThaiService.getById(11);
		trangThaiHoaDonService.Save(hoaDon, trangThai,"");
		hoaDon.setTrangThai(trangThai);
		hoaDonSerVice.Update(hoaDon);

	}
	
	@Transactional
	public void HoanDo(long id,int hinhThucHoa) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if (hoaDon.getTrangThai().getId() != 1) {
			throw new GeneralException("Hóa đơn này chưa thể hoàn được do khách hàng chưa thanh toán, bạn có thể thu hồi và hủy đơn",HttpStatus.BAD_REQUEST);
			
		}
		else {
			List<TrangThaiHoaDon> t = hoaDon.getTrangThaiHoaDon().stream().filter(d -> d.getTrangThai().getId() == 1)
					.collect(Collectors.toList());
			if (t == null || t.size() == 0) {
				throw new GeneralException("Bạn không thể hoàn hóa đơn này",HttpStatus.BAD_REQUEST);
			} else {
				Duration d = Duration.between(t.get(0).getThoiDiem(), LocalDateTime.now());
				if (d.getSeconds() > 43200) {
					throw new GeneralException("Đã hết thời hạn hoàn đơn di đã quá 12 tiếng",HttpStatus.BAD_REQUEST);
				}
			}
		}
		hoaDonSerVice.huyDonTaiQuay(id);
		TrangThai trangThai = trangThaiService.getById(8);
		trangThaiHoaDonService.Save(hoaDon, trangThai,"");
		hoaDon.setTrangThai(trangThai);
		hoaDonSerVice.Update(hoaDon);

	}
	
	
	
	@Transactional
	public void hoanDonCheckTaiQuay(long id, int lan,boolean re) {
		HoaDon hoaDo = hoaDonSerVice.getById(id);
		if(hoaDo instanceof HoaDonOnline) {
			throw new GeneralException("Bạn không thể chủ động hoàn trả hóa đơn này",HttpStatus.BAD_REQUEST);
		}
		HoaDonTaiQuay hoaDon = (HoaDonTaiQuay)hoaDo;
		if(hoaDon.getHinhThucThanhToan().getId()==2 && hoaDon.isDaHoanHang()==false && hoaDon.getTrangThai().getId()==1) {
			
				List<TrangThaiHoaDon> t = hoaDon.getTrangThaiHoaDon().stream().filter(d -> d.getTrangThai().getId() == 1)
						.collect(Collectors.toList());
				if (t == null || t.size() == 0) {
					throw new GeneralException("Bạn không thể hoàn hóa đơn này",HttpStatus.BAD_REQUEST);
				} else {
					Duration d = Duration.between(t.get(0).getThoiDiem(), LocalDateTime.now());
					if (d.getSeconds() > 43200) {
						throw new GeneralException("Đã hết thời hạn hoàn đơn di đã quá 12 tiếng",HttpStatus.BAD_REQUEST);
					}
				}
			hoaDonSerVice.huyDonTaiQuay(id);
			TrangThai trangThai = trangThaiService.getById(8);
			trangThaiHoaDonService.Save(hoaDon, trangThai,"");
			hoaDon.setTrangThai(trangThai);
			hoaDon.setHinhThucHoanTra(2);
			hoaDon.setDaHoanHang(true);
			hoaDonSerVice.Update(hoaDon);
			return;
		}
		else if(hoaDon.getHinhThucThanhToan().getId()==1 && re==false && hoaDon.isDaHoanHang()==false && hoaDon.getTrangThai().getId()==1) {
			
			long tienGoc = (long) hoaDon.getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000;  
			
			hoaDonSerVice.huyDonTaiQuay(id);
			TrangThai trangThai = trangThaiService.getById(8);
			trangThaiHoaDonService.Save(hoaDon, trangThai,"");
			hoaDon.setHinhThucHoanTra(1);
			hoaDon.setTrangThai(trangThai);
			hoaDon.setDaHoanHang(true);
			hoaDonTaiQuayRepository.save(hoaDon);
			try {
				 zaloPayService.refund(hoaDon.getHD_zpTrans_Id()+"", soTienRefound,"Hoàn tiền đơn hàng- "+hoaDon.getId(),hoaDon.getRefund(), null);
			} catch (Exception e) {
				if(lan<2) {
					throw new GeneralException("Hoàn tiền thất baị vui lòng thử lại",HttpStatus.INTERNAL_SERVER_ERROR);
				}
				else {
					throw new GeneralException("Bạn có thể sử dụng tiền mặt để hoàn trả hóa đơn này",HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			return;
		}
		else if(hoaDon.getHinhThucThanhToan().getId()==1 && re==true && hoaDon.getTransId()!=null){
			
			hoaDonSerVice.huyDonTaiQuay(id);
			TrangThai trangThai = trangThaiService.getById(8);
			trangThaiHoaDonService.Save(hoaDon, trangThai,"");
			hoaDon.setHinhThucHoanTra(2);
			hoaDon.setTrangThai(trangThai);
			hoaDo.setDaHoanHang(true);
			hoaDonTaiQuayRepository.save(hoaDon);
			return;
		}
		throw new GeneralException("Hóa đơn không đủ điều kiện hoàn trả", HttpStatus.BAD_REQUEST);
	}

	public boolean isCartItemsDifferent(List<CartItem> cartItems, List<ChiTietHoaDon> chiTietHoaDons) {
		if (cartItems == null && chiTietHoaDons == null) {
			return false;
		}
		if (cartItems == null || chiTietHoaDons == null) {
			return true;
		}
		if (cartItems.size() != chiTietHoaDons.size()) {
			return true;
		}
		Map<Integer, Integer> cartItemMap = new HashMap<>();
		for (CartItem item : cartItems) {
			cartItemMap.put(item.getIdBienThe(), item.getSoLuong());
		}
		for (ChiTietHoaDon chiTiet : chiTietHoaDons) {
			int idBienThe = chiTiet.getBienThe().getId();
			int soLuong = chiTiet.getSoLuong();
			if (!cartItemMap.containsKey(idBienThe) || cartItemMap.get(idBienThe) != soLuong) {
				return true;
			}
		}
		Map<Integer, Integer> chiTietMap = new HashMap<>();
		for (ChiTietHoaDon chiTiet : chiTietHoaDons) {
			chiTietMap.put(chiTiet.getBienThe().getId(), chiTiet.getSoLuong());
		}
		for (CartItem item : cartItems) {
			int idBienThe = item.getIdBienThe();
			int soLuong = item.getSoLuong();
			if (!chiTietMap.containsKey(idBienThe) || chiTietMap.get(idBienThe) != soLuong) {
				return true;
			}
		}
		return false;
	}

	public long getTrangThaiHoaDon(long id, int lan) {
		HoaDonTaiQuay h = hoaDonTaiQuayRepository.findById(id)
				
				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
		if(lan%3==0 && h.isDaThanhTona()==false) {
			hoaDonSerVice.checkThanhToan(h);
		}
		return h.getTrangThai().getId();
	}
	
	
	
	
	public Map<String, Object> getBillThanhToan(long id){
		HoaDonTaiQuay h= (HoaDonTaiQuay)hoaDonSerVice.getById(id);
		Map<String,Object> re= new HashMap<String, Object>();
		re.put("tenCongTy","SKINLY");
		re.put("diaDiem","106–32 Hai Bà Trưng, phường Tân An, quận Ninh Kiều, TP. Cần Thơ");
		re.put("website","www.skinly.vn");
		re.put("soHoaDon",h.getId());
		re.put("ngayLap", h.getNgayLap());
		re.put("nhanVien",h.getNhanVien().getTen());
		re.put("tongTienThanhToan", h.getTongTien());
		re.put("hinhThucThanhToan", h.getHinhThucThanhToan().getTen());
		List<Map<String, Object>> item=
		h.getChiTietHoaDons().stream().map(k->{
			Map<String, Object> i= new HashMap<String, Object>();
			i.put("tenSanPham", k.getBienThe().getSanPham().getTen()+(k.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"":(" ("+k.getBienThe().getTen() +")")));
			i.put("soLuongMua",k.getSoLuong());
			ThueVATSanPham v= thueVATSanPhamSerVice.getThueVATAtTime(k.getBienThe().getSanPham().getId(),h.getNgayLap());
			
			float giaBanSauThue =(k.getDonGia() * (1 + v.getTiLe() / 100f));
			
			i.put("donGia",giaBanSauThue);
			double tongTruocGiam = giaBanSauThue * k.getSoLuong();
			int tongGiamGia = (int) Math.round(tongTruocGiam - k.getTongTien());
			if(tongGiamGia<=0) {
				i.put("tongGiamGia", 0);
			}
			else {
				i.put("tongGiamGia", tongGiamGia);
			}
			
			i.put("thanhTien", k.getTongTien());
			return i;
		}).collect(Collectors.toList());
		re.put("detail", item);
		return re;
	}
	
	
	
	
	public byte[] generateBillPdf(Map<String, Object> billData) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(baos);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    // Create a table for horizontal alignment of titles
	    Table titleTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
	    titleTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	    // First title: SKINLY
	    Paragraph companyTitle = new Paragraph("SKINLY")
	            .setFont(getFontHeader())
	            .setFontSize(16)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontColor(ColorConstants.BLACK);
	    Paragraph address = new Paragraph("106–32 Hai Bà Trưng, phường Tân An,\nquận Ninh Kiều, TP. Cần Thơ\nWebsite: www.skinly.vn")
	            .setFont(getFont())
	            .setFontSize(10)
	            .setTextAlignment(TextAlignment.CENTER);
	    Cell companyCell = new Cell().add(companyTitle).add(address).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
	    titleTable.addCell(companyCell);

	    // Second title: PHIẾU THANH TOÁN
	    Paragraph billTitle = new Paragraph("PHIẾU THANH TOÁN")
	            .setFont(getFontHeader())
	            .setFontSize(16)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontColor(ColorConstants.BLACK);
	    Paragraph billDetails = new Paragraph("Số hóa đơn: 23242684\nNgày lập: 13/08/2025 08:29\nNhân viên lập: Nguyễn Thị Dung")
	            .setFont(getFontNormal())
	            .setFontSize(10)
	            .setTextAlignment(TextAlignment.CENTER);
	    Cell billCell = new Cell().add(billTitle).add(billDetails).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
	    titleTable.addCell(billCell);

	    document.add(titleTable);

	    document.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------").setFont(getFont()).setFontSize(10));
	    document.add(new Paragraph(" "));

	    // Product table
	    float[] columnWidths = {4, 2, 3, 2, 3};
	    Table productTable = new Table(columnWidths).useAllAvailableWidth();
	    productTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	    // Headers
	    productTable.addHeaderCell(new Cell().add(new Paragraph("Sản Phẩm").setFont(getFontHeaderTable()).setFontSize(12)).setBorder(null).setPadding(5));
	    productTable.addHeaderCell(new Cell().add(new Paragraph("SL").setFont(getFontHeaderTable()).setFontSize(12)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	    productTable.addHeaderCell(new Cell().add(new Paragraph("Đ.Giá").setFont(getFontHeaderTable()).setFontSize(12)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	    productTable.addHeaderCell(new Cell().add(new Paragraph("Giảm").setFont(getFontHeaderTable()).setFontSize(12)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	    productTable.addHeaderCell(new Cell().add(new Paragraph("T.Tiền").setFont(getFontHeaderTable()).setFontSize(12)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));

	    // Sample product data (replace with dynamic data from billData)
	    @SuppressWarnings("unchecked")
	    List<Map<String, Object>> details = (List<Map<String, Object>>) billData.get("detail");
	    for (Map<String, Object> item : details) {
	        productTable.addCell(new Cell().add(new Paragraph((String) item.get("tenSanPham")).setFont(getFont()).setFontSize(10)).setBorder(null).setPadding(5));
	        productTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.get("soLuongMua"))).setFont(getFont()).setFontSize(10)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	        productTable.addCell(new Cell().add(new Paragraph(formatWithoutRounding((float)item.get("donGia"))).setFont(getFont()).setFontSize(10)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	        productTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.get("tongGiamGia"))).setFont(getFont()).setFontSize(10)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	        productTable.addCell(new Cell().add(new Paragraph(formatWithoutRounding((float)item.get("thanhTien"))).setFont(getFont()).setFontSize(10)).setTextAlignment(TextAlignment.CENTER).setBorder(null).setPadding(5));
	    }
	    document.add(productTable);

	    document.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------").setFont(getFont()).setFontSize(10));
	    document.add(new Paragraph(" "));

	    // Total and payment method
	    
	    billData.get("tongTienThanhToan");
	    billData.get("hinhThucThanhToan");
	    Paragraph totalInfo = new Paragraph("Tổng tiền thanh toán: " + formatRoundToThousands((float)billData.get("tongTienThanhToan")) + " VND\nHình thức thanh toán: " + billData.get("hinhThucThanhToan"))
	            .setFont(getFont())
	            .setFontSize(10)
	            .setTextAlignment(TextAlignment.LEFT)
	            .setBold()
	            .setFontColor(ColorConstants.BLACK);
	    document.add(totalInfo);

	    document.close();
	    return baos.toByteArray();
	}
	public  String formatWithoutRounding(double number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    public  String formatRoundToThousands(double number) {
        long rounded = (long) (Math.ceil(number / 1000.0) * 1000);
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(rounded);
    }
	private PdfFont getFont() throws IOException {
	    String fontPath = getClass().getClassLoader().getResource("fonts/Inter_18pt-Italic.ttf").getPath();
	    if (fontPath == null) {
	        throw new IOException("Không tìm thấy file font: fonts/Roboto-ExtraLight.ttf");
	    }
	    return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
	}
	private PdfFont getFontNormal() throws IOException {
	    String fontPath = getClass().getClassLoader().getResource("fonts/Inter_24pt-Regular.ttf").getPath();
	    if (fontPath == null) {
	        throw new IOException("Không tìm thấy file font: fonts/Roboto-ExtraLight.ttf");
	    }
	    return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
	}
	private PdfFont getFontHeaderTable() throws IOException {
	    String fontPath = getClass().getClassLoader().getResource("fonts/Inter_18pt-Bold.ttf").getPath();
	    if (fontPath == null) {
	        throw new IOException("Không tìm thấy file font: fonts/Roboto-ExtraLight.ttf");
	    }
	    return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
	}
	private PdfFont getFontHeader() throws IOException {
	    String fontPath = getClass().getClassLoader().getResource("fonts/Inter_24pt-Bold.ttf").getPath();
	    if (fontPath == null) {
	        throw new IOException("Không tìm thấy file font: fonts/Roboto-ExtraLight.ttf");
	    }
	    return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
	}


	private PdfFont getBoldFont() throws IOException {
	    String boldFontPath = getClass().getClassLoader().getResource("fonts/Roboto-ExtraLight.ttf").getPath();
	    if (boldFontPath == null) {
	        throw new IOException("Không tìm thấy file font: fonts/Roboto-ExtraLight.ttf");
	    }
	    return PdfFontFactory.createFont(boldFontPath, PdfEncodings.IDENTITY_H);
	}

	 public byte[] renderBill(long id) throws Exception {
			return generateBillPdf(getBillThanhToan(id));
		}

	 public Map<String, Object> filterHoaDonTaiQuay(
			 String maHoaDon,
			 String khachHang,
			 long trangThai,
			 LocalDate ngayLap,
			 int sort,
			 int trang
			 ) {
		 Pageable p= PageRequest.of(trang, 15);
		 Page<HoaDonTaiQuay> ds= hoaDonTaiQuayRepository.findHoaDonTaiQuayByFilters
				 (maHoaDon, trangThai, khachHang, ngayLap, sort, p);
		 Map<String, Object> re= new HashMap<String, Object>();
		 re.put("tongTrang",ds.getTotalPages());
		 re.put("tongSoPhanTu",ds.getTotalElements());
		 re.put("danhSach",ds.getContent().stream().map(d->{
			 Map<String, Object> i= new HashMap<String, Object>();
				i.put("id", d.getId());
				i.put("ngayLap",d.getNgayLap());
				i.put("tongTien", d.getTongTien());
				i.put("tongSoLuongHang", d.getChiTietHoaDons().stream().mapToInt(f->f.getSoLuong()).sum());
				i.put("tongSoMatHang", d.getChiTietHoaDons().size());
				i.put("daThanhToan", d.isDaThanhTona());
				i.put("daHoanHang", d.isDaHoanHang());
				i.put("idKhachHang", d.getKhachHang()!=null?d.getKhachHang().getId():0);
				i.put("tenKhachHang",d.getKhachHang()!=null?d.getKhachHang().getTen():"Khách vãng lai");
				return i;
		 }).collect(Collectors.toList()));
		 return re;
	 }
	 
	 public Map<String, Object> viewHoaDonAlReady(long idHoaDon) {
			HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
			if(hoaDon instanceof HoaDonOnline) {
				throw new GeneralException("Hóa đơn truy cập không hợp lệ", HttpStatus.NOT_FOUND);
			}
			NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
			if (nguoiDung instanceof KhachHang ) {
				throw new GeneralException("Bạn không có quyền truy cập vào hóa đơn này", HttpStatus.NOT_FOUND);
			}
			List<ParentCartLast> gioHangCuoi =hoaDonOnlineService.mapToHoaDonConver(idHoaDon);
			Map<Long, Map<String, Object>> m = new HashMap<Long, Map<String, Object>>();

			gioHangCuoi.stream().forEach(d -> {
				Map<String, Object> m1 = new HashMap<String, Object>();
				BienThe b = bienTheSerVice.getById(d.getIdBienThe());
				com.example.e_commerce.model.SanPham s = b.getSanPham();
				if (m.get(s.getId()) != null) {
					m1 = m.get(s.getId());
				} else {
					m.put(s.getId(), m1);
					m1.put("tenSanPham", s.getTen());
					m1.put("anhBia", s.getAnhBia());
					m1.put("danhSachCon", new ArrayList<ParentCartLast>());
				}

				List<ParentCartLast> pp = (List<ParentCartLast>) m1.get("danhSachCon");
				pp.add(d);
			});

			float tongTien = (float) gioHangCuoi.stream().flatMap(d -> d.getCartItemLasts().stream())
					.mapToDouble(d -> d.getSoLuong() * d.getGiaGiam()).sum();

			float giamGia = (float) gioHangCuoi.stream().mapToDouble(ParentCartLast::getTongGiaTriGiam).sum();

			Map<String, Object> result = new HashMap<String, Object>();
			float lamTron = (float) (Math.ceil(tongTien / 1000) * 1000);
			result.put("tongTien", lamTron);
			result.put("giamGia", giamGia);
			result.put("ngayLap", hoaDon.getNgayLap());
			result.put("canUpdateCustomer", false);
			result.put("hoanHangCustomer", true);
			result.put("canThanhToan", false);
			result.put("huyDonHangCustomer", false);
			if(hoaDon.isDaThanhTona()==false && hoaDon.getTrangThai().getId()==2) {
				result.put("canThanhToan", true);
			}
			if (hoaDon.getTrangThai().getId() != 1) {
				if (hoaDon.getTrangThai().getId() != 1) {
					result.put("hoanHangCustomer", false);
				}
				List<TrangThaiHoaDon> t = hoaDon.getTrangThaiHoaDon().stream().filter(d -> d.getTrangThai().getId() == 1)
						.collect(Collectors.toList());
				if (t == null || t.size() == 0) {
					result.put("hoanHangCustomer", false);
				} else {
					Duration d = Duration.between(t.get(0).getThoiDiem(), LocalDateTime.now());
					if (d.getSeconds() > 43200) {
						result.put("hoanHangCustomer", false);
					}
				}
			}
			if(hoaDon.getTrangThai().getId()==2) {
				result.put("huyDonHangCustomer", true);
			}
			result.put("data", m.values().stream().collect(Collectors.toList()));
			return result;
		}
	 
	 public Map<String, Object> PrePaymentHoaDon(long id) {
		 HoaDonTaiQuay h= hoaDonTaiQuayRepository.findById(id).orElseThrow(()->{
			 throw new GeneralException("Không tìm thấy thông tin hóa đơn", HttpStatus.NOT_FOUND);
		 });
		 System.out.println("QRCODE: "+h.getqRCode());
		 
		 Map<String, Object> result = new HashMap<String, Object>();

			if (h.getHinhThucThanhToan().getId() == 1) {
				result.put("needCheck", false);
			} else {
				result.put("needCheck", true);
			}
			result.put("tongTien", h.getTongTien());
			if (h.getHinhThucThanhToan().getId() == 1) {
				result.put("qrCodeUrl", h.getqRCode());
				
			} else {
				result.put("qrCodeUrl", null);
			}

			result.put("idCall", h.getId());
			return result;
		 
	 }
	 
//	 public int getHinhThucThanhToan(long id) {
//		 HoaDonTaiQuay h= hoaDonTaiQuayRepository.findById(id).orElseThrow(()->{
//			 throw new GeneralException("Không tìm thấy thông tin hóa đơn", HttpStatus.NOT_FOUND);
//		 });
//		 if(h.getTrangThai().getId()!=1) {
//			 throw new GeneralException("Không thể hoàn đơn hàng này", HttpStatus.OK);
//		 }
//		 List<TrangThaiHoaDon> t = h.getTrangThaiHoaDon().stream().filter(d -> d.getTrangThai().getId() == 1)
//					.collect(Collectors.toList());
//			if (t == null || t.size() == 0) {
//				throw new GeneralException("Không thể hoàn đơn hàng này", HttpStatus.OK);
//			} else {
//				Duration d = Duration.between(t.get(0).getThoiDiem(), LocalDateTime.now());
//				if (d.getSeconds() > 43200) {
//					throw new GeneralException("Không thể hoàn đơn hàng này", HttpStatus.OK);
//				}
//			}
//			return h.getHinhThucThanhToan().
//	 }
	 
	 public long getHinhThucThanhToan(long id) {
		 HoaDonTaiQuay h= hoaDonTaiQuayRepository.findById(id).orElseThrow(()->{
			 throw new GeneralException("Không tìm thấy thông tin hóa đơn", HttpStatus.NOT_FOUND);
		 });
		 return h.getHinhThucThanhToan().getId();
	 }
	 
//	 public void 

}

class FilterDeal {
	Deal d;
	int soLuong;

	public Deal getD() {
		return d;
	}

	public void setD(Deal d) {
		this.d = d;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

}

class LuotDungFlash {
	int soLuong;
	float tyLe;
	FlashSale flashSale;

	public FlashSale getFlashSale() {
		return flashSale;
	}

	public void setFlashSale(FlashSale flashSale) {
		this.flashSale = flashSale;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public float getTyLe() {
		return tyLe;
	}

	public void setTyLe(float tyLe) {
		this.tyLe = tyLe;
	}

}
