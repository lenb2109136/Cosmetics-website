package com.example.e_commerce.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.hibernate.internal.build.AllowSysOut;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.CartItem;
import com.example.e_commerce.DTO.request.GHNItem;
import com.example.e_commerce.DTO.request.GHNOrder;
import com.example.e_commerce.DTO.request.SanPham;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.DTO.response.CartItemLast;
import com.example.e_commerce.DTO.response.DonGiaBan;
import com.example.e_commerce.DTO.response.ParentCartLast;
import com.example.e_commerce.DTO.response.ZaloPayOrder;
import com.example.e_commerce.Exception.Custom.ErrorData;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.constants.GHNConstans;
import com.example.e_commerce.model.ApDungKhuyenMai;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.DongHop;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.HinhThucThanhToan;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.KhuyenMai;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.ThueVATSanPham;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.model.TrangThaiHoaDon;
import com.example.e_commerce.repository.HoaDonOnlineRepository;

@Service
public class HoaDonOnlineService {

	@Autowired
	HoaDonOnlineRepository hoaDonOnlineRepository;

	@Autowired
	private PushNotificationService pushNotificationService;

	@Autowired
	private ZaloPayService zaloPayService;
	@Autowired
	private BienTheSerVice bienTheSerVice;

	@Autowired
	private DongHopService dongHopService;

	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;

	@Autowired
	private MomoService momoService;

	@Autowired
	private GHNService ghnService;

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
	private HoaDonSerVice hoaDonSerVice;

	@Value("${app_USER}")
	private String app_USER;

//	public List<ParentCartLast> calculate(List<CartItem> danhSachBienThe) {
//		// Sắp xếp danh sách biến thể theo giá giảm dần
//		List<CartItem> danhSachBienTheSapXep = danhSachBienThe.stream().sorted((item1, item2) -> {
//			BienThe bienThe1 = bienTheSerVice.getById(item1.getIdBienThe());
//			BienThe bienThe2 = bienTheSerVice.getById(item2.getIdBienThe());
//			return Double.compare(bienThe1.getGia(), bienThe2.getGia());
//		}).collect(Collectors.toList());
//		SanPhamService sanPhamService=ServiceLocator.getBean(SanPhamService.class);
//		// Kiểm tra tồn kho
//		for (CartItem item : danhSachBienTheSapXep) {
//			BienThe bienThe = bienTheSerVice.getById(item.getIdBienThe());
//			if (bienThe == null) {
//				throw new GeneralException("Biến thể không tồn tại: " + item.getIdBienThe(), HttpStatus.BAD_REQUEST);
//			}
//			long soLuongTonKho = bienThe.getSoLuongKho();
//			int soLuongHaoHut=sanPhamService.soLuongHaoHienGio(bienThe);
//			if (item.getSoLuong() > (soLuongTonKho-soLuongHaoHut)) {
//				
//				item.setSoLuong((int)(soLuongTonKho-soLuongHaoHut));
//				throw new ErrorData("Số lượng đặt của phân loại: " + (bienThe.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định":bienThe.getTen()) + " không được vượt quá: "
//						+ (soLuongTonKho-soLuongHaoHut), HttpStatus.INTERNAL_SERVER_ERROR, danhSachBienThe);
//			}
//			
//		}
//
//		// Tính số lượng giới hạn cho mỗi deal
//		List<FilterDeal> dealApDung = danhSachBienTheSapXep.stream().map(dm -> {
//			List<BienTheDealChinh> bienTheDealChinh = bienTheDealChinhService
//					.getBienTheDealChinhOfSanPham(List.of(dm.getIdBienThe()));
//			FilterDeal filterDeal = new FilterDeal();
//			if (bienTheDealChinh != null && !bienTheDealChinh.isEmpty()) {
//				filterDeal.setD(bienTheDealChinh.get(0).getDeal());
//				filterDeal.setSoLuong(dm.getSoLuong() / bienTheDealChinh.get(0).getSoLuongTu());
//			}
//			return filterDeal;
//		}).filter(l -> l.getSoLuong() > 0)
//				.collect(Collectors.collectingAndThen(
//						Collectors.groupingBy(FilterDeal::getD, Collectors.summingInt(FilterDeal::getSoLuong)),
//						map -> map.entrySet().stream().map(entry -> {
//							Deal deal = entry.getKey();
//							int soLuongTongHop = entry.getValue();
//							int soLuongConLai = deal.getSoLuongGioiHan() - deal.getSoLuongDaDung();
//							FilterDeal filterDeal = new FilterDeal();
//							filterDeal.setD(deal);
//							filterDeal.setSoLuong(Math.min(soLuongTongHop, soLuongConLai));
//							return filterDeal;
//						}).filter(fd -> fd.getSoLuong() > 0).collect(Collectors.toList())));
//
//		// Xử lý giỏ hàng cuối
//		List<ParentCartLast> gioHangCuoi = danhSachBienTheSapXep.stream().map(m -> {
//			BienThe d = bienTheSerVice.getById(m.getIdBienThe());
//			if (d == null) {
//				throw new GeneralException("Biến thể không tồn tại: " + m.getIdBienThe(), HttpStatus.BAD_REQUEST);
//			}
//			ParentCartLast parentCartLast = new ParentCartLast();
//			parentCartLast.setTenBienThe((d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định":d.getTen()));
//			parentCartLast.setIdBienThe(d.getId());
//			float thueVAT = d.getSanPham().getThuevat();
//			parentCartLast.setGiaGoc(d.getGia() * (1 + thueVAT / 100));
//			parentCartLast.setAnhGioiThieu(d.getAnhBia());
//
//			List<BienTheFlashSale> flashSales = bienTheFlashSaleService
//					.getBienTheFlahSaleDangApDungOfSanPham(List.of(d.getId()));
//
//			int soLuongConLaiFlash = 0;
//			float phanTramGiamFlash = 0;
//			Long idFlash = null;
//			if (flashSales != null && !flashSales.isEmpty()) {
//				LocalTime now = LocalTime.now();
//				LocalTime gioBatDau = flashSales.get(0).getFlashSale().getGioBatDau();
//				LocalTime gioKetThuc = flashSales.get(0).getFlashSale().getGioKetThuc();
//				if (gioBatDau != null && gioKetThuc != null && now.isAfter(gioBatDau) && now.isBefore(gioKetThuc)) {
//					soLuongConLaiFlash = Math.max(0,
//							flashSales.get(0).getSoLuongGioiHan() - flashSales.get(0).getSoLuongDaDung());
//					phanTramGiamFlash = flashSales.get(0).getGiaTriGiam();
//					idFlash = flashSales.get(0).getFlashSale().getId();
//				}
//			}
//
//			int soLuongTrongGio = m.getSoLuong();
//			long soLuongTonKho = d.getSoLuongKho();
//			if (soLuongTrongGio > soLuongTonKho) {
//				throw new GeneralException(
//						"Số lượng đặt của phân loại: " + d.getTen() + " không được vượt quá: " + d.getSoLuongKho(),
//						HttpStatus.BAD_REQUEST);
//			}
//
//			int soLuongDuocFlash = Math.min(soLuongTrongGio, soLuongConLaiFlash);
//			int soLuongKhongFlash = soLuongTrongGio - soLuongDuocFlash;
//
//			if (soLuongDuocFlash > 0) {
//				CartItemLast itemFlash = new CartItemLast();
//				float giaSauGiamFlash = d.getGia() * (1 - phanTramGiamFlash / 100);
//				itemFlash.setGiaGiam(giaSauGiamFlash * (1 + thueVAT / 100));
//				itemFlash.setSoLuong(soLuongDuocFlash);
//				itemFlash.setIdFlashsale(idFlash);
//				itemFlash.setPhanTramFlashsale(phanTramGiamFlash);
//				parentCartLast.addItemCart(itemFlash);
//			}
//
//			if (soLuongKhongFlash > 0) {
//				CartItemLast itemNormal = new CartItemLast();
//				itemNormal.setGiaGiam(d.getGia() * (1 + thueVAT / 100));
//				itemNormal.setSoLuong(soLuongKhongFlash);
//				itemNormal.setIdFlashsale(0L);
//				itemNormal.setPhanTramFlashsale(0f);
//				parentCartLast.addItemCart(itemNormal);
//			}
//
//			return parentCartLast;
//		}).collect(Collectors.toList());
//
//		// Phân bổ deal
//		dealApDung.forEach(deal -> {
//			List<BienTheDealPhu> bienTheDealPhuList = deal.getD().getBienTheDealPhu();
//			Map<Integer, Float> tempDealMap = new HashMap<>();
//			bienTheDealPhuList.forEach(dealPhu -> {
//				int idBienThe = dealPhu.getBienThe().getId();
//				float giam = dealPhu.getGiaTriGiam();
//				tempDealMap.put(idBienThe, giam);
//			});
//
//			// Sắp xếp giỏ hàng theo mức giảm giá giảm dần
//			gioHangCuoi.sort((p1, p2) -> {
//				float giam1 = tempDealMap.getOrDefault(p1.getIdBienThe(), 0f);
//				float giam2 = tempDealMap.getOrDefault(p2.getIdBienThe(), 0f);
//				return Float.compare(giam2, giam1);
//			});
//
//			int soLuongDealConLai = deal.getSoLuong();
//
//			// Duyệt từ đầu danh sách gioHangCuoi (vị trí 0)
//			for (int i = 0; i < gioHangCuoi.size() && soLuongDealConLai > 0; i++) {
//				ParentCartLast cart = gioHangCuoi.get(i);
//				float phanTramGiam = tempDealMap.getOrDefault(cart.getIdBienThe(), 0f);
//				if (phanTramGiam <= 0f)
//					continue;
//
//				List<CartItemLast> danhSachItem = cart.getCartItemLasts();
//
//				// Duyệt từ cuối danh sách item (từ dưới lên)
//				for (int j = danhSachItem.size() - 1; j >= 0 && soLuongDealConLai > 0; j--) {
//					CartItemLast item = danhSachItem.get(j);
//					if (item.getPhanTramDealGiam() == 0 && item.getSoLuong() > 0) {
//						int soLuongItem = item.getSoLuong();
//						int soLuongApDung = Math.min(soLuongItem, soLuongDealConLai);
//
//						if (soLuongItem == soLuongApDung) {
//							// Gán trực tiếp nếu vừa đủ
//							item.setIdDeal(deal.getD().getId());
//							item.setPhanTramDealGiam(phanTramGiam);
//							item.setTenDeal(deal.getD().getTenChuongTrinh());
//						} else {
//							// Tách thành 2 CartItemLast
//							CartItemLast itemCoDeal = new CartItemLast();
//							itemCoDeal.setSoLuong(soLuongApDung);
//							itemCoDeal.setIdDeal(deal.getD().getId());
//							itemCoDeal.setPhanTramDealGiam(phanTramGiam);
//							itemCoDeal.setTenDeal(deal.getD().getTenChuongTrinh());
//							itemCoDeal.setIdFlashsale(item.getIdFlashsale());
//							itemCoDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
//							itemCoDeal.setGiaGiam(item.getGiaGiam()); // Giữ nguyên giá giảm
//
//							CartItemLast itemKhongDeal = new CartItemLast();
//							itemKhongDeal.setSoLuong(soLuongItem - soLuongApDung);
//							itemKhongDeal.setIdDeal(0);
//							itemKhongDeal.setPhanTramDealGiam(0);
//							itemKhongDeal.setTenDeal("");
//							itemKhongDeal.setIdFlashsale(item.getIdFlashsale());
//							itemKhongDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
//							itemKhongDeal.setGiaGiam(item.getGiaGiam()); // Giữ nguyên giá giảm
//
//							// Thay thế item cũ
//							danhSachItem.remove(j);
//							danhSachItem.add(j, itemKhongDeal);
//							danhSachItem.add(j, itemCoDeal);
//						}
//
//						soLuongDealConLai -= soLuongApDung;
//					}
//				}
//			}
//		});
//
//		// Tính toán giá cuối cùng
//		gioHangCuoi.forEach(parentCartLast -> {
//			BienThe d = bienTheSerVice.getById(parentCartLast.getIdBienThe());
//			float thueVAT = d.getSanPham().getThuevat();
//			System.out.println("thuws VA sản phẩm là");
//			List<CartItemLast> updatedItems = new ArrayList<>();
//			float totalAmount = 0f;
//			int totalQuantity = 0;
//			float totalDiscount = 0f;
//
//			for (CartItemLast item : parentCartLast.getCartItemLasts()) {
//				int sl = item.getSoLuong();
//				if (sl <= 0)
//					continue;
//
//				// Giá gốc chưa bao gồm VAT
//				float giaBanDau = d.getGia();
//				// Áp dụng flash sale trước
//				float giaSauFlash = giaBanDau * (1 - item.getPhanTramFlashsale() / 100);
//				// Áp dụng deal lên giá sau flash sale
//				float giaSauDeal = giaSauFlash * (1 - item.getPhanTramDealGiam() / 100);
//				// Áp VAT lên giá sau cùng
//				System.out.println("GIÁ TRỊ THUẾ VAT :"+thueVAT);
//				System.out.println("GIÁ SAU DEAL: "+giaSauDeal);
//				float giaSauThue = giaSauDeal * (1 + thueVAT / 100);
//
//				item.setGiaGiam(giaSauThue);
//				totalAmount += giaSauThue * sl;
//				totalQuantity += sl;
//				totalDiscount += (giaBanDau * (1 + thueVAT / 100) - giaSauThue) * sl;
//
//				updatedItems.add(item);
//			}
//
//			parentCartLast.setCartItemLasts(updatedItems);
//			parentCartLast.setTongTien(totalAmount);
//			parentCartLast.setTongSoLuong(totalQuantity);
//			parentCartLast.setTongGiaTriGiam((d.getGia() * (1 + thueVAT / 100)) * totalQuantity - totalAmount);
//		});
//
//		return gioHangCuoi;
//	}
	public List<ParentCartLast> calculate(List<CartItem> danhSachBienThe) {
		// Sắp xếp danh sách biến thể theo giá trị giảm thực tế giảm dần
		List<CartItem> danhSachBienTheSapXep = danhSachBienThe.stream().sorted((item1, item2) -> {
			BienThe bienThe1 = bienTheSerVice.getById(item1.getIdBienThe());
			BienThe bienThe2 = bienTheSerVice.getById(item2.getIdBienThe());

			// Tìm deal phụ áp dụng cho biến thể 1
			float giam1 = 0f;
			List<BienTheDealChinh> bienTheDealChinh1 = bienTheDealChinhService
					.getBienTheDealChinhOfSanPham(List.of(item1.getIdBienThe()));
			if (bienTheDealChinh1 != null && !bienTheDealChinh1.isEmpty()) {
				Deal deal = bienTheDealChinh1.get(0).getDeal();
				List<BienTheDealPhu> bienTheDealPhuList = deal.getBienTheDealPhu();
				for (BienTheDealPhu dealPhu : bienTheDealPhuList) {
					if (dealPhu.getBienThe().getId() == item1.getIdBienThe()) {
						giam1 = dealPhu.isLaPhanTram() ? bienThe1.getGia() * (dealPhu.getGiaTriGiam() / 100)
								: dealPhu.getGiaTriGiam();
						break;
					}
				}
			}

			// Tìm deal phụ áp dụng cho biến thể 2
			float giam2 = 0f;
			List<BienTheDealChinh> bienTheDealChinh2 = bienTheDealChinhService
					.getBienTheDealChinhOfSanPham(List.of(item2.getIdBienThe()));
			if (bienTheDealChinh2 != null && !bienTheDealChinh2.isEmpty()) {
				Deal deal = bienTheDealChinh2.get(0).getDeal();
				List<BienTheDealPhu> bienTheDealPhuList = deal.getBienTheDealPhu();
				for (BienTheDealPhu dealPhu : bienTheDealPhuList) {
					if (dealPhu.getBienThe().getId() == item2.getIdBienThe()) {
						giam2 = dealPhu.isLaPhanTram() ? bienThe2.getGia() * (dealPhu.getGiaTriGiam() / 100)
								: dealPhu.getGiaTriGiam();
						break;
					}
				}
			}

			return Float.compare(giam2, giam1); // Sắp xếp giảm dần theo giá trị giảm thực tế
		}).collect(Collectors.toList());
		SanPhamService sanPhamService = ServiceLocator.getBean(SanPhamService.class);
		// Kiểm tra tồn kho
		for (CartItem item : danhSachBienTheSapXep) {
			BienThe bienThe = bienTheSerVice.getById(item.getIdBienThe());
			if (bienThe == null) {
				throw new GeneralException("Biến thể không tồn tại: " + item.getIdBienThe(), HttpStatus.BAD_REQUEST);
			}
			long soLuongTonKho = bienThe.getSoLuongKho();
			int soLuongHaoHut = sanPhamService.soLuongHaoHienGio(bienThe);
			if (item.getSoLuong() > (soLuongTonKho - soLuongHaoHut)) {

				item.setSoLuong((int) (soLuongTonKho - soLuongHaoHut));
				throw new ErrorData("Số lượng đặt của phân loại: "
						+ (bienThe.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : bienThe.getTen())
						+ " không được vượt quá: " + (soLuongTonKho - soLuongHaoHut), HttpStatus.INTERNAL_SERVER_ERROR,
						danhSachBienThe);
			}

		}

		// Tính số lượng giới hạn cho mỗi deal
		List<FilterDeal> dealApDung = danhSachBienTheSapXep.stream().map(dm -> {
			List<BienTheDealChinh> bienTheDealChinh = bienTheDealChinhService
					.getBienTheDealChinhOfSanPham(List.of(dm.getIdBienThe()));
			FilterDeal filterDeal = new FilterDeal();
			if (bienTheDealChinh != null && !bienTheDealChinh.isEmpty()) {
				filterDeal.setD(bienTheDealChinh.get(0).getDeal());
				filterDeal.setSoLuong(dm.getSoLuong() / bienTheDealChinh.get(0).getSoLuongTu());
			}
			return filterDeal;
		}).filter(l -> l.getSoLuong() > 0)
				.collect(Collectors.collectingAndThen(
						Collectors.groupingBy(FilterDeal::getD, Collectors.summingInt(FilterDeal::getSoLuong)),
						map -> map.entrySet().stream().map(entry -> {
							Deal deal = entry.getKey();
							int soLuongTongHop = entry.getValue();
							int soLuongConLai = deal.getSoLuongGioiHan() - deal.getSoLuongDaDung();
							FilterDeal filterDeal = new FilterDeal();
							filterDeal.setD(deal);
							filterDeal.setSoLuong(Math.min(soLuongTongHop, soLuongConLai));
							return filterDeal;
						}).filter(fd -> fd.getSoLuong() > 0).collect(Collectors.toList())));

		// Xử lý giỏ hàng cuối
		List<ParentCartLast> gioHangCuoi = danhSachBienTheSapXep.stream().map(m -> {
			BienThe d = bienTheSerVice.getById(m.getIdBienThe());
			if (d == null) {
				throw new GeneralException("Biến thể không tồn tại: " + m.getIdBienThe(), HttpStatus.BAD_REQUEST);
			}
			ParentCartLast parentCartLast = new ParentCartLast();
			parentCartLast.setTenBienThe((d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : d.getTen()));
			parentCartLast.setIdBienThe(d.getId());
			float thueVAT = d.getSanPham().getThuevat();
			parentCartLast.setGiaGoc(d.getGia() * (1 + thueVAT / 100));
			parentCartLast.setAnhGioiThieu(d.getAnhBia());

			List<BienTheFlashSale> flashSales = bienTheFlashSaleService
					.getBienTheFlahSaleDangApDungOfSanPham(List.of(d.getId()));

			int soLuongConLaiFlash = 0;
			float phanTramGiamFlash = 0;
			Long idFlash = null;
			if (flashSales != null && !flashSales.isEmpty()) {
				LocalTime now = LocalTime.now();
				LocalTime gioBatDau = flashSales.get(0).getFlashSale().getGioBatDau();
				LocalTime gioKetThuc = flashSales.get(0).getFlashSale().getGioKetThuc();
				if (gioBatDau != null && gioKetThuc != null && now.isAfter(gioBatDau) && now.isBefore(gioKetThuc)) {
					soLuongConLaiFlash = Math.max(0,
							flashSales.get(0).getSoLuongGioiHan() - flashSales.get(0).getSoLuongDaDung());
					phanTramGiamFlash = flashSales.get(0).getGiaTriGiam();
					idFlash = flashSales.get(0).getFlashSale().getId();
				}
			}

			int soLuongTrongGio = m.getSoLuong();
			long soLuongTonKho = d.getSoLuongKho();
			if (soLuongTrongGio > soLuongTonKho) {
				throw new GeneralException(
						"Số lượng đặt của phân loại: " + d.getTen() + " không được vượt quá: " + d.getSoLuongKho(),
						HttpStatus.BAD_REQUEST);
			}

			int soLuongDuocFlash = Math.min(soLuongTrongGio, soLuongConLaiFlash);
			int soLuongKhongFlash = soLuongTrongGio - soLuongDuocFlash;

			if (soLuongDuocFlash > 0) {
				CartItemLast itemFlash = new CartItemLast();
				float giaSauGiamFlash = d.getGia() * (1 - phanTramGiamFlash / 100);
				itemFlash.setGiaGiam(giaSauGiamFlash * (1 + thueVAT / 100));
				itemFlash.setSoLuong(soLuongDuocFlash);
				itemFlash.setIdFlashsale(idFlash);
				itemFlash.setPhanTramFlashsale(phanTramGiamFlash);
				parentCartLast.addItemCart(itemFlash);
			}

			if (soLuongKhongFlash > 0) {
				CartItemLast itemNormal = new CartItemLast();
				itemNormal.setGiaGiam(d.getGia() * (1 + thueVAT / 100));
				itemNormal.setSoLuong(soLuongKhongFlash);
				itemNormal.setIdFlashsale(0L);
				itemNormal.setPhanTramFlashsale(0f);
				parentCartLast.addItemCart(itemNormal);
			}

			return parentCartLast;
		}).collect(Collectors.toList());

		// Phân bổ deal
		dealApDung.forEach(deal -> {
			List<BienTheDealPhu> bienTheDealPhuList = deal.getD().getBienTheDealPhu();
			Map<Integer, Float> tempDealMap = new HashMap<>();
			bienTheDealPhuList.forEach(dealPhu -> {
				int idBienThe = dealPhu.getBienThe().getId();
				float giam = dealPhu.getGiaTriGiam();
				tempDealMap.put(idBienThe, giam);
			});

			// Sắp xếp giỏ hàng theo mức giảm giá giảm dần
			gioHangCuoi.sort((p1, p2) -> {
				float giam1 = tempDealMap.getOrDefault(p1.getIdBienThe(), 0f);
				float giam2 = tempDealMap.getOrDefault(p2.getIdBienThe(), 0f);
				return Float.compare(giam2, giam1);
			});

			int soLuongDealConLai = deal.getSoLuong();

			// Duyệt từ đầu danh sách gioHangCuoi (vị trí 0)
			for (int i = 0; i < gioHangCuoi.size() && soLuongDealConLai > 0; i++) {
				ParentCartLast cart = gioHangCuoi.get(i);
				float phanTramGiam = tempDealMap.getOrDefault(cart.getIdBienThe(), 0f);
				if (phanTramGiam <= 0f)
					continue;

				List<CartItemLast> danhSachItem = cart.getCartItemLasts();

				// Duyệt từ cuối danh sách item (từ dưới lên)
				for (int j = danhSachItem.size() - 1; j >= 0 && soLuongDealConLai > 0; j--) {
					CartItemLast item = danhSachItem.get(j);
					if (item.getPhanTramDealGiam() == 0 && item.getSoLuong() > 0) {
						int soLuongItem = item.getSoLuong();

						// Lấy toiDaTrenDonVi từ dealPhu tương ứng
						int toiDaTrenDonVi = bienTheDealPhuList.stream()
								.filter(d -> d.getBienThe().getId() == cart.getIdBienThe()).findFirst()
								.map(BienTheDealPhu::getToiDaTrenDonVi).orElse(1); // mặc định 1 nếu không cấu hình

						// Tính số sản phẩm được áp dụng deal
						int maxSanPhamApDung = soLuongDealConLai * toiDaTrenDonVi;
						int soLuongApDung = Math.min(soLuongItem, maxSanPhamApDung);

						// Tính số lượt deal thật sự đã dùng
						int soLuongDealSuDung = (int) Math.ceil((double) soLuongApDung / toiDaTrenDonVi);

						if (soLuongApDung > 0) {
							if (soLuongItem == soLuongApDung) {
								// Gán trực tiếp nếu vừa đủ
								item.setIdDeal(deal.getD().getId());
								item.setPhanTramDealGiam(phanTramGiam);
								item.setTenDeal(deal.getD().getTenChuongTrinh());
							} else {
								// Tách thành 2 CartItemLast
								CartItemLast itemCoDeal = new CartItemLast();
								itemCoDeal.setSoLuong(soLuongApDung);
								itemCoDeal.setIdDeal(deal.getD().getId());
								itemCoDeal.setPhanTramDealGiam(phanTramGiam);
								itemCoDeal.setTenDeal(deal.getD().getTenChuongTrinh());
								itemCoDeal.setIdFlashsale(item.getIdFlashsale());
								itemCoDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
								itemCoDeal.setGiaGiam(item.getGiaGiam());

								CartItemLast itemKhongDeal = new CartItemLast();
								itemKhongDeal.setSoLuong(soLuongItem - soLuongApDung);
								itemKhongDeal.setIdDeal(0);
								itemKhongDeal.setPhanTramDealGiam(0);
								itemKhongDeal.setTenDeal("");
								itemKhongDeal.setIdFlashsale(item.getIdFlashsale());
								itemKhongDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
								itemKhongDeal.setGiaGiam(item.getGiaGiam());

								danhSachItem.remove(j);
								danhSachItem.add(j, itemKhongDeal);
								danhSachItem.add(j, itemCoDeal);
							}

							// Trừ số lượt deal đã dùng
							soLuongDealConLai -= soLuongDealSuDung;

						}
					}
				}
			}
		});

		// Tính toán giá cuối cùng
		gioHangCuoi.forEach(parentCartLast -> {
			BienThe d = bienTheSerVice.getById(parentCartLast.getIdBienThe());
			float thueVAT = d.getSanPham().getThuevat();
			System.out.println("thuws VA sản phẩm là");
			List<CartItemLast> updatedItems = new ArrayList<>();
			float totalAmount = 0f;
			int totalQuantity = 0;
			float totalDiscount = 0f;

			for (CartItemLast item : parentCartLast.getCartItemLasts()) {
				int sl = item.getSoLuong();
				if (sl <= 0)
					continue;

				// Giá gốc chưa bao gồm VAT
				float giaBanDau = d.getGia();
				// Áp dụng flash sale trước
				float giaSauFlash = giaBanDau * (1 - item.getPhanTramFlashsale() / 100);
				// Áp dụng deal lên giá sau flash sale
				float giaSauDeal = giaSauFlash * (1 - item.getPhanTramDealGiam() / 100);
				// Áp VAT lên giá sau cùng
				System.out.println("GIÁ TRỊ THUẾ VAT :" + thueVAT);
				System.out.println("GIÁ SAU DEAL: " + giaSauDeal);
				float giaSauThue = giaSauDeal * (1 + thueVAT / 100);

				item.setGiaGiam(giaSauThue);
				totalAmount += giaSauThue * sl;
				totalQuantity += sl;
				totalDiscount += (giaBanDau * (1 + thueVAT / 100) - giaSauThue) * sl;

				updatedItems.add(item);
			}

			parentCartLast.setCartItemLasts(updatedItems);
			parentCartLast.setTongTien(totalAmount);
			parentCartLast.setTongSoLuong(totalQuantity);
			parentCartLast.setTongGiaTriGiam((d.getGia() * (1 + thueVAT / 100)) * totalQuantity - totalAmount);
		});

		return gioHangCuoi;
	}

	@Transactional
	public void DatHangOnline(List<CartItem> danhSachBienThe) {

		HoaDonOnline h = new HoaDonOnline();
		KhachHang khachHang = (KhachHang) nguoiDungService.getById(jwtService.getIdUser());
		// thêm đóng gói cho nó
		DongHop dongHop = dongHopService.getDongHopById(1);
		h.setDongHop(dongHop);
		h.setKhachHang(khachHang);
		h.setNgayLap(LocalDateTime.now());
		hoaDonOnlineRepository.save(h);
		h.setDiaChi(danhSachBienThe.get(0).getDiaChi());
		List<ParentCartLast> parentCartLasts = calculate(danhSachBienThe);
		danhSachBienThe.stream().forEach(k -> {

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
			// thêm thông báo nếu hàng bằng 0
			if ((bienThe.getSoLuongKho() - pr.getTongSoLuong()) == 0) {

			}
			bienTheSerVice.save(bienThe);

		}

		trangThaiHoaDonService.Save(h, trangThaiService.getById(3), "");
		h.setTrangThai(trangThaiService.getById(3));
		float lamTron = (float) (Math.ceil(tongTien.get() / 100) * 100);
		h.setTongTien(lamTron);
//		System.out.println("số lượng chi tiết hóa đơn"+h.getChiTietHoaDons().size());
//		h.setTongKhoiLuong(ghnOrder.getWeight());
		h.setDongHop(dongHopService.getToiUu());

//		hoaDonOnlineRepository.save(h);
	}

	public Map<String, Object> viewHoaDonUpdate(List<CartItem> danhSachBienThe, long id, boolean kt) {
		List<ParentCartLast> gioHangCuoi = calculateAlReady(danhSachBienThe, id, kt);
		Map<Long, Map<String, Object>> m = new HashMap<Long, Map<String, Object>>();
		HoaDon hoaDon = hoaDonSerVice.getById(id);
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
		result.put("canUpdateCustomer", true);
		result.put("huyDonHangCustomer", true);
		result.put("hoanHangCustomer", true);
		if (hoaDon instanceof HoaDonTaiQuay || hoaDon.getTrangThai().getId() != 3) {

			result.put("canUpdateCustomer", false);
			result.put("huyDonHangCustomer", false);
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
		result.put("data", m.values().stream().collect(Collectors.toList()));
		return result;
	}

	public Map<String, Object> viewHoaDonUpdateEmployee(List<CartItem> danhSachBienThe, long id, boolean kt) {
		List<ParentCartLast> gioHangCuoi = calculateAlReady(danhSachBienThe, id, kt);
		Map<Long, Map<String, Object>> m = new HashMap<Long, Map<String, Object>>();
		HoaDon hoaDon = hoaDonSerVice.getById(id);
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
		result.put("thanhToan", hoaDon.isDaThanhTona());
		result.put("giamGia", giamGia);
		result.put("canUpdateCustomer", false);
		result.put("huyDonHangCustomer", false);
		result.put("hoanHangCustomer", false);
		System.out.println("TRANG THAI: " + hoaDon.getTrangThai().getId());
		if (hoaDon.getTrangThai().getId() == 3) {
			result.put("canUpdateCustomer", true);
		}
		if (hoaDon.getTrangThai().getId() == 3 || hoaDon.getTrangThai().getId() == 2) {
			result.put("huyDonHangCustomer", true);
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
		result.put("data", m.values().stream().collect(Collectors.toList()));
		return result;
	}

	public Map<String, Object> viewHoaDon(List<CartItem> danhSachBienThe) {
		List<ParentCartLast> gioHangCuoi = calculate(danhSachBienThe);
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
		result.put("data", m.values().stream().collect(Collectors.toList()));
		return result;
	}

	public Map<String, Object> getUpdateViewLast(List<CartItem> danhSachBienThe, long id) {
		Map<String, Object> resultTrue = viewHoaDonUpdate(danhSachBienThe, id, true);
		Map<String, Object> resultFalse = viewHoaDonUpdate(danhSachBienThe, id, false);

		float tongTienTrue = (float) resultTrue.get("tongTien");
		float tongTienFalse = (float) resultFalse.get("tongTien");
		System.out.println("tổng tiền cũ: " + tongTienTrue);
		System.out.println("tổng tiền mới " + tongTienFalse);
		return tongTienFalse < tongTienTrue ? resultFalse : resultTrue;
	}

	public Map<String, Object> getUpdateViewLastEmployee(List<CartItem> danhSachBienThe, long id) {
		Map<String, Object> resultTrue = viewHoaDonUpdateEmployee(danhSachBienThe, id, true);
		Map<String, Object> resultFalse = viewHoaDonUpdateEmployee(danhSachBienThe, id, false);

		float tongTienTrue = (float) resultTrue.get("tongTien");
		float tongTienFalse = (float) resultFalse.get("tongTien");
		System.out.println("tổng tiền cũ: " + tongTienTrue);
		System.out.println("tổng tiền mới " + tongTienFalse);
		return tongTienFalse < tongTienTrue ? resultFalse : resultTrue;
	}

	public boolean kt(List<CartItem> danhSachBienThe, long id) {
		Map<String, Object> resultTrue = viewHoaDonUpdate(danhSachBienThe, id, true);
		Map<String, Object> resultFalse = viewHoaDonUpdate(danhSachBienThe, id, false);

		float tongTienTrue = (float) resultTrue.get("tongTien");
		float tongTienFalse = (float) resultFalse.get("tongTien");
		return tongTienFalse < tongTienTrue ? false : true;
	}

	public Map<String, Object> viewHoaDonAlReady(long idHoaDon) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		if (hoaDon == null || (nguoiDung instanceof KhachHang && hoaDon.getKhachHang() == null)
				|| (nguoiDung instanceof KhachHang && hoaDon.getKhachHang().getId() != jwtService.getIdUser())) {
			throw new GeneralException("Bạn không có quyền truy cập vào hóa đơn này", HttpStatus.NOT_FOUND);
		}
		List<ParentCartLast> gioHangCuoi = mapToHoaDonConver(idHoaDon);
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
		result.put("canUpdateCustomer", true);
		result.put("huyDonHangCustomer", true);
		result.put("hoanHangCustomer", true);
		result.put("status", hoaDon.getTrangThai().getId());
		if (((HoaDonOnline) hoaDon).getThoiGianDuKienGiao() != null) {
			result.put("thoiGianDuKienGiao", ((HoaDonOnline) hoaDon).getThoiGianDuKienGiao());
		}
		result.put("phiGiao", ((HoaDonOnline) hoaDon).getTongPhiGHN());
		if (hoaDon instanceof HoaDonTaiQuay || hoaDon.getTrangThai().getId() != 3) {

			result.put("canUpdateCustomer", false);
			result.put("huyDonHangCustomer", false);
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
		String diaChi = hoaDon.getDiaChi().substring(hoaDon.getDiaChi().lastIndexOf(".") + 1);

		String[] codeStr = diaChi.split(" ");
		int[] codeInt = Arrays.stream(codeStr).mapToInt(Integer::parseInt).toArray();
		result.put("diaChi", hoaDon.getDiaChi().substring(0, hoaDon.getDiaChi().lastIndexOf(".")));
		result.put("code", codeInt);
		result.put("data", m.values().stream().collect(Collectors.toList()));
		return result;
	}

	public Map<String, Object> viewHoaDonAlReadyEmployee(long idHoaDon) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());

		List<ParentCartLast> gioHangCuoi = mapToHoaDonConver(idHoaDon);
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
		result.put("thanhToan", hoaDon.isDaThanhTona());
		result.put("ngayLap", hoaDon.getNgayLap());
		result.put("canUpdateCustomer", false);
		result.put("huyDonHangCustomer", false);
		result.put("hoanHangCustomer", true);
		if (hoaDon.getTrangThai().getId() == 7) {
			result.put("coTheXacNhanHoanHang", true);
		} else {
			result.put("coTheXacNhanHoanHang", false);
		}
		if (hoaDon.getTrangThai().getId() == 3) {
			result.put("canUpdateCustomer", true);
		}
		if (hoaDon.getTrangThai().getId() == 3 || hoaDon.getTrangThai().getId() == 13
				|| hoaDon.getTrangThai().getId() == 2) {
			result.put("huyDonHangCustomer", true);
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
		String diaChi = hoaDon.getDiaChi().substring(hoaDon.getDiaChi().lastIndexOf(".") + 1);
		System.out.println(diaChi);
		String[] codeStr = diaChi.split(" ");
		int[] codeInt = Arrays.stream(codeStr).mapToInt(Integer::parseInt).toArray();

		result.put("diaChi", hoaDon.getDiaChi().substring(0, hoaDon.getDiaChi().lastIndexOf(".")));
		result.put("code", codeInt);
		result.put("data", m.values().stream().collect(Collectors.toList()));
		result.put("data", m.values().stream().collect(Collectors.toList()));
		return result;
	}

	public List<ParentCartLast> mapToHoaDonConver(long idHoaDon) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
		return hoaDon.getChiTietHoaDons().stream().map((d) -> {
			ThueVATSanPham thueVATSanPham = thueVATSanPhamSerVice.getThueVATAtTime(d.getBienThe().getSanPham().getId(),
					hoaDon.getNgayLap());
			float thueVAT = thueVATSanPham.getTiLe();
			List<ApDungKhuyenMai> apDungKhuyenMais = d.getDanhSachKhuyenMai();
			List<CartItemLast> cartItemLasts = new ArrayList<CartItemLast>();
			int soLuongDealApDung = 0;
			int soLuongFlashApDung = 0;
			float tyLeDealApDung = 0;
			float tyLeFlashApDung = 0;

			Deal deal = new Deal();
			FlashSale flash = new FlashSale();

			// Phân loại khuyến mãi (Deal hoặc FlashSale)
			for (ApDungKhuyenMai apDung : apDungKhuyenMais) {
				if (apDung.getKhuyenMai() instanceof Deal) {
					deal = (Deal) apDung.getKhuyenMai();
					soLuongDealApDung = apDung.getSoLuongApDung();
					tyLeDealApDung = apDung.getTyLeApDung();
				} else if (apDung.getKhuyenMai() instanceof FlashSale) {
					flash = (FlashSale) apDung.getKhuyenMai();
					soLuongFlashApDung = apDung.getSoLuongApDung();
					tyLeFlashApDung = apDung.getTyLeApDung();
				}
			}

			// Xử lý FlashSale
			if (tyLeFlashApDung == 0) {
				CartItemLast cc = new CartItemLast();
				cc.setIdDeal(0);
				cc.setIdFlashsale(0);
				cc.setPhanTramDealGiam(0);
				cc.setPhanTramFlashsale(0);
				cc.setSoLuong(d.getSoLuong());
				cc.setTenDeal(null);
				cartItemLasts.add(cc);
			} else {
				if (d.getSoLuong() >= soLuongFlashApDung) {
					CartItemLast cc = new CartItemLast();
					cc.setIdDeal(0);
					cc.setIdFlashsale(flash.getId());
					cc.setPhanTramDealGiam(0);
					cc.setPhanTramFlashsale(tyLeFlashApDung);
					cc.setSoLuong(soLuongFlashApDung);
					cc.setTenDeal(null);
					cartItemLasts.add(cc);

					if (d.getSoLuong() > soLuongFlashApDung) {
						CartItemLast cc2 = new CartItemLast();
						cc2.setIdDeal(0);
						cc2.setIdFlashsale(0);
						cc2.setPhanTramDealGiam(0);
						cc2.setPhanTramFlashsale(0);
						cc2.setSoLuong(d.getSoLuong() - soLuongFlashApDung);
						cc2.setTenDeal(null);
						cartItemLasts.add(cc2);
					}
				} else {
					CartItemLast cc = new CartItemLast();
					cc.setIdDeal(0);
					cc.setIdFlashsale(flash.getId());
					cc.setPhanTramDealGiam(0);
					cc.setPhanTramFlashsale(tyLeFlashApDung);
					cc.setSoLuong(d.getSoLuong());
					cc.setTenDeal(null);
					cartItemLasts.add(cc);
				}
			}

			// Xử lý Deal
			if (tyLeDealApDung != 0) {
				List<CartItemLast> tempCartItemLasts = new ArrayList<>(cartItemLasts);
				cartItemLasts.clear();
				int remainingDealQuantity = soLuongDealApDung;

				// Reverse the list to process from bottom to top
				Collections.reverse(tempCartItemLasts);

				for (CartItemLast item : tempCartItemLasts) {
					if (remainingDealQuantity <= 0) {
						cartItemLasts.add(item);
						continue;
					}

					if (item.getSoLuong() <= remainingDealQuantity) {
						item.setIdDeal(deal.getId());
						item.setPhanTramDealGiam(tyLeDealApDung);
						item.setTenDeal(deal.getTenChuongTrinh());
						cartItemLasts.add(item);
						remainingDealQuantity -= item.getSoLuong();
					} else {
						CartItemLast cc1 = new CartItemLast();
						cc1.setIdDeal(deal.getId());
						cc1.setIdFlashsale(item.getIdFlashsale());
						cc1.setPhanTramDealGiam(tyLeDealApDung);
						cc1.setPhanTramFlashsale(item.getPhanTramFlashsale());
						cc1.setSoLuong(remainingDealQuantity);
						cc1.setTenDeal(deal.getTenChuongTrinh());
						cartItemLasts.add(cc1);

						CartItemLast cc2 = new CartItemLast();
						cc2.setIdDeal(0);
						cc2.setIdFlashsale(item.getIdFlashsale());
						cc2.setPhanTramDealGiam(0);
						cc2.setPhanTramFlashsale(item.getPhanTramFlashsale());
						cc2.setSoLuong(item.getSoLuong() - remainingDealQuantity);
						cc2.setTenDeal(null);
						cartItemLasts.add(cc2);

						remainingDealQuantity = 0;
					}
				}
				// Reverse back to maintain original order in the result
				Collections.reverse(cartItemLasts);
			}

			AtomicReference<Float> tongTien = new AtomicReference<Float>(0f);

			cartItemLasts.forEach(df -> {
				float giaFlash = df.getPhanTramFlashsale();
				float giaDeal = df.getPhanTramDealGiam();
				float giaGoc = d.getDonGia();

				float giaSauFlash = giaGoc * (1 - giaFlash / 100f);
				float giaSauDeal = giaSauFlash * (1 - giaDeal / 100f);
				float giaSauVAT = giaSauDeal * (1 + thueVAT / 100f);
				df.setGiaGiam(giaSauVAT);
				tongTien.updateAndGet(v -> v + giaSauVAT * df.getSoLuong());
			});

			ParentCartLast pr = new ParentCartLast();
			pr.setGiaGoc(d.getDonGia() * (1 + thueVAT / 100f));
			pr.setTenBienThe((d.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định"
					: d.getBienThe().getTen()));
			pr.setAnhGioiThieu(d.getBienThe().getAnhBia());
			pr.setIdBienThe(d.getBienThe().getId());
			pr.setTongTien(tongTien.get());
			pr.setTongSoLuong(d.getSoLuong());
			pr.setTongGiaTriGiam((d.getDonGia() * (1 + thueVAT / 100f) * d.getSoLuong()) - tongTien.get());
			pr.setCartItemLasts(cartItemLasts);

			return pr;
		}).collect(Collectors.toList());
	}

	@Transactional
	public void removeHoaDonByKhachHang(long id, int status, String re) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());

		if (status == 4) {
			if (hoaDon.getTrangThai().getId() < 2 && nguoiDung instanceof KhachHang) {
				throw new GeneralException(
						"Bạn chỉ có thể hủy đơn khi đơn hàng chưa xác nhận, quý khách vui lòng liên hệ nhân viên để được hỗ trợ",
						HttpStatus.BAD_REQUEST);
			}
			TrangThai trangThai = trangThaiService.getById(4);
			hoaDonSerVice.huyDonTaiQuay(id);
			trangThaiHoaDonService.Save(hoaDon, trangThai, "");
			hoaDon.setTrangThai(trangThai);
			hoaDonSerVice.Update(hoaDon);
		} else if (status == 6) {
			if (hoaDon.getTrangThai().getId() != 1) {
				throw new GeneralException("Đơn hàng chưa thành công, bạn không thể yêu cầu hoàn đơn",
						HttpStatus.BAD_REQUEST);

			}
			List<TrangThaiHoaDon> t = hoaDon.getTrangThaiHoaDon().stream().filter(d -> d.getTrangThai().getId() == 1)
					.collect(Collectors.toList());
			if (t == null || t.size() == 0) {
				throw new GeneralException(
						"Bạn chưa được phép tạo yêu cầu hoàn đơn. Mọi thắc mắc vui lòng liên hệ nhân viên để được hỗ trợ",
						HttpStatus.BAD_REQUEST);

			}
			Duration d = Duration.between(t.get(0).getThoiDiem(), LocalDateTime.now());
			if (d.getSeconds() > 43200) {
				throw new GeneralException("Bạn không thể hoàn hàng do đã quá thời hạn 12 tiếng",
						HttpStatus.BAD_REQUEST);
			}

			TrangThai trangThai = trangThaiService.getById(6);
			trangThaiHoaDonService.Save(hoaDon, trangThai, re);
			hoaDon.setTrangThai(trangThai);
			hoaDonSerVice.Update(hoaDon);
		} else {
			throw new GeneralException(
					"Hiện tại đơn hàng không thể điều chỉnh trạng thái, quý khách vui lòng liên hệ nhân viên để được hỗ trợ",
					HttpStatus.BAD_REQUEST);
		}

	}

	@Transactional
	public Map<String, Object> removeHoaDonByNhanVien(long id, boolean traluon) {
		Map<String, Object> result = new HashMap<>();
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		if (hoaDon.getTrangThai().getId() == 13) {
			System.out.println("đi vô đây");
			Boolean re = ghnService.cancelOrder(((HoaDonOnline) hoaDon).getGhnCode());
			if (re == false) {
				throw new GeneralException("Hủy vận đơn thất bại, thử lại hoặc thao tác trực tiếp trên ứng dụng GHN",
						HttpStatus.BAD_REQUEST);
			}
		}
		if (hoaDon.getTrangThai().getId() == 3 || hoaDon.getTrangThai().getId() == 2
				|| hoaDon.getTrangThai().getId() == 13 || hoaDon.getTrangThai().getId() == 1) {

			hoaDonSerVice.HuyDon3(id);
			long tienGoc = (long) hoaDon.getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000;

			if (hoaDon.isDaThanhTona() && traluon) {

				hoaDon.setDaHoanHang(true);
				hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
				try {
					zaloPayService.refund(hoaDon.getHD_zpTrans_Id() + "", soTienRefound,
							"Hoàn tiền đơn hàng- " + hoaDon.getId(), hoaDon.getRefund(), null);
					result.put("mess", "Hủy đơn thành công");
					result.put("status", HttpStatus.OK);
					return result;
				} catch (Exception e) {
					e.printStackTrace();
					if (hoaDon.getTrangThai().getId() == 13) {
						hoaDon.setDaHoanHang(false);
						hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
						result.put("mess", "Hóa đơn đã hủy nhưng chưa hoàn trả tiền vui lòng vào đơn hủy");
						result.put("status", HttpStatus.BAD_REQUEST);
						return result;
					} else {
						throw new GeneralException("Hủy đơn hàng thất bại, vui lòng thử lại", HttpStatus.BAD_REQUEST);
					}
				}
			} else {
				result.put("mess", "Hủy đơn thành công");
				result.put("status", HttpStatus.OK);
				return result;
			}
		} else {
			throw new GeneralException("Hóa đơn này không thể chỉnh được nữa", HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional
	public void XacNhanHoanDonByNhanVien(long id, boolean traluon, String lyDo, boolean thanhCong) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		if (hoaDon.getTrangThai().getId() == 7) {
			TrangThai t;
			if (thanhCong) {
				t = trangThaiService.getById(8);
			} else {
				t = trangThaiService.getById(10);
			}
			if (thanhCong == false && lyDo.equals("")) {
				throw new GeneralException("Vui lòng cung cấp lý do hoàn không thành công", HttpStatus.BAD_REQUEST);
			}
			trangThaiHoaDonService.Save(hoaDon, t, lyDo);
			long tienGoc = (long) hoaDon.getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000;

			hoaDon.setTrangThai(t);
			hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
			if (hoaDon.isDaThanhTona() && traluon == true && thanhCong == true) {
				try {
					hoaDon.setDaHoanHang(true);
					hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
					Map<String, String> map = momoService.refundOrder(hoaDon.getId() + "", soTienRefound,
							hoaDon.getTransId() + "", "Hoàn tiền hóa đơn - " + hoaDon.getId() + " - SKINLY");
				} catch (Exception e) {
					throw new GeneralException("Hoàn tiền cho khách thất bại, vui lòng thử lại",
							HttpStatus.BAD_REQUEST);
				}
			}
		} else {
			throw new GeneralException("Hóa đơn này không ở trạng thái có thể xác nhận hoàn", HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional
	public void xacNhanHoanDon(long id) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		TrangThai trangThai = trangThaiService.getById(7);
		trangThaiHoaDonService.Save(hoaDon, trangThai, "");
		hoaDonSerVice.huyDonTaiQuay(id);
		hoaDon.setTrangThai(trangThai);
		hoaDonSerVice.Update(hoaDon);
	}

	public FilterDeal getDealChinhByCondition(CartItem cartItems, long idHoadDon, boolean uuTienCu) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoadDon);
		List<ApDungKhuyenMai> apDungKhuyenMais = hoaDon.getChiTietHoaDons().stream()
				.flatMap(d -> d.getDanhSachKhuyenMai().stream())
				.filter(m -> (m.getBienThe().getId() == cartItems.getIdBienThe() && (m.getKhuyenMai() instanceof Deal)
						&& m.isDealPhu() == false))
				.collect(Collectors.toList());
		List<BienTheDealChinh> bienTheDealChinhs = bienTheDealChinhService
				.getBienTheDealChinhOfSanPham(Arrays.asList(cartItems.getIdBienThe()));
		FilterDeal filterDeal = new FilterDeal();

		if (!bienTheDealChinhs.isEmpty() && !apDungKhuyenMais.isEmpty()) {
			if (bienTheDealChinhs.get(0).getDeal().getId() != apDungKhuyenMais.get(0).getKhuyenMai().getId()) {
				if (uuTienCu == true) {
					Deal apDung = (Deal) apDungKhuyenMais.get(0).getKhuyenMai();
					filterDeal.setD(apDung);
					filterDeal.setSoLuong(cartItems.getSoLuong() / apDungKhuyenMais.get(0).getSoLuongApDung());

					return filterDeal;
				} else {
					Deal apDung = (Deal) bienTheDealChinhs.get(0).getDeal();
					filterDeal.setD(apDung);
					filterDeal.setSoLuong(cartItems.getSoLuong() / bienTheDealChinhs.get(0).getSoLuongTu());

					return filterDeal;
				}
			} else {
				Deal apDung = (Deal) apDungKhuyenMais.get(0).getKhuyenMai();
				filterDeal.setD(apDung);
				filterDeal.setSoLuong(cartItems.getSoLuong() / apDungKhuyenMais.get(0).getSoLuongApDung());
				return filterDeal;
			}
		} else {
			if (bienTheDealChinhs.isEmpty() && apDungKhuyenMais.isEmpty()) {
				return null;
			} else if (bienTheDealChinhs.isEmpty()) {
				Deal apDung = (Deal) apDungKhuyenMais.get(0).getKhuyenMai();
				filterDeal.setD(apDung);
				filterDeal.setSoLuong(cartItems.getSoLuong() / apDungKhuyenMais.get(0).getSoLuongApDung());
				return filterDeal;
			} else {
				Deal apDung = (Deal) bienTheDealChinhs.get(0).getDeal();
				filterDeal.setD(apDung);
				filterDeal.setSoLuong(cartItems.getSoLuong() / bienTheDealChinhs.get(0).getSoLuongTu());
				return filterDeal;
			}
		}
	}

	public LuotDungFlash getFlashByCondition(int idBienThe, long idHoadDon, boolean uuTienCu) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoadDon);
		List<ApDungKhuyenMai> apDungKhuyenMais = hoaDon.getChiTietHoaDons().stream()
				.flatMap(d -> d.getDanhSachKhuyenMai().stream()).filter(m -> (m.getBienThe().getId() == idBienThe
						&& (m.getKhuyenMai() instanceof FlashSale) && m.isDealPhu() == true))
				.collect(Collectors.toList());
		List<BienTheFlashSale> flashSales = bienTheFlashSaleService
				.getBienTheFlahSaleDangApDungOfSanPham(List.of(idBienThe));
		LuotDungFlash l = new LuotDungFlash();
		if (!apDungKhuyenMais.isEmpty() && flashSales != null && !flashSales.isEmpty()) {
			if (flashSales.get(0).getFlashSale().getId() != apDungKhuyenMais.get(0).getKhuyenMai().getId()) {
				if (uuTienCu == false) {
					if (TimeValidator.isTrongKhungGio(flashSales.get(0).getFlashSale().getGioBatDau(),
							flashSales.get(0).getFlashSale().getGioKetThuc())) {
						l.setSoLuong(flashSales.get(0).getSoLuongGioiHan() - flashSales.get(0).getSoLuongDaDung());
						l.setTyLe(flashSales.get(0).getGiaTriGiam());
						l.setFlashSale(flashSales.get(0).getFlashSale());
						return l;
					} else {
						return null;
					}
				} else {
					FlashSale fl = (FlashSale) apDungKhuyenMais.get(0).getKhuyenMai();
					l.setSoLuong(apDungKhuyenMais.get(0).getSoLuongApDung());
					l.setTyLe(apDungKhuyenMais.get(0).getTyLeApDung());
					l.setFlashSale((FlashSale) apDungKhuyenMais.get(0).getKhuyenMai());
					return l;
				}
			} else {
				FlashSale fl = (FlashSale) apDungKhuyenMais.get(0).getKhuyenMai();
				List<BienTheFlashSale> bienTheFlashSale = fl.getBienTheFlashSale().stream()
						.filter(d -> (d.getBienThe().getId() == idBienThe && d.isConSuDung() == true))
						.collect(Collectors.toList());
				LocalDateTime tgbd = fl.getThoiGianApDung();
				LocalDateTime tgkt = fl.getThoiGianNgung();
				LocalTime bd = fl.getGioBatDau();
				LocalTime kt = fl.getGioKetThuc();

				if (fl.isConSuDung() == true
						&& (!LocalDateTime.now().isBefore(tgbd) && !LocalDateTime.now().isAfter(tgkt))
						&& TimeValidator.isTrongKhungGio(bd, kt) && flashSales != null && !bienTheFlashSale.isEmpty()) {

					l.setSoLuong(apDungKhuyenMais.get(0).getSoLuongApDung()
							+ bienTheFlashSale.get(0).getSoLuongGioiHan() - bienTheFlashSale.get(0).getSoLuongDaDung());
					l.setTyLe(apDungKhuyenMais.get(0).getTyLeApDung());
					l.setFlashSale((FlashSale) apDungKhuyenMais.get(0).getKhuyenMai());
					return l;
				} else {
					l.setSoLuong(apDungKhuyenMais.get(0).getSoLuongApDung());
					l.setTyLe(apDungKhuyenMais.get(0).getTyLeApDung());
					l.setFlashSale((FlashSale) apDungKhuyenMais.get(0).getKhuyenMai());
					return l;
				}
			}
		} else {
			if (flashSales.isEmpty() && apDungKhuyenMais.isEmpty()) {
				return null;
			} else if (flashSales != null && flashSales.isEmpty() == false) {
				if (TimeValidator.isTrongKhungGio(flashSales.get(0).getFlashSale().getGioBatDau(),
						flashSales.get(0).getFlashSale().getGioKetThuc())) {
					l.setSoLuong(flashSales.get(0).getSoLuongGioiHan() - flashSales.get(0).getSoLuongDaDung());
					l.setTyLe(flashSales.get(0).getGiaTriGiam());
					l.setFlashSale(flashSales.get(0).getFlashSale());
					return l;
				} else {
					return null;
				}
			} else {
				FlashSale fl = (FlashSale) apDungKhuyenMais.get(0).getKhuyenMai();
				List<BienTheFlashSale> bienTheFlashSale = fl.getBienTheFlashSale().stream()
						.filter(d -> (d.getBienThe().getId() == idBienThe && d.isConSuDung() == true))
						.collect(Collectors.toList());
				LocalDateTime tgbd = fl.getThoiGianApDung();
				LocalDateTime tgkt = fl.getThoiGianNgung();
				LocalTime bd = fl.getGioBatDau();
				LocalTime kt = fl.getGioKetThuc();
				if (fl.isConSuDung() == true
						&& (!LocalDateTime.now().isBefore(tgbd) && !LocalDateTime.now().isAfter(tgkt))
						&& TimeValidator.isTrongKhungGio(bd, kt) && flashSales != null && !bienTheFlashSale.isEmpty()) {

					l.setSoLuong(apDungKhuyenMais.get(0).getSoLuongApDung()
							+ bienTheFlashSale.get(0).getSoLuongGioiHan() - bienTheFlashSale.get(0).getSoLuongDaDung());
					l.setTyLe(apDungKhuyenMais.get(0).getTyLeApDung());
					l.setFlashSale((FlashSale) apDungKhuyenMais.get(0).getKhuyenMai());
					return l;
				} else {
					l.setSoLuong(apDungKhuyenMais.get(0).getSoLuongApDung());
					l.setTyLe(apDungKhuyenMais.get(0).getTyLeApDung());
					l.setFlashSale((FlashSale) apDungKhuyenMais.get(0).getKhuyenMai());
					return l;
				}
			}
		}
	}

	public int getDealPhuByCondition(int idBienThe, long idHoadDon, Deal deal) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoadDon);
		List<ApDungKhuyenMai> apDungKhuyenMais = hoaDon.getChiTietHoaDons().stream()
				.flatMap(d -> d.getDanhSachKhuyenMai().stream())
				.filter(m -> (m.getBienThe().getId() == idBienThe && (m.getKhuyenMai() instanceof Deal)
						&& m.isDealPhu() == true && m.getKhuyenMai().getId() == deal.getId()))
				.collect(Collectors.toList());
		List<BienTheDealPhu> p = deal.getBienTheDealPhu().stream().filter(d -> d.getBienThe().getId() == idBienThe)
				.collect(Collectors.toList());
		if (deal.isConSuDung() && (deal.getThoiGianApDung() != null && deal.getThoiGianNgung() != null
				&& !LocalDateTime.now().isBefore(deal.getThoiGianApDung())
				&& !LocalDateTime.now().isAfter(deal.getThoiGianNgung()))) {
			if (p.isEmpty() == false) {
				if (p.get(0).isConSuDung()) {
					return Integer.MAX_VALUE;
				} else {
					if (apDungKhuyenMais.isEmpty() == false) {
						return apDungKhuyenMais.get(0).getSoLuongApDung();
					} else {
						return 0;
					}
				}
			} else {
				return 0;
			}
		} else {
			if (apDungKhuyenMais.isEmpty() == false) {
				return apDungKhuyenMais.get(0).getSoLuongApDung();
			} else {
				return 0;
			}
		}
	}

	public List<ParentCartLast> calculateAlReady(List<CartItem> danhSachBienThe, long idHoaDon, boolean uuTienCu) {
		// Sắp xếp danh sách biến thể theo giá giảm dần
		List<CartItem> danhSachBienTheSapXep = danhSachBienThe.stream().sorted((item1, item2) -> {
			BienThe bienThe1 = bienTheSerVice.getById(item1.getIdBienThe());
			BienThe bienThe2 = bienTheSerVice.getById(item2.getIdBienThe());
			return Double.compare(bienThe1.getGia(), bienThe2.getGia());
		}).collect(Collectors.toList());

		// Kiểm tra tồn kho
		for (CartItem item : danhSachBienTheSapXep) {
			BienThe bienThe = bienTheSerVice.getById(item.getIdBienThe());
			if (bienThe == null) {
				throw new GeneralException("Biến thể không tồn tại: " + item.getIdBienThe(), HttpStatus.BAD_REQUEST);
			}
			long soLuongTonKho = bienThe.getSoLuongKho();
			if (item.getSoLuong() > soLuongTonKho) {
				throw new GeneralException(
						"Số lượng đặt của phân loại: " + bienThe.getTen() + " không được vượt quá: " + soLuongTonKho,
						HttpStatus.BAD_REQUEST);
			}
		}

		// Tính số lượng giới hạn cho mỗi deal
		List<FilterDeal> dealApDung = danhSachBienTheSapXep.stream().map(dm -> {
			FilterDeal filterDeal = getDealChinhByCondition(dm, idHoaDon, uuTienCu);
			if (filterDeal == null) {
				filterDeal = new FilterDeal();
				filterDeal.setSoLuong(0);
			}
			return filterDeal;
		}).filter(l -> l.getSoLuong() > 0)
				.collect(Collectors.collectingAndThen(
						Collectors.groupingBy(FilterDeal::getD, Collectors.summingInt(FilterDeal::getSoLuong)),
						map -> map.entrySet().stream().map(entry -> {
							Deal deal = entry.getKey();
							int soLuongTongHop = entry.getValue();
							int soLuongConLai = deal.getSoLuongGioiHan() - deal.getSoLuongDaDung();
							FilterDeal filterDeal = new FilterDeal();
							filterDeal.setD(deal);
							filterDeal.setSoLuong(Math.min(soLuongTongHop, soLuongConLai));
							return filterDeal;
						}).filter(fd -> fd.getSoLuong() > 0).collect(Collectors.toList())));

		// Xử lý giỏ hàng cuối
		List<ParentCartLast> gioHangCuoi = danhSachBienTheSapXep.stream().map(m -> {
			BienThe d = bienTheSerVice.getById(m.getIdBienThe());
			if (d == null) {
				throw new GeneralException("Biến thể không tồn tại: " + m.getIdBienThe(), HttpStatus.BAD_REQUEST);
			}
			ParentCartLast parentCartLast = new ParentCartLast();
			parentCartLast.setTenBienThe(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : d.getTen());
			parentCartLast.setIdBienThe(d.getId());
			float thueVAT = d.getSanPham().getThuevat();
			parentCartLast.setGiaGoc(d.getGia() * (1 + thueVAT / 100));
			parentCartLast.setAnhGioiThieu(d.getAnhBia());

			// Sử dụng getFlashByCondition thay vì kiểm tra thời gian
			LuotDungFlash flashSaleInfo = getFlashByCondition(d.getId(), idHoaDon, uuTienCu);
			int soLuongConLaiFlash = 0;
			float phanTramGiamFlash = 0;
			Long idFlash = 0L;
			if (flashSaleInfo != null) {
				soLuongConLaiFlash = flashSaleInfo.getSoLuong();
				phanTramGiamFlash = flashSaleInfo.getTyLe();
				idFlash = flashSaleInfo.getFlashSale().getId();
			}
			int soLuongTrongGio = m.getSoLuong();
			long soLuongTonKho = d.getSoLuongKho();
			if (soLuongTrongGio > soLuongTonKho) {
				throw new GeneralException(
						"Số lượng đặt của phân loại: " + d.getTen() + " không được vượt quá: " + soLuongTonKho,
						HttpStatus.BAD_REQUEST);
			}

			List<CartItemLast> items = new ArrayList<>();
			int soLuongDuocFlash = Math.min(soLuongTrongGio, soLuongConLaiFlash);
			int soLuongKhongFlash = soLuongTrongGio - soLuongDuocFlash;

			if (soLuongDuocFlash > 0) {
				CartItemLast itemFlash = new CartItemLast();
				float giaSauGiamFlash = d.getGia() * (1 - phanTramGiamFlash / 100);
				itemFlash.setGiaGiam(giaSauGiamFlash * (1 + thueVAT / 100));
				itemFlash.setSoLuong(soLuongDuocFlash);
				itemFlash.setIdFlashsale(idFlash);
				itemFlash.setPhanTramFlashsale(phanTramGiamFlash);
				items.add(itemFlash);
			}

			if (soLuongKhongFlash > 0) {
				CartItemLast itemNormal = new CartItemLast();
				itemNormal.setGiaGiam(d.getGia() * (1 + thueVAT / 100));
				itemNormal.setSoLuong(soLuongKhongFlash);
				itemNormal.setIdFlashsale(0L);
				itemNormal.setPhanTramFlashsale(0f);
				items.add(itemNormal);
			}

			parentCartLast.setCartItemLasts(items);
			return parentCartLast;
		}).collect(Collectors.toList());

		// Phân bổ deal
		dealApDung.forEach(deal -> {
			List<BienTheDealPhu> bienTheDealPhuList = deal.getD().getBienTheDealPhu();
			Map<Integer, Float> tempDealMap = new HashMap<>();
			bienTheDealPhuList.forEach(dealPhu -> {
				int idBienThe = dealPhu.getBienThe().getId();
				float giam = dealPhu.getGiaTriGiam();
				tempDealMap.put(idBienThe, giam);
			});

			// Đảo ngược danh sách giỏ hàng để duyệt từ dưới lên
			Collections.reverse(gioHangCuoi);

			int soLuongDealConLai = deal.getSoLuong();

			for (ParentCartLast cart : gioHangCuoi) {
				if (soLuongDealConLai <= 0)
					break;

				float phanTramGiam = tempDealMap.getOrDefault(cart.getIdBienThe(), 0f);
				if (phanTramGiam <= 0f)
					continue;

				int soLuongDealToiDaChoBienThe = getDealPhuByCondition(cart.getIdBienThe(), idHoaDon, deal.getD());
				if (soLuongDealToiDaChoBienThe <= 0)
					continue;
				List<CartItemLast> danhSachItem = new ArrayList<>(cart.getCartItemLasts());
				List<CartItemLast> updatedItems = new ArrayList<>();
				int soLuongDealDaApDungChoBienThe = 0;

				// Đảo ngược danh sách item để ưu tiên áp deal từ dưới lên
				Collections.reverse(danhSachItem);

				for (CartItemLast item : danhSachItem) {
					if (soLuongDealConLai <= 0 || soLuongDealDaApDungChoBienThe >= soLuongDealToiDaChoBienThe) {
						updatedItems.add(item);
						continue;
					}

					int soLuongItem = item.getSoLuong();
//					int soLuongApDung = Math.min(soLuongItem,
//							Math.min(soLuongDealConLai, soLuongDealToiDaChoBienThe - soLuongDealDaApDungChoBienThe));
					int toiDaTrenDonVi = bienTheDealPhuList.stream()
							.filter(d -> d.getBienThe().getId() == cart.getIdBienThe()).findFirst()
							.map(BienTheDealPhu::getToiDaTrenDonVi).orElse(1); // mặc định 1 nếu không cấu hình

					// Tính max sản phẩm có thể áp dụng theo số lượt còn lại
					int maxSanPhamApDung = soLuongDealConLai * toiDaTrenDonVi;
					int soLuongApDung = Math.min(soLuongItem,
							Math.min(maxSanPhamApDung, soLuongDealToiDaChoBienThe - soLuongDealDaApDungChoBienThe));

					// Tính số lượt deal thực sự đã dùng
					int soLuongDealSuDung = (int) Math.ceil((double) soLuongApDung / toiDaTrenDonVi);
					if (soLuongApDung <= 0) {
						updatedItems.add(item);
						continue;
					}

					if (soLuongItem == soLuongApDung) {
						item.setIdDeal(deal.getD().getId());
						item.setPhanTramDealGiam(phanTramGiam);
						item.setTenDeal(deal.getD().getTenChuongTrinh());
						updatedItems.add(item);
					} else {
						CartItemLast itemCoDeal = new CartItemLast();
						itemCoDeal.setSoLuong(soLuongApDung);
						itemCoDeal.setIdDeal(deal.getD().getId());
						itemCoDeal.setPhanTramDealGiam(phanTramGiam);
						itemCoDeal.setTenDeal(deal.getD().getTenChuongTrinh());
						itemCoDeal.setIdFlashsale(item.getIdFlashsale());
						itemCoDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
						itemCoDeal.setGiaGiam(item.getGiaGiam());

						CartItemLast itemKhongDeal = new CartItemLast();
						itemKhongDeal.setSoLuong(soLuongItem - soLuongApDung);
						itemKhongDeal.setIdDeal(0);
						itemKhongDeal.setPhanTramDealGiam(0);
						itemKhongDeal.setTenDeal("");
						itemKhongDeal.setIdFlashsale(item.getIdFlashsale());
						itemKhongDeal.setPhanTramFlashsale(item.getPhanTramFlashsale());
						itemKhongDeal.setGiaGiam(item.getGiaGiam());

						updatedItems.add(itemCoDeal);
						updatedItems.add(itemKhongDeal);
					}

//					soLuongDealConLai -= soLuongApDung;
					soLuongDealConLai -= soLuongDealSuDung;

					soLuongDealDaApDungChoBienThe += soLuongApDung;
				}

				cart.setCartItemLasts(updatedItems);
			}

			Collections.reverse(gioHangCuoi);
		});

		// Tính toán giá cuối cùng
		gioHangCuoi.forEach(parentCartLast -> {
			BienThe d = bienTheSerVice.getById(parentCartLast.getIdBienThe());
			float thueVAT = d.getSanPham().getThuevat();
			List<CartItemLast> updatedItems = new ArrayList<>();
			float totalAmount = 0f;
			int totalQuantity = 0;
			float totalDiscount = 0f;

			for (CartItemLast item : parentCartLast.getCartItemLasts()) {
				int sl = item.getSoLuong();
				if (sl <= 0)
					continue;

				float giaBanDau = d.getGia();
				float giaSauFlash = giaBanDau * (1 - item.getPhanTramFlashsale() / 100);
				float giaSauDeal = giaSauFlash * (1 - item.getPhanTramDealGiam() / 100);
				float giaSauThue = giaSauDeal * (1 + thueVAT / 100);

				item.setGiaGiam(giaSauThue);
				totalAmount += giaSauThue * sl;
				totalQuantity += sl;
				totalDiscount += (giaBanDau * (1 + thueVAT / 100) - giaSauThue) * sl;

				updatedItems.add(item);
			}

			parentCartLast.setCartItemLasts(updatedItems);
			parentCartLast.setTongTien(totalAmount);
			parentCartLast.setTongSoLuong(totalQuantity);
			parentCartLast.setTongGiaTriGiam(totalDiscount);
		});

		return gioHangCuoi;
	}

	@Transactional
	public void UPdateOnLineCanChange(List<CartItem> danhSachBienThe, long idHoaDon) {
		if (danhSachBienThe == null || danhSachBienThe.size() == 0) {
			throw new GeneralException("Đơn hàng không được để trống sản phẩm", HttpStatus.BAD_REQUEST);
		}
		HoaDon h2 = hoaDonSerVice.getById(idHoaDon);

		if (isCartItemsDifferent(danhSachBienThe, h2.getChiTietHoaDons()) == false
				&& danhSachBienThe.get(0).getDiaChi().equals(h2.getDiaChi()) == true) {
			throw new GeneralException("Bạn chưa thực hiện bất cứ thay đổi nào hệ thống sẽ vẫn giữ nguyên đơn hàng",
					HttpStatus.BAD_REQUEST);
		}
		h2.setDiaChi(danhSachBienThe.get(0).getDiaChi());
		NguoiDung khachHang = nguoiDungService.getById(jwtService.getIdUser());
		if (khachHang instanceof KhachHang && h2.getKhachHang().getId() != khachHang.getId()) {
			throw new GeneralException("Bạn không có quyền chỉnh sửa hóa đơn này", HttpStatus.BAD_REQUEST);
		}
		if (khachHang instanceof KhachHang && (h2.getTrangThai().getId() != 3 || h2 instanceof HoaDonTaiQuay)) {
			throw new GeneralException(
					"Quý khách không có quyền chỉnh hóa đơn này, vui lòng liên hệ nhân viên để được hỗ trợ",
					HttpStatus.BAD_REQUEST);
		}
		HoaDonOnline h = (HoaDonOnline) h2;
		boolean kt = kt(danhSachBienThe, idHoaDon);
		List<ParentCartLast> parentCartLasts = calculateAlReady(danhSachBienThe, idHoaDon, kt);

		// DANH SÁCH ID BIẾN THỂ CỦA HÓA ĐƠN CHƯA UPDATE
		List<Integer> danhSachMoi = danhSachBienThe.stream().map(d -> d.getIdBienThe()).collect(Collectors.toList());
		List<Integer> danhSachCu = h.getChiTietHoaDons().stream().map(d -> d.getBienThe().getId())
				.collect(Collectors.toList());

		// Lưu các ChiTietHoaDon cần xóa để tránh ConcurrentModificationException
		List<ChiTietHoaDon> chiTietToRemove = h.getChiTietHoaDons().stream()
				.filter(d -> !danhSachMoi.contains(d.getBienThe().getId())).collect(Collectors.toList());

		// Xóa ChiTietHoaDon và cập nhật danh sách trong bộ nhớ
		chiTietToRemove.forEach(d -> {
			BienThe b = d.getBienThe();
			b.setSoLuongKho(b.getSoLuongKho() + d.getSoLuong());
			bienTheSerVice.save(b);

			// Xóa ApDungKhuyenMai và cập nhật danh sách trong bộ nhớ
			List<ApDungKhuyenMai> khuyenMaiToRemove = new ArrayList<>(d.getDanhSachKhuyenMai());
			khuyenMaiToRemove.forEach(m -> {
				if (m.isDealPhu() == false) {
					apDungKhuyenMaiService.delete(m.getId());
				} else {
					if (m.getKhuyenMai() instanceof Deal) {
						Deal dd = (Deal) m.getKhuyenMai();
						dd.setSoLuongDaDung(dd.getSoLuongDaDung() - m.getSoLuongApDung());
						dealService.save(dd);
						apDungKhuyenMaiService.delete(m.getId());
					} else {
						((FlashSale) m.getKhuyenMai()).getBienTheFlashSale().forEach(l -> {
							if (l.getBienThe().getId() == b.getId()) {
								BienTheFlashSale bf = l;
								bf.setSoLuongDaDung(l.getSoLuongDaDung() - m.getSoLuongApDung());
								bienTheFlashSaleService.save(bf);
							}
						});
						apDungKhuyenMaiService.delete(m.getId());
					}
				}
			});
			d.getDanhSachKhuyenMai().clear(); // Xóa danh sách khuyến mãi trong ChiTietHoaDon
			chiTietHoaDonService.delete(d.getId());
		});

		// Xóa ChiTietHoaDon khỏi danh sách trong bộ nhớ
		h.getChiTietHoaDons().removeAll(chiTietToRemove);

		// Xóa ApDungKhuyenMai không phải dealPhu (tránh trùng lặp)
		List<ApDungKhuyenMai> khuyenMaiToRemove = h.getChiTietHoaDons().stream()
				.flatMap(d -> d.getDanhSachKhuyenMai().stream()).filter(d -> !d.isDealPhu())
				.collect(Collectors.toList());
		khuyenMaiToRemove.forEach(d -> {
			apDungKhuyenMaiService.delete(d.getId());
			// Xóa khỏi danh sách trong bộ nhớ của ChiTietHoaDon tương ứng
			h.getChiTietHoaDons().forEach(chiTiet -> {
				chiTiet.getDanhSachKhuyenMai().remove(d);
			});
		});

		// Phần còn lại của logic giữ nguyên
		h.getChiTietHoaDons().stream().flatMap(d -> d.getDanhSachKhuyenMai().stream())
				.filter(d -> d.isDealPhu() == false).forEach(d -> {
					apDungKhuyenMaiService.delete(d.getId()); // Đã xử lý ở trên, dòng này có thể dư thừa
				});
		parentCartLasts.stream().flatMap(m -> m.getCartItemLasts().stream()).filter(d -> d.getIdDeal() != 0)
				.forEach(d -> {
					Deal dd = dealService.getById(d.getIdDeal());
					List<Integer> idDealChinh = dd.getBienTheDealChinh().stream().map(m -> m.getBienThe().getId())
							.distinct().collect(Collectors.toList());
					danhSachBienThe.stream().filter(df -> idDealChinh.contains(df.getIdBienThe())).forEach(m -> {
						List<BienTheDealChinh> bg = dd.getBienTheDealChinh().stream()
								.filter(j -> j.getBienThe().getId() == m.getIdBienThe()).collect(Collectors.toList());
						ApDungKhuyenMai a = new ApDungKhuyenMai();
						a.setBienThe(bienTheSerVice.getById(m.getIdBienThe()));
						a.setDealPhu(false);
						a.setHoaDon(h);
						a.setKhuyenMai(dd);
						a.setSoLuongApDung(bg.get(0).getSoLuongTu());
						a.setThoiDiemApDung(LocalDateTime.now());
						a.setTyLeApDung(0);
						// Thêm lưu ApDungKhuyenMai để đảm bảo
						apDungKhuyenMaiService.saveAlready(a);
					});
				});

		AtomicReference<Float> tongTien = new AtomicReference<Float>(0f);
		for (ParentCartLast pr : parentCartLasts) {
			BienThe bienThe = bienTheSerVice.getById(pr.getIdBienThe());
			if (!danhSachCu.contains(pr.getIdBienThe())) {
				ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
				chiTietHoaDon.setBienThe(bienThe);
				chiTietHoaDon.setDonGia(bienThe.getGia());
				chiTietHoaDon.setHoaDon(h);
				chiTietHoaDon.setSoLuong(pr.getTongSoLuong());
				chiTietHoaDon.setTongTien(pr.getTongTien());
				chiTietHoaDonService.save(chiTietHoaDon);
				tongTien.updateAndGet(v -> v + pr.getTongTien());
			} else {
				h.getChiTietHoaDons().forEach(chiTietHoaDon -> {
					if (chiTietHoaDon.getBienThe().getId() == pr.getIdBienThe()) {
						chiTietHoaDon.setDonGia(bienThe.getGia());
						chiTietHoaDon.setTongTien(pr.getTongTien());
						chiTietHoaDonService.save(chiTietHoaDon);
						tongTien.updateAndGet(v -> v + pr.getTongTien());
					}
				});
			}

			// Lưu danh sách khuyến mãi
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
			List<ApDungKhuyenMai> test1 = h.getChiTietHoaDons().stream()
					.filter(d -> d.getBienThe().getId() == bienThe.getId())
					.flatMap(m -> m.getDanhSachKhuyenMai().stream())
					.filter(g -> (g.getKhuyenMai() instanceof Deal && g.isDealPhu() == true))
					.collect(Collectors.toList());
			List<ApDungKhuyenMai> test2 = h.getChiTietHoaDons().stream()
					.filter(d -> d.getBienThe().getId() == pr.getIdBienThe())
					.flatMap(m -> m.getDanhSachKhuyenMai().stream())
					.filter(g -> (g.getKhuyenMai() instanceof FlashSale && g.isDealPhu() == true))
					.collect(Collectors.toList());
			if (ideal.get() != 0 && test1.isEmpty()) {
				Deal d = dealService.getById(ideal.get());
				if ((d.getSoLuongGioiHan() - (d.getSoLuongDaDung() + tongSoLuongDeal.get())) < 0) {
					throw new GeneralException(
							"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
							HttpStatus.BAD_REQUEST);
				}
				d.setSoLuongDaDung(d.getSoLuongDaDung() + tongSoLuongDeal.get());
				dealService.save(d);
				ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();
				apDungKhuyenMai.setBienThe(bienThe);
				apDungKhuyenMai.setHoaDon(h);
				apDungKhuyenMai.setKhuyenMai(dealService.getById(ideal.get()));
				apDungKhuyenMai.setSoLuongApDung(tongSoLuongDeal.get());
				apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
				apDungKhuyenMai.setTyLeApDung(tyLeKhuyenMaiDeal.get());
				apDungKhuyenMai.setDealPhu(true);
				apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);
				// Thêm vào danh sách khuyến mãi của ChiTietHoaDon
				h.getChiTietHoaDons().stream().filter(ct -> ct.getBienThe().getId() == bienThe.getId()).findFirst()
						.ifPresent(ct -> ct.getDanhSachKhuyenMai().add(apDungKhuyenMai));
			} else if (ideal.get() != 0 && test1.isEmpty() == false) {
				if (ideal.get() == test1.get(0).getKhuyenMai().getId()) {
					Deal d = (Deal) test1.get(0).getKhuyenMai();
					int sl = tongSoLuongDeal.get() - test1.get(0).getSoLuongApDung();
					if ((d.getSoLuongGioiHan() - (d.getSoLuongDaDung() + sl)) < 0) {
						throw new GeneralException(
								"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
								HttpStatus.BAD_REQUEST);
					}
					d.setSoLuongDaDung(d.getSoLuongDaDung() + sl);
					ApDungKhuyenMai apDungKhuyenMai = test1.get(0);
					apDungKhuyenMai.setSoLuongApDung(test1.get(0).getSoLuongApDung() + sl);
					apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);
					dealService.save(d);
				} else {
					Deal dd = (Deal) test1.get(0).getKhuyenMai();
					dd.setSoLuongDaDung(dd.getSoLuongDaDung() - test1.get(0).getSoLuongApDung());
					dealService.save(dd);
					apDungKhuyenMaiService.delete(test1.get(0).getId());
					// Xóa khỏi danh sách trong bộ nhớ
					h.getChiTietHoaDons().forEach(ct -> ct.getDanhSachKhuyenMai().remove(test1.get(0)));
					Deal d = dealService.getById(ideal.get());
					if ((d.getSoLuongGioiHan() - (d.getSoLuongDaDung() + tongSoLuongDeal.get())) < 0) {
						throw new GeneralException(
								"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
								HttpStatus.BAD_REQUEST);
					}
					d.setSoLuongDaDung(d.getSoLuongDaDung() + tongSoLuongDeal.get());
					dealService.save(d);
					ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();
					apDungKhuyenMai.setBienThe(bienThe);
					apDungKhuyenMai.setHoaDon(h);
					apDungKhuyenMai.setKhuyenMai(d);
					apDungKhuyenMai.setSoLuongApDung(tongSoLuongDeal.get());
					apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
					apDungKhuyenMai.setTyLeApDung(tyLeKhuyenMaiDeal.get());
					apDungKhuyenMai.setDealPhu(true);
					apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);
					// Thêm vào danh sách khuyến mãi của ChiTietHoaDon
					h.getChiTietHoaDons().stream().filter(ct -> ct.getBienThe().getId() == bienThe.getId()).findFirst()
							.ifPresent(ct -> ct.getDanhSachKhuyenMai().add(apDungKhuyenMai));
				}
			}
			// Xử lý FlashSale
			if (idflash.get() != 0 && test2.isEmpty()) {
				FlashSale f = flashSaleService.getById(idflash.get());
				List<BienTheFlashSale> bienTheFlashSale = f.getBienTheFlashSale().stream()
						.filter(l -> l.getBienThe().getId() == bienThe.getId()).collect(Collectors.toList());
				if (bienTheFlashSale != null && !bienTheFlashSale.isEmpty()) {
					BienTheFlashSale bienTheFlashSale2 = bienTheFlashSale.get(0);
					if ((bienTheFlashSale2.getSoLuongGioiHan()
							- (bienTheFlashSale2.getSoLuongDaDung() + tongSoLuongFlashsale.get())) < 0) {
						throw new GeneralException(
								"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
								HttpStatus.BAD_REQUEST);
					}
					bienTheFlashSale2
							.setSoLuongDaDung(bienTheFlashSale2.getSoLuongDaDung() + tongSoLuongFlashsale.get());
					bienTheFlashSaleService.save(bienTheFlashSale2);
					ApDungKhuyenMai apDungKhuyenMai = new ApDungKhuyenMai();
					apDungKhuyenMai.setBienThe(bienThe);
					apDungKhuyenMai.setHoaDon(h);
					apDungKhuyenMai.setKhuyenMai(f);
					apDungKhuyenMai.setSoLuongApDung(tongSoLuongFlashsale.get());
					apDungKhuyenMai.setThoiDiemApDung(LocalDateTime.now());
					apDungKhuyenMai.setTyLeApDung(tyLeKhuyenMaiFlashSale.get());
					apDungKhuyenMai.setDealPhu(true);
					apDungKhuyenMaiService.saveAlready(apDungKhuyenMai);
					// Thêm vào danh sách khuyến mãi của ChiTietHoaDon
					h.getChiTietHoaDons().stream().filter(ct -> ct.getBienThe().getId() == bienThe.getId()).findFirst()
							.ifPresent(ct -> ct.getDanhSachKhuyenMai().add(apDungKhuyenMai));
				}
			} else if (idflash.get() != 0 && !test2.isEmpty()) {
				if (idflash.get() != test2.get(0).getKhuyenMai().getId()) {
					FlashSale f = flashSaleService.getById(idflash.get());
					((FlashSale) test2.get(0).getKhuyenMai()).getBienTheFlashSale().forEach(d -> {
						if (d.getBienThe().getId() == bienThe.getId()) {
							BienTheFlashSale demo = d;
							demo.setSoLuongDaDung(d.getSoLuongDaDung() - test2.get(0).getSoLuongApDung()); // Giữ nguyên
																											// logic gốc
							bienTheFlashSaleService.save(demo);
							apDungKhuyenMaiService.delete(test2.get(0).getId());
							// Xóa ApDungKhuyenMai khỏi danh sách trong bộ nhớ của ChiTietHoaDon
							h.getChiTietHoaDons().stream().filter(ct -> ct.getBienThe().getId() == bienThe.getId())
									.findFirst().ifPresent(ct -> ct.getDanhSachKhuyenMai().remove(test2.get(0)));
						}
					});
					ApDungKhuyenMai a = new ApDungKhuyenMai();
					List<BienTheFlashSale> t = f.getBienTheFlashSale().stream()
							.filter(i -> i.getBienThe().getId() == bienThe.getId()).collect(Collectors.toList());
					a.setBienThe(bienThe);
					a.setDealPhu(true);
					a.setHoaDon(h);
					a.setKhuyenMai(f);
					a.setSoLuongApDung(tongSoLuongFlashsale.get());
					a.setThoiDiemApDung(LocalDateTime.now());
					a.setTyLeApDung(t.get(0).getGiaTriGiam());
					apDungKhuyenMaiService.saveAlready(a);
					// Thêm vào danh sách khuyến mãi của ChiTietHoaDon
					h.getChiTietHoaDons().stream().filter(ct -> ct.getBienThe().getId() == bienThe.getId()).findFirst()
							.ifPresent(ct -> ct.getDanhSachKhuyenMai().add(a));
				} else {
					System.out.println("FLASH SALE TRÙNG");
					FlashSale f = (FlashSale) test2.get(0).getKhuyenMai();
					List<BienTheFlashSale> bienTheFlashSale = f.getBienTheFlashSale().stream()
							.filter(l -> l.getBienThe().getId() == bienThe.getId()).collect(Collectors.toList());
					if (bienTheFlashSale != null && !bienTheFlashSale.isEmpty()) {
						BienTheFlashSale bienTheFlashSale2 = bienTheFlashSale.get(0);
						int sl = tongSoLuongFlashsale.get() - test2.get(0).getSoLuongApDung();
						if ((bienTheFlashSale2.getSoLuongGioiHan() - (bienTheFlashSale2.getSoLuongDaDung() + sl)) < 0) {
							throw new GeneralException(
									"Quý khách vui lòng kiểm load lại tiền do khuyến mãi đã bị khách hàng khác sử dụng trước",
									HttpStatus.BAD_REQUEST);
						}
						bienTheFlashSale2.setSoLuongDaDung(bienTheFlashSale2.getSoLuongDaDung() + sl);
						bienTheFlashSaleService.save(bienTheFlashSale2);
						test2.get(0).setSoLuongApDung(test2.get(0).getSoLuongApDung() + sl);
						apDungKhuyenMaiService.saveAlready(test2.get(0));
					}
				}
			}

			// Trừ số lượng hàng
			List<ChiTietHoaDon> hh = h.getChiTietHoaDons().stream()
					.filter(d -> d.getBienThe().getId() == bienThe.getId()).collect(Collectors.toList());
			System.out.println("số lượng mới nhận được : " + pr.getTongSoLuong());
			System.out.println(bienThe.getSoLuongKho());
			System.out.println("số lượng hóa đơn ");
			if (hh.isEmpty()) {
				System.out.println("đi vào trong đây");
				ChiTietHoaDon c = new ChiTietHoaDon();
				bienThe.setSoLuongKho(bienThe.getSoLuongKho() - pr.getTongSoLuong());
				bienTheSerVice.save(bienThe);
				System.out.println("số lượng của biến thể: " + bienThe.getSoLuongKho());
				c.setBienThe(bienThe);
				c.setDonGia(bienThe.getGia());
				c.setHoaDon(h);
				c.setSoLuong(pr.getTongSoLuong());
				c.setTongTien(pr.getTongTien());
				chiTietHoaDonService.save(c);

			} else {
				System.out.println("đi vào đây 2: " + bienThe.getId());
				bienThe.setSoLuongKho(bienThe.getSoLuongKho() + (pr.getTongSoLuong() - hh.get(0).getSoLuong()));
				System.out.println(pr.getTongSoLuong());
				System.out.println(hh.get(0).getSoLuong());
				ChiTietHoaDon c = hh.get(0);
				c.setDonGia(bienThe.getGia());
				c.setSoLuong(pr.getTongSoLuong());
				c.setTongTien(pr.getTongTien());
				chiTietHoaDonService.save(c);
				bienTheSerVice.save(bienThe);
			}
		}

		float lamTron = (float) (Math.ceil(tongTien.get() / 100) * 100);
		h.setTongTien(lamTron);
		hoaDonOnlineRepository.save(h);
	}

	public Map<String, Object> filterHoaDonOnline(String maHoaDon, String khachHang, long trangThai, LocalDate ngayLap,
			int sort, int trang) {
		Pageable p = PageRequest.of(trang, 15);
		Page<HoaDonOnline> ds = hoaDonOnlineRepository.findHoaDonOnlineByFilters(maHoaDon, trangThai, khachHang,
				ngayLap, sort, p);
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("tongTrang", ds.getTotalPages());
		re.put("tongSoPhanTu", ds.getTotalElements());
		re.put("danhSach", ds.getContent().stream().map(d -> {
			Map<String, Object> i = new HashMap<String, Object>();
			i.put("id", d.getId());
			i.put("ngayLap", d.getNgayLap());
			float tongTienFloat = d.getTongTien();
			long tongTienLamTron = (long) (Math.ceil(tongTienFloat / 1000.0) * 1000);

			i.put("tongTien", tongTienLamTron);

			i.put("tongSoLuongHang", d.getChiTietHoaDons().stream().mapToInt(f -> f.getSoLuong()).sum());
			i.put("tongSoMatHang", d.getChiTietHoaDons().size());
			i.put("daThanhToan", d.isDaThanhTona());
			i.put("daHoanHang", d.isDaHoanHang());
			i.put("danhSachMatHang", d.getChiTietHoaDons().stream().map(m -> {
				Map<String, Object> ma = new HashMap<>();
				ma.put("tenSanPham", m.getBienThe().getSanPham().getTen());
				ma.put("soLuong", m.getSoLuong());
				ma.put("donGia", m.getDonGia());
				ma.put("tenPhanLoai", m.getBienThe().getTen());
				return ma;
			}).collect(Collectors.toList()));

			i.put("idKhachHang", d.getKhachHang() != null ? d.getKhachHang().getId() : 0);
			i.put("tenKhachHang", d.getKhachHang() != null ? d.getKhachHang().getTen() : "Khách vãng lai");
			if (d.getTrangThai().getId() == 6) {
				i.put("lyDo", d.getTrangThaiHoaDon().get(d.getTrangThaiHoaDon().size() - 1).getGhiChu());
			}

			else {
				i.put("lyDo", null);
			}
			i.put("daLenDonGHN", d.isDaLenDonGHN());
			i.put("khoiLuong", d.getTongKhoiLuong());
			i.put("chieuRong", d.getDongHop().getChieuRong());
			i.put("chieuCao", d.getDongHop().getChieuCao());
			i.put("canNang", d.getTongKhoiLuong());
			i.put("dongGoiId", d.getDongHop().getId());
			i.put("tenDongGoi", d.getDongHop().getTen());
			i.put("chieuDai", d.getDongHop().getChieuDai());
			i.put("duocDuyet", true);
			if (hoaDonSerVice.ischange(d.getId()) == false) {
				System.out.println("đơn không được duyệt: " + d.getId());
				i.put("duocDuyet", false);
			}
			if (d.isDaThanhTona() == true && d.getTrangThai().getId() == 2 && d.isDaLenDonGHN() == false
					&& hoaDonSerVice.ischange(d.getId()) == true) {
				i.put("lenDon", true);
			} else {
				i.put("lenDon", false);
			}

			return i;
		}).collect(Collectors.toList()));
		return re;
	}

	public boolean getTrangThaiHoaDon(long id, int lan) {
		HoaDonOnline h = hoaDonOnlineRepository.findById(id)
				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
		if(h.getTrangThai().getId()!=2 || h.isDaThanhTona()) {
			throw new GeneralException("Hóa đơn đã được thanh toán vui lòng kiểm tra lại",HttpStatus.BAD_REQUEST);
		}
		if (h.getKhachHang().getId() != jwtService.getIdUser()) {
			throw new GeneralException("Bạn không có quyền xem hóa đơn này", HttpStatus.BAD_REQUEST);
		}
		if (lan % 3 == 0 && h.isDaThanhTona() == false) {
			hoaDonSerVice.checkThanhToan(h);
		}

		return h.isDaThanhTona();
	}

	public Map<String, Object> getThongTinThanhToan(long id) {
		HoaDonOnline h = hoaDonOnlineRepository.findById(id)
				.orElseThrow(() -> new GeneralException("Hóa đơn không tồn tại", null));
		if (h.getKhachHang().getId() != jwtService.getIdUser() || h.getTrangThai().getId() != 2) {
			throw new GeneralException("Bạn không có quyền thực hiện thanh toán hóa đơn", HttpStatus.OK);
		}
		float tongTien = h.getTongTien();
		long lamTron2 = (long) (Math.ceil(tongTien / 1000.0f) * 1000);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("needCheck", false);
		result.put("tongTien", lamTron2);
//		Map<String, String> thongTinThanhToan = new HashMap<String, String>();
		ZaloPayOrder zaloPayResponseDTO = null;
		try {
			if (h.getqRCode() == null) {
				zaloPayResponseDTO = zaloPayService.createOrder(app_USER, lamTron2, "Thanh toán đơn hàng SKINLY",
						String.valueOf(h.getId()));
				h.setqRCode(zaloPayResponseDTO.getOrderUrl());
				h.setTransId(zaloPayResponseDTO.getAppTransId());
//			h.setHD_zpTransToken(zaloPayResponseDTO.getZpTransToken());
				hoaDonSerVice.Update(h);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(
					"Bạn không thể thực hiện thay đổi nữa, hãy tạo lại đơn hoặc thanh toán bằng tiền mặt",
					HttpStatus.BAD_REQUEST);
		}
		Map<String, Object> ThongTinDonHang = viewHoaDonAlReady(id);
		result.put("tongTien", ThongTinDonHang.get("tongTien"));
		result.put("tongGiamGia", ThongTinDonHang.get("giamGia"));
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		result.put("tenNguoiDung", nguoiDung.getTen());
		result.put("soDienThoai", nguoiDung.getSodienthoai());
		result.put("ngayLap", ThongTinDonHang.get("ngayLap"));
		result.put("qrCodeUrl", h.getqRCode());
		result.put("idCall", h.getId());
		return result;
	}

	@Transactional
	public void hoanDon(long id) {
		HoaDon hoaDon = hoaDonSerVice.getById(id);
		if (hoaDon.getTrangThai().getId() == 8 || hoaDon.getTrangThai().getId() == 9
				|| hoaDon.getTrangThai().getId() == 14) {
			hoaDon.setDaHoanHang(true);
			long tienGoc = (long) hoaDon.getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000;
			try {
				zaloPayService.refund(hoaDon.getHD_zpTrans_Id() + "", soTienRefound,
						"Hoàn tiền hóa đơn -SKINLY- " + hoaDon.getId(), hoaDon.getRefund(), null);
				hoaDon.setDaHoanHang(true);
				hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("Hoàn tiền thất bại vui lòng thử lại", HttpStatus.BAD_REQUEST);
			}
		} else if (hoaDon.getTrangThai().getId() == 15) {
			hoaDon.setDaHoanHang(true);
			long tienGoc = (long) hoaDon.getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000;
			TrangThai trangThai = trangThaiService.getById(14);
			hoaDon.setTrangThai(trangThai);
			trangThaiHoaDonService.Save(hoaDon, trangThai, "Hoàn tiền hóa đơn SKINLY - " + hoaDon.getId());
			hoaDonSerVice.Update(hoaDon);
			try {
				zaloPayService.refund(hoaDon.getHD_zpTrans_Id() + "", soTienRefound,
						"Hoàn tiền hóa đơn - SKINLY- " + hoaDon.getId(), hoaDon.getRefund(), null);
				hoaDon.setDaHoanHang(true);
				hoaDonOnlineRepository.save((HoaDonOnline) hoaDon);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("Hoàn tiền thất bại vui lòng thử lại", HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new GeneralException("Hóa đơn này không thể hoàn tiền do chưa đáp ứng đủ điều kiện",
					HttpStatus.BAD_REQUEST);
		}
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

	@Transactional
	public void duyetDonHang(List<Long> ds) {
		OrderClassSchedule orderClassSchedule = ServiceLocator.getBean(OrderClassSchedule.class);
		List<HoaDonOnline> dss = ds.stream().map(d -> {
			HoaDonOnline h;
			try {
				h = hoaDonOnlineRepository.findById(d)
						.orElseThrow(() -> new GeneralException("Không tìm thấy hóa đơn", HttpStatus.BAD_REQUEST));
			} catch (Exception e) {
				System.out.println("lloix trên");
				throw new GeneralException("Hóa đơn được duyệt không hợp lệ", HttpStatus.BAD_REQUEST);
			}
			System.out.println(h.getTrangThai().getId());
			return h;
		}).filter(df -> df.getTrangThai().getId() == 3).collect(Collectors.toList());
		System.out.println("số lượng phần tử :" + dss.size());
		if (dss.size() != ds.size()) {
			System.out.println("lỗi dướis");
			throw new GeneralException("Hóa đơn được duyệt không hợp lệ", HttpStatus.BAD_REQUEST);
		}
		dss.forEach(d -> {
			try {

				orderClassSchedule.scheduleJob(d.getId(), 70);
				TrangThai trangThai = trangThaiService.getById(2);
				trangThaiHoaDonService.Save(d, trangThai, "");
				d.setTrangThai(trangThai);
				hoaDonOnlineRepository.save(d);
				pushNotificationService.sendSubScription("Đơn hàng Skinly của bạn cần thanh toán", d.getKhachHang());
			} catch (Exception e) {
				throw new GeneralException("Có lỗi xảy ra", HttpStatus.BAD_REQUEST);

			}
		});
		HoaDonSerVice hoaDonSerVice = ServiceLocator.getBean(HoaDonSerVice.class);

	}

	// hàm mới thêm
	public DonGiaBan tinhDonGiaBan(long idHoaDon, long idBienThe) {
		// Lấy hóa đơn
		HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
		if (hoaDon == null) {
			throw new GeneralException("Hóa đơn không tồn tại: " + idHoaDon, HttpStatus.NOT_FOUND);
		}

		// Tìm ChiTietHoaDon tương ứng với idBienThe
		ChiTietHoaDon chiTietHoaDon = hoaDon.getChiTietHoaDons().stream()
				.filter(d -> d.getBienThe().getId() == idBienThe).findFirst()
				.orElseThrow(() -> new GeneralException("Không tìm thấy ChiTietHoaDon với idBienThe: " + idBienThe,
						HttpStatus.NOT_FOUND));

		// Khởi tạo đối tượng DonGiaBan
		DonGiaBan donGiaBan = new DonGiaBan();
		donGiaBan.setDonGia(new ArrayList<>());

		// Lấy thuế VAT
		ThueVATSanPham thueVATSanPham = thueVATSanPhamSerVice
				.getThueVATAtTime(chiTietHoaDon.getBienThe().getSanPham().getId(), hoaDon.getNgayLap());
		float thueVAT = thueVATSanPham != null ? thueVATSanPham.getTiLe() : 0f;

		// Lấy danh sách khuyến mãi
		List<ApDungKhuyenMai> apDungKhuyenMais = chiTietHoaDon.getDanhSachKhuyenMai();
		List<CartItemLast> cartItemLasts = new ArrayList<>();
		int soLuongDealApDung = 0;
		int soLuongFlashApDung = 0;
		float tyLeDealApDung = 0;
		float tyLeFlashApDung = 0;
		Deal deal = new Deal();
		FlashSale flash = new FlashSale();

		// Phân loại khuyến mãi
		for (ApDungKhuyenMai apDung : apDungKhuyenMais) {
			if (apDung.getKhuyenMai() instanceof Deal) {
				deal = (Deal) apDung.getKhuyenMai();
				soLuongDealApDung = apDung.getSoLuongApDung();
				tyLeDealApDung = apDung.getTyLeApDung();
			} else if (apDung.getKhuyenMai() instanceof FlashSale) {
				flash = (FlashSale) apDung.getKhuyenMai();
				soLuongFlashApDung = apDung.getSoLuongApDung();
				tyLeFlashApDung = apDung.getTyLeApDung();
			}
		}

		// Xử lý FlashSale
		if (tyLeFlashApDung == 0) {
			CartItemLast cc = new CartItemLast();
			cc.setIdDeal(0);
			cc.setIdFlashsale(0);
			cc.setPhanTramDealGiam(0);
			cc.setPhanTramFlashsale(0);
			cc.setSoLuong(chiTietHoaDon.getSoLuong());
			cc.setTenDeal(null);
			cartItemLasts.add(cc);
		} else {
			if (chiTietHoaDon.getSoLuong() >= soLuongFlashApDung) {
				CartItemLast cc = new CartItemLast();
				cc.setIdDeal(0);
				cc.setIdFlashsale(flash.getId());
				cc.setPhanTramDealGiam(0);
				cc.setPhanTramFlashsale(tyLeFlashApDung);
				cc.setSoLuong(soLuongFlashApDung);
				cc.setTenDeal(null);
				cartItemLasts.add(cc);

				if (chiTietHoaDon.getSoLuong() > soLuongFlashApDung) {
					CartItemLast cc2 = new CartItemLast();
					cc2.setIdDeal(0);
					cc2.setIdFlashsale(0);
					cc2.setPhanTramDealGiam(0);
					cc2.setPhanTramFlashsale(0);
					cc2.setSoLuong(chiTietHoaDon.getSoLuong() - soLuongFlashApDung);
					cc2.setTenDeal(null);
					cartItemLasts.add(cc2);
				}
			} else {
				CartItemLast cc = new CartItemLast();
				cc.setIdDeal(0);
				cc.setIdFlashsale(flash.getId());
				cc.setPhanTramDealGiam(0);
				cc.setPhanTramFlashsale(tyLeFlashApDung);
				cc.setSoLuong(chiTietHoaDon.getSoLuong());
				cc.setTenDeal(null);
				cartItemLasts.add(cc);
			}
		}

		// Xử lý Deal
		if (tyLeDealApDung != 0) {
			List<CartItemLast> tempCartItemLasts = new ArrayList<>(cartItemLasts);
			cartItemLasts.clear();
			int remainingDealQuantity = soLuongDealApDung;

			Collections.reverse(tempCartItemLasts);

			for (CartItemLast item : tempCartItemLasts) {
				if (remainingDealQuantity <= 0) {
					cartItemLasts.add(item);
					continue;
				}

				if (item.getSoLuong() <= remainingDealQuantity) {
					item.setIdDeal(deal.getId());
					item.setPhanTramDealGiam(tyLeDealApDung);
					item.setTenDeal(deal.getTenChuongTrinh());
					cartItemLasts.add(item);
					remainingDealQuantity -= item.getSoLuong();
				} else {
					CartItemLast cc1 = new CartItemLast();
					cc1.setIdDeal(deal.getId());
					cc1.setIdFlashsale(item.getIdFlashsale());
					cc1.setPhanTramDealGiam(tyLeDealApDung);
					cc1.setPhanTramFlashsale(item.getPhanTramFlashsale());
					cc1.setSoLuong(remainingDealQuantity);
					cc1.setTenDeal(deal.getTenChuongTrinh());
					cartItemLasts.add(cc1);

					CartItemLast cc2 = new CartItemLast();
					cc2.setIdDeal(0);
					cc2.setIdFlashsale(item.getIdFlashsale());
					cc2.setPhanTramDealGiam(0);
					cc2.setPhanTramFlashsale(item.getPhanTramFlashsale());
					cc2.setSoLuong(item.getSoLuong() - remainingDealQuantity);
					cc2.setTenDeal(null);
					cartItemLasts.add(cc2);

					remainingDealQuantity = 0;
				}
			}
			Collections.reverse(cartItemLasts);
		}

		// Tính giá và thêm vào DonGiaBan
		AtomicReference<Float> tongTien = new AtomicReference<>(0f);

		cartItemLasts.forEach(df -> {
			float giaFlash = df.getPhanTramFlashsale();
			float giaDeal = df.getPhanTramDealGiam();
			float giaGoc = chiTietHoaDon.getDonGia();

			float giaSauFlash = giaGoc * (1 - giaFlash / 100f);
			float giaSauDeal = giaSauFlash * (1 - giaDeal / 100f);
			float giaSauVAT = giaSauDeal * (1 + thueVAT / 100f);

			// Thêm vào danh sách ItemDonGia
			donGiaBan.themItem(df.getSoLuong(), giaSauVAT);
			tongTien.updateAndGet(v -> v + giaSauVAT * df.getSoLuong());
		});

		// Đảm bảo tổng đơn giá được tính lại
		donGiaBan.tinhTongDonGia();

		return donGiaBan;
	}

	public GHNOrder getGHNorderFromCartItemForCalculatrfee(List<CartItem> cart, String address) {
		GHNOrder ghnOrder = new GHNOrder();
		// LƯU Ý Ở ĐÂY NHA LÊN LẤY NẾU KỊP THƯỜI GIAN TỐI ƯU ĐƯỢC CÁI HỘP
		// CHIỀU CAO RỘNG DÀI NẰM Ở ĐÂY
		DongHop dongHop = dongHopService.getToiUu();
		AtomicInteger tongKhoiLuong = new AtomicInteger(0);
		// TÍNH TỔNG KHỐI LƯỢNG
		AtomicReference<Float> tongTienHang = new AtomicReference<Float>(0f);
		List<GHNItem> GHNItem = cart.stream().map(d -> {
			BienThe bienThe = bienTheSerVice.getById(d.getIdBienThe());
			tongKhoiLuong.addAndGet(bienThe.getKhoiLuong() * d.getSoLuong());
			tongTienHang.updateAndGet(v -> v + bienThe.getGia() * d.getSoLuong());
			GHNItem ghnItem = new GHNItem();
			ghnItem.setName(bienThe.getSanPham().getTen() + " - "
					+ (bienThe.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : bienThe.getTen()));
			int result = (int) Math.ceil(bienThe.getGia());
			ghnItem.setPrice(result);
			ghnItem.setWeight(bienThe.getKhoiLuong());
			ghnItem.setQuantity(d.getSoLuong());
			return ghnItem;
		}).collect(Collectors.toList());

		// THÔNG TIN ĐỊA CHỈ
		int idXa = AddressService.getThirdNumberAfterDot(address);
		int idHuyen = AddressService.getSecondNumberAfterDot(address);
		String diChi = AddressService.getTextBeforeDot(address);

		Map<String, Integer> servic = ghnService.getService(1572, idHuyen);
		tongKhoiLuong.addAndGet(dongHop.getCanNangBoSung());

		ghnOrder.setPayment_type_id(GHNConstans.NGUOIMUA_TRAPHI);
		ghnOrder.setRequired_note(GHNConstans.REQUIRED_NOTE_CHOTHUHANG);
		ghnOrder.setHeight(dongHop.getChieuCao());
		ghnOrder.setWidth(dongHop.getChieuRong());
		ghnOrder.setLength(dongHop.getChieuDai());
		ghnOrder.setWeight(tongKhoiLuong.get());
		ghnOrder.setTo_district_id(idHuyen);
		ghnOrder.setTo_ward_code(String.valueOf(idXa));

		ghnOrder.setFrom_district_id(1572);
		ghnOrder.setFrom_ward_code("550113");
		ghnOrder.setItems(GHNItem);
		ghnOrder.setService_id(servic.get("s_id"));
		ghnOrder.setService_type_id(servic.get("s_t_id"));
		return ghnOrder;
	}

	public void reCreateGHNOrder(long id) {
		HoaDonOnline hoaDonOnline = (HoaDonOnline) hoaDonSerVice.getById(id);
		GHNOrder ghnOrder = getGHNorderFromCartItemForCreateOrder(hoaDonOnline);
		ghnOrder.setWeight(hoaDonOnline.getTongKhoiLuong());
		ghnOrder.setHeight(hoaDonOnline.getDongHop().getChieuCao());
		ghnOrder.setWidth(hoaDonOnline.getDongHop().getChieuCao());
		ghnOrder.setLength(hoaDonOnline.getDongHop().getChieuDai());
		Map<String, Object> re = null;
		try {
			re = ghnService.CreateGHNOrder(ghnOrder);
		} catch (Exception e) {
			throw new GeneralException("Lên đơn thất bại, vui lòng thử lại", HttpStatus.BAD_REQUEST);
		}
		if (re != null) {
			String maDonHang = (String) re.get("maDonHang");
			String thoiGianGiaoHangStr = (String) re.get("thoiGianGiaoHang");
			Number tongTienGiao = (Number) re.get("tongTienGiao");
			Number tienBaoHiem = (Number) re.get("tienBaoHiem");
//	        Number khoiLuong = (Number) re.get("khoiLuong");
			LocalDateTime thoiGianGiaoHang = null;
			if (thoiGianGiaoHangStr != null) {
				thoiGianGiaoHang = LocalDateTime.parse(thoiGianGiaoHangStr, DateTimeFormatter.ISO_DATE_TIME);
			}

			hoaDonOnline.setGhnCode(maDonHang);
			hoaDonOnline.setThoiGianDuKienGiao(thoiGianGiaoHang);
			hoaDonOnline.setTongPhiGHN(tongTienGiao != null ? tongTienGiao.intValue() : 0);
			hoaDonOnline.setPhiBaoHiemGHN(tienBaoHiem != null ? tienBaoHiem.intValue() : 0);
			hoaDonOnline.setTongKhoiLuong(ghnOrder.getWeight());
			hoaDonOnline.setDaLenDonGHN(true);
			TrangThai t = trangThaiService.getById(13);
			trangThaiHoaDonService.Save(hoaDonOnline, t, "");
			hoaDonOnline.setTrangThai(t);
			hoaDonOnlineRepository.save(hoaDonOnline);
		} else {
			throw new GeneralException("Lên đơn thất bại, vui lòng thử lại", HttpStatus.BAD_REQUEST);
		}
	}

	public GHNOrder getGHNorderFromCartItemForCreateOrder(HoaDonOnline hoaDonOnline) {
		GHNOrder ghnOrder = new GHNOrder();
		// LƯU Ý Ở ĐÂY NHA LÊN LẤY NẾU KỊP THƯỜI GIAN TỐI ƯU ĐƯỢC CÁI HỘP
		// CHIỀU CAO RỘNG DÀI NẰM Ở ĐÂY
		List<CartItem> cart = hoaDonOnline.getChiTietHoaDons().stream().map(d -> {
			CartItem c = new CartItem();
			c.setIdBienThe(d.getBienThe().getId());
			c.setSoLuong(d.getSoLuong());
			return c;
		}).collect(Collectors.toList());
		String address = hoaDonOnline.getDiaChi();
		DongHop dongHop = dongHopService.getToiUu();
		AtomicInteger tongKhoiLuong = new AtomicInteger(0);
		// TÍNH TỔNG KHỐI LƯỢNG
		AtomicReference<Float> tongTienHang = new AtomicReference<Float>(0f);
		List<GHNItem> GHNItem = cart.stream().map(d -> {
			BienThe bienThe = bienTheSerVice.getById(d.getIdBienThe());
			tongKhoiLuong.addAndGet(bienThe.getKhoiLuong() * d.getSoLuong());
			tongTienHang.updateAndGet(v -> v + bienThe.getGia() * d.getSoLuong());
			GHNItem ghnItem = new GHNItem();
			ghnItem.setName(bienThe.getSanPham().getTen() + " - "
					+ (bienThe.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : bienThe.getTen()));
			int result = (int) Math.ceil(bienThe.getGia());
			ghnItem.setPrice(result);
			ghnItem.setWeight(bienThe.getKhoiLuong());
			ghnItem.setQuantity(d.getSoLuong());
			return ghnItem;
		}).collect(Collectors.toList());

		// THÔNG TIN ĐỊA CHỈ
		int idXa = AddressService.getThirdNumberAfterDot(address);
		int idHuyen = AddressService.getSecondNumberAfterDot(address);
		String diChi = AddressService.getTextBeforeDot(address);

		Map<String, Integer> servic = ghnService.getService(1572, idHuyen);
		tongKhoiLuong.addAndGet(dongHop.getCanNangBoSung());

		ghnOrder.setPayment_type_id(GHNConstans.NGUOIMUA_TRAPHI);
		ghnOrder.setRequired_note(GHNConstans.REQUIRED_NOTE_CHOTHUHANG);
		ghnOrder.setHeight(dongHop.getChieuCao());
		ghnOrder.setWidth(dongHop.getChieuRong());
		ghnOrder.setLength(dongHop.getChieuDai());
		ghnOrder.setWeight(tongKhoiLuong.get());
		ghnOrder.setTo_district_id(idHuyen);
		ghnOrder.setTo_ward_code(String.valueOf(idXa));
		ghnOrder.setCod_amount(0);
		ghnOrder.setFrom_district_id(1572);
		ghnOrder.setFrom_ward_code("550113");
		ghnOrder.setItems(GHNItem);
		ghnOrder.setService_id(servic.get("s_id"));
		ghnOrder.setService_type_id(servic.get("s_t_id"));
		int insurance = (int) Math.ceil(tongTienHang.get());
		ghnOrder.setInsurance_value(insurance);
		ghnOrder.setTo_name(hoaDonOnline.getKhachHang().getTen());
		ghnOrder.setTo_phone(hoaDonOnline.getKhachHang().getSodienthoai());
		ghnOrder.setTo_address(diChi);
		return ghnOrder;
	}

	public Map<String, Object> tinhPhi(List<CartItem> cart, String address) {
		if (cart == null || cart.size() == 0 || address == null || address.isBlank()) {
			throw new GeneralException("Thông tin không hợp lệ", HttpStatus.BAD_REQUEST);
		}
		return ghnService.tinhPhiGiaoHang(getGHNorderFromCartItemForCalculatrfee(cart, address));
	}

	public String getPrintGHNOrder(long idHoaDon) {
		HoaDon hoaDon = hoaDonSerVice.getById(idHoaDon);
		if (hoaDon instanceof HoaDonTaiQuay) {
			throw new GeneralException("Thông tin chưa hợp lệ", HttpStatus.BAD_REQUEST);
		}

		return "https://dev-online-gateway.ghn.vn/a5/public-api/printA5?token="
				+ ghnService.getPrintOrder(((HoaDonOnline) hoaDon).getGhnCode());
	}

	@Transactional
	public void CallBackDonHang(String GHNCode) {
		HoaDonOnline hoaDonOnline = hoaDonOnlineRepository.findByGhnCode(GHNCode);
		String sta = ghnService.getOrderDetail(GHNCode).getStatus();
		long s = trangThaiService.anhXaGhnStatus(sta);
		if (s == -1) {
			return;
		} else {
			TrangThai t = trangThaiService.getById(s);
			trangThaiHoaDonService.Save(hoaDonOnline, t, "Chuyển đổi GHN");
			hoaDonOnline.setTrangThai(t);
			hoaDonOnlineRepository.save(hoaDonOnline);
		}

	}

	@Transactional
	public float changeDongHop(int khoiLuong, int idDongHop, long idHoaDon) {
		DongHop dongHop = dongHopService.getDongHopById(idDongHop);
		HoaDonOnline hoaDonOnline = (HoaDonOnline) hoaDonSerVice.getById(idHoaDon);
		hoaDonOnline.setTongKhoiLuong(khoiLuong);
		hoaDonOnline.setDongHop(dongHop);
		hoaDonSerVice.Update(hoaDonOnline);
		GHNOrder ghnOrder = getGHNorderFromCartItemForCalculatrfee(hoaDonOnline.getChiTietHoaDons().stream().map(d -> {
			CartItem c = new CartItem();
			c.setIdBienThe(d.getBienThe().getId());
			c.setSoLuong(d.getSoLuong());
			return c;
		}).collect(Collectors.toList()), hoaDonOnline.getDiaChi());
		Map<String, Object> change = new HashMap<String, Object>();
		change.put("order_code", hoaDonOnline.getGhnCode());
		change.put("weight", khoiLuong);
		change.put("length", dongHop.getChieuDai());
		change.put("width", dongHop.getChieuRong());
		change.put("height", dongHop.getChieuCao());
		ghnService.ChangeInfo(change);
		ghnOrder.setWeight(khoiLuong);
		ghnOrder.setHeight(dongHop.getChieuCao());
		ghnOrder.setWidth(dongHop.getChieuCao());
		ghnOrder.setLength(dongHop.getChieuDai());
		Map<String, Object> k = ghnService.tinhPhiGiaoHang(ghnOrder);
		Map<String, Object> data = (Map<String, Object>) k.get("data");
		float total = ((Number) data.get("total")).floatValue();
		hoaDonOnline.setTongPhiGHN(total);
		hoaDonSerVice.Update(hoaDonOnline);
		return total;
	}

	@Transactional
	public String hoanDonXacNhan(long idHoaDon, int triTra, int traLuon) {
		hoaDonSerVice.huyDonTaiQuay(idHoaDon);
		HoaDonOnline hoaDonOnline = ((HoaDonOnline) hoaDonSerVice.getById(idHoaDon));
		TrangThai t = null;
		if (triTra == 0) {
			if (traLuon == 0) {
				hoaDonOnline.setDaHoanHang(true);
			} else {
				hoaDonOnline.setDaHoanHang(false);
			}
			t = trangThaiService.getById(14);
		} else {
			hoaDonOnline.setDaHoanHang(false);
			t = trangThaiService.getById(15);
		}
		System.out.println(t.getId());
		trangThaiHoaDonService.Save(hoaDonOnline, t, "Hoàn hàng hóa đơn SKINLY");
		hoaDonOnline.setTrangThai(t);
		hoaDonSerVice.Update(hoaDonOnline);
		float giatien = hoaDonOnline.getTongTien();
		long giatienLamTron = (long) (Math.ceil(giatien / 1000.0) * 1000);
		if (triTra == 0 && traLuon == 0) {
			try {
				zaloPayService.refund(hoaDonOnline.getHD_zpTrans_Id() + "", giatienLamTron,
						"Hoàn tiền đơn hàng- " + hoaDonOnline.getId(), hoaDonOnline.getRefund(), null);
			} catch (Exception e) {
				e.printStackTrace();
				hoaDonOnline.setDaHoanHang(false);
				return "Hóa đơn đã được xác nhận nhưng chưa hoàn tiền, vui lòng chuyển trạng thái để xem chi tiết";
			}
		}
		hoaDonSerVice.Update(hoaDonOnline);
		return null;
	}
}
