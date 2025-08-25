package com.example.e_commerce.service;

import java.io.ObjectOutputStream.PutField;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.DTO.request.FilterViewProduct;
import com.example.e_commerce.DTO.request.ItemQuyCachDongGoi;
import com.example.e_commerce.DTO.request.PhieuKiemDTO;
import com.example.e_commerce.DTO.request.SanPham;
import com.example.e_commerce.DTO.response.CartItemLast;
import com.example.e_commerce.DTO.response.DonGiaBan;
import com.example.e_commerce.DTO.response.ParentCartLast;
import com.example.e_commerce.DTO.response.PhieuNhapDTO;
import com.example.e_commerce.DTO.response.SanPhamPhieuNhap;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.contoller.DealController;
import com.example.e_commerce.model.AnhGioiThieu;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.ChiTietPhieuNhap;
import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.model.DonGiaBanHang;
import com.example.e_commerce.model.DongGoiChan;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.HoaDonTaiQuay;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.PhanHoi;
import com.example.e_commerce.model.QuyCachDongGoiLanNhap;
import com.example.e_commerce.model.SanPhamKiem;
import com.example.e_commerce.model.ThongSoCuThe;
import com.example.e_commerce.model.ThueVATSanPham;
import com.example.e_commerce.model.ThuongHieu;
import com.example.e_commerce.model.TruyCap;
import com.example.e_commerce.model.embeded.EChiTietHoaDon;
import com.example.e_commerce.repository.DongGoiChanRepository;
import com.example.e_commerce.repository.PhieuKiemRepository;
import com.example.e_commerce.repository.SanPhamRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SanPhamService {

	@Autowired
	private ThuongHieuService thuongHieuService;

	@Autowired
	private SanPhamKiemService sanPhamKiemService;

	@Autowired
	private ThueVATSanPhamSerVice thueVATSanPhamSerVice;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private BienTheDongGoiChanService bienTheDongGoiChanService;

	@Autowired
	private DanhMucSerVice danhMucSerVice;

	@Autowired
	private HoaDonOnlineService hoaDonOnlineService;

	@Autowired
	private ThongSoCuTheService thongSoCuTheService;

	@Autowired
	private DongGoiChanRepository dongGoiChanRepository;

	@Autowired
	private BienTheSerVice bienTheSerVice;

	@Autowired
	private DonGiaBanHangService donGiaBanHangService;

	@Autowired
	private SanPhamRepository sanPhamRepository;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private AnhGioiThieuService anhGioiThieuService;

	@Autowired
	private BienTheFlashSaleService bienTheFlashSaleService;

	@Autowired
	private DealService dealService;

	@Autowired
	KhuyenMaiTangKemSerivce khuyenMaiTangKemSerivce;

	@Autowired
	private TruyCapService truyCapService;

	@Autowired
	private ChiTietHoaDonService chiTietHoaDonService;

	@Autowired
	private ChiTietPhieuNhapService chiTietPhieuNhapService;

	@Autowired
	private PhanHoiService phanHoiService;

	@Autowired
	private BienTheDealChinhService bienTheDealChinhService;

	@Autowired
	private BienTheDealPhuService bienTheDealPhuService;

	public com.example.e_commerce.model.SanPham getSanPhamById(Long id) {
		return sanPhamRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin sản phẩm"));
	}

	public String check(String d) {
		BienTheDongGoiChan q = bienTheDongGoiChanService.getBienTheDongGoiByMaVach(d);
		if (q != null) {
			if (q.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")) {
				return q.getBienThe().getSanPham().getTen() + " - bản mặc định";
			} else {
				return q.getBienThe().getSanPham().getTen() + " - " + q.getBienThe().getTen() + " - "
						+ q.getDongGoiChan().getTenQuyCach() + "(" + q.getId().getSOLUONG() + ")";
			}

		}
		return null;
	}
	@Transactional
	public void save(SanPham sanPham, List<MultipartFile> anhphu, MultipartFile anhbia,
	                 List<MultipartFile> anhbienthe) {
	    // Validation for variant images
	    if (sanPham.getBienTheKhongLe().size() != (anhbienthe == null ? 0 : anhbienthe.size())) {
	        throw new GeneralException("Ảnh biến thể không đầy đủ", HttpStatus.BAD_REQUEST);
	    }
	    // Validation for cover image
	    if (anhbia == null) {
	        throw new GeneralException("Vui lòng cung cấp ảnh bìa", HttpStatus.BAD_REQUEST);
	    }
	    // Validation for additional images
	    if (anhphu == null || anhphu.size() < 2) {
	        throw new GeneralException("Vui lòng cung cấp hơn một ảnh phụ", HttpStatus.BAD_REQUEST);
	    }

	    // Product information setup
	    com.example.e_commerce.model.SanPham s = new com.example.e_commerce.model.SanPham();
	    s.setTen(sanPham.getTen());
	    s.setMoTa(sanPham.getMoTa());
	    s.setThanhPhan(sanPham.getThanhPhan());
	    s.setCachDung(sanPham.getCachDung());
	    s.setGiaMacDinh(sanPham.getGia());

	    ThuongHieu thuongHieu = thuongHieuService.getById(sanPham.getThuongHieu());
	    s.setThuongHieu(thuongHieu);
	    s.setDanhMuc(danhMucSerVice.getDanhMucById(sanPham.getDanhMuc()));

	    // Set specific attributes
	    s.setThongSoCuThe(new ArrayList<ThongSoCuThe>());
	    sanPham.getThongSo().forEach(data -> {
	        s.getThongSoCuThe().add(thongSoCuTheService.getThongCuTheById(data.getId()));
	    });
	    sanPhamRepository.save(s);

	    // Validation for price and barcode when no variants
	    if (sanPham.getBienTheKhongLe().size() == 0 && sanPham.getGia() == 0) {
	        throw new GeneralException("Vui lòng cung cấp giá sản phẩm", HttpStatus.BAD_REQUEST);
	    }
	    if (sanPham.getBienTheKhongLe().size() == 0 && sanPham.getKhoiLuong() == 0) {
	        throw new GeneralException("Vui lòng cung cấp khối lượng cho sản phẩm", HttpStatus.BAD_REQUEST);
	    }
	    if (sanPham.getBienTheKhongLe().size() == 0
	            && (sanPham.getMaVach() == null || sanPham.getMaVach().equals(""))) {
	        throw new GeneralException("Vui lòng cung cấp mã vạch mặc định", HttpStatus.BAD_REQUEST);
	    }
	    
	    // Initialize thread pool for image uploads
	    ExecutorService executor = Executors.newFixedThreadPool(4);
	    List<Future<String>> uploadFutures = new ArrayList<>();
	    List<AnhGioiThieu> anhGioiThieuList = new ArrayList<>();
	    List<String> variantImageUrls = new ArrayList<>();

	    try {
	        // Upload cover image asynchronously
	        Future<String> coverImageFuture = executor.submit(() -> cloudinaryService.uploadImage(anhbia));
	        uploadFutures.add(coverImageFuture);

	        // Upload additional images (anhphu) concurrently
	        for (MultipartFile file : anhphu) {
	            Future<String> future = executor.submit(() -> cloudinaryService.uploadImage(file));
	            uploadFutures.add(future);
	        }

	        // Upload variant images (anhbienthe) concurrently
	        if (anhbienthe != null) {
	            for (MultipartFile file : anhbienthe) {
	                Future<String> future = executor.submit(() -> cloudinaryService.uploadImage(file));
	                uploadFutures.add(future);
	            }
	        }

	        // Collect results from uploads
	        int variantImageIndex = 0;
	        for (Future<String> future : uploadFutures) {
	            try {
	                String url = future.get(30, TimeUnit.SECONDS);
	                if (future == coverImageFuture) {
	                    s.setAnhBia(url);
	                } else if (variantImageIndex < (anhbienthe != null ? anhbienthe.size() : 0)) {
	                    variantImageUrls.add(url);
	                    variantImageIndex++;
	                } else {
	                    AnhGioiThieu a = new AnhGioiThieu();
	                    a.setSanPham(s);
	                    a.setDuongDan(url);
	                    anhGioiThieuList.add(a);
	                }
	            } catch (Exception e) {
	                throw new GeneralException("Lỗi khi tải ảnh lên: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	            }
	        }

	        // Save additional images to database
	        for (AnhGioiThieu anh : anhGioiThieuList) {
	            anhGioiThieuService.save(anh);
	        }

	        // Save product after setting cover image
	        sanPhamRepository.save(s);

	        // Save VAT
	        ThueVATSanPham thueVATSanPham = thueVATSanPhamSerVice.save(s, sanPham.getThue());

	        // Handle default variant (no variants case)
	        if (sanPham.getBienTheKhongLe().size() == 0) {
	            BienThe bienThe = new BienThe();
	            bienThe.setConSuDung(true);
	            bienThe.setBanLe(false);

	            String check = check(sanPham.getMaVach());
	            if (check != null) {
	                throw new GeneralException("Mã vạch mặc định bị trùng với (sản phẩm- biến thể ): " + check,
	                        HttpStatus.BAD_REQUEST);
	            }
	            bienThe.setTen("DEFAULT_SYSTEM_NAME_CLASSIFY");
	            bienThe.setTiLeBanLe("");
	            bienThe.setKhoiLuong(sanPham.getKhoiLuong());
	            bienThe.setSanPham(s);
	            bienThe.setGia(sanPham.getGia());
	            bienThe.setAnhBia(s.getAnhBia());
	            bienThe.setMaVach(sanPham.getMaVach());
	            bienTheSerVice.save(bienThe);

	            DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
	            donGiaBanHang.setBienThe(bienThe);
	            donGiaBanHang.setGia(sanPham.getGia());
	            donGiaBanHang.setThoiDiem(LocalDateTime.now());
	            donGiaBanHangService.save(donGiaBanHang);

	            DongGoiChan defaul = dongGoiChanRepository.findById(4).orElseThrow(
	                    () -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
	            BienTheDongGoiChan bienTheDongGoiChand = new BienTheDongGoiChan();
	            bienTheDongGoiChand.setBienThe(bienThe);
	            bienTheDongGoiChand.setDongGoiChan(defaul);
	            bienTheDongGoiChand.getId().setSOLUONG(1);
	            bienTheDongGoiChand.setMaVach(sanPham.getMaVach());
	            bienTheDongGoiChanService.save(bienTheDongGoiChand);

	            sanPham.getDongGoiNhap().forEach(p -> {
	                if (p.getSoLuong() <= 1) {
	                    throw new GeneralException("Số lượng quy cách đóng gói: " + p.getTenQuyCach() + "(" + p.getSoLuong()
	                            + ")" + " chưa phù hợp", HttpStatus.BAD_REQUEST);
	                }
	                String check2 = check(p.getMaVach());
	                if (check2 != null) {
	                    throw new GeneralException("Mã vạch quy cách: " + p.getTenQuyCach() + "(" + p.getSoLuong() + ")"
	                            + " bị trùng với (sản phẩm- biến thể ): " + check2, HttpStatus.BAD_REQUEST);
	                }
	                DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
	                        () -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
	                BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
	                bienTheDongGoiChan.setBienThe(bienThe);
	                bienTheDongGoiChan.setDongGoiChan(defaul2);
	                bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
	                bienTheDongGoiChan.setMaVach(p.getMaVach());
	                bienTheDongGoiChanService.save(bienTheDongGoiChan);
	            });
	        } else {
	            // Validate variants
	            for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
	                if (sanPham.getBienTheKhongLe().get(i).getMaVach() == null
	                        || sanPham.getBienTheKhongLe().get(i).getMaVach().equals("")) {
	                    throw new GeneralException(
	                            "Vui lòng cung cấp mã vạch: " + sanPham.getBienTheKhongLe().get(i).getTen(),
	                            HttpStatus.BAD_REQUEST);
	                }
	                String check = check(sanPham.getBienTheKhongLe().get(i).getMaVach());
	                if (check != null) {
	                    throw new GeneralException("Mã vạch biến thể: " + sanPham.getBienTheKhongLe().get(i).getTen()
	                            + " bị trùng với (sản phẩm- biến thể ): " + check, HttpStatus.BAD_REQUEST);
	                }
	                sanPham.getBienTheKhongLe().get(i).getDongGoiNhap().forEach(p -> {
	                    if (p.getSoLuong() <= 1) {
	                        throw new GeneralException("Số lượng quy cách đóng gói: " + p.getTenQuyCach() + "("
	                                + p.getSoLuong() + ")" + " chưa phù hợp", HttpStatus.BAD_REQUEST);
	                    }
	                    String check2 = check(p.getMaVach());
	                    if (check2 != null) {
	                        throw new GeneralException("Mã vạch quy cách: " + p.getTenQuyCach() + "(" + p.getSoLuong() + ")"
	                                + " bị trùng với (sản phẩm- biến thể ): " + check2, HttpStatus.BAD_REQUEST);
	                    }
	                });
	            }

	            // Create variants with pre-uploaded images
	            for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
	                BienThe bienThe = new BienThe();
	                bienThe.setConSuDung(true);
	                bienThe.setBanLe(false);
	                bienThe.setTiLeBanLe("");
	                bienThe.setTen(sanPham.getBienTheKhongLe().get(i).getTen());
	                bienThe.setAnhBia(variantImageUrls.get(i));
	                bienThe.setSanPham(s);
	                bienThe.setKhoiLuong(sanPham.getBienTheKhongLe().get(i).getKhoiLuong());
	                bienThe.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
	                bienThe.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
	                bienTheSerVice.save(bienThe);

	                DongGoiChan defaul = dongGoiChanRepository.findById(4).orElseThrow(
	                        () -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
	                BienTheDongGoiChan bienTheDongGoiChand = new BienTheDongGoiChan();
	                bienTheDongGoiChand.setBienThe(bienThe);
	                bienTheDongGoiChand.setDongGoiChan(defaul);
	                bienTheDongGoiChand.getId().setSOLUONG(1);
	                bienTheDongGoiChand.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
	                bienTheDongGoiChanService.save(bienTheDongGoiChand);

	                sanPham.getBienTheKhongLe().get(i).getDongGoiNhap().forEach(p -> {
	                    DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
	                            () -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
	                    BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
	                    bienTheDongGoiChan.setBienThe(bienThe);
	                    bienTheDongGoiChan.setDongGoiChan(defaul2);
	                    bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
	                    bienTheDongGoiChan.setMaVach(p.getMaVach());
	                    bienTheDongGoiChanService.save(bienTheDongGoiChan);
	                });

	                DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
	                donGiaBanHang.setBienThe(bienThe);
	                donGiaBanHang.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
	                donGiaBanHang.setThoiDiem(LocalDateTime.now());
	                donGiaBanHangService.save(donGiaBanHang);

	                bienTheSerVice.save(bienThe);
	            }
	        }
	    } finally {
	        // Shutdown executor
	        executor.shutdown();
	        try {
	            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
	                executor.shutdownNow();
	            }
	        } catch (InterruptedException e) {
	            executor.shutdownNow();
	            Thread.currentThread().interrupt();
	        }
	    }
	}
//	@Transactional
//	public void save(SanPham sanPham, List<MultipartFile> anhphu, MultipartFile anhbia,
//			List<MultipartFile> anhbienthe) {
//		if (sanPham.getBienTheKhongLe().size() != (anhbienthe == null ? 0 : anhbienthe.size())) {
//			throw new GeneralException("Ảnh biến thể không đầy đủ", HttpStatus.BAD_REQUEST);
//		}
//		if (anhbia == null) {
//			throw new GeneralException("Vui lòng cung cấp ảnh bìa", HttpStatus.BAD_REQUEST);
//		}
//		if (anhphu == null || anhphu.size() < 2) {
//			throw new GeneralException("Vui lòng cung cấp hơn một ảnh phụ", HttpStatus.BAD_REQUEST);
//		}
//		// thông tin sản phẩm
//		com.example.e_commerce.model.SanPham s = new com.example.e_commerce.model.SanPham();
//		s.setTen(sanPham.getTen());
//		s.setMoTa(sanPham.getMoTa());
//		s.setThanhPhan(sanPham.getThanhPhan());
//		s.setCachDung(sanPham.getCachDung());
//		s.setGiaMacDinh(sanPham.getGia());
//
//		ThuongHieu thuongHieu = thuongHieuService.getById(sanPham.getThuongHieu());
//		s.setThuongHieu(thuongHieu);
//		s.setDanhMuc(danhMucSerVice.getDanhMucById(sanPham.getDanhMuc()));
//
//		// set thong số cụ thể
//		s.setThongSoCuThe(new ArrayList<ThongSoCuThe>());
//		sanPham.getThongSo().forEach(data -> {
//			s.getThongSoCuThe().add(thongSoCuTheService.getThongCuTheById(data.getId()));
//		});
//		sanPhamRepository.save(s);
//		if (sanPham.getBienTheKhongLe().size() == 0 && sanPham.getGia() == 0) {
//			throw new GeneralException("Vui lòng cung cấp giá sản phẩm", HttpStatus.BAD_REQUEST);
//		}
//
//		if (sanPham.getBienTheKhongLe().size() == 0
//				&& (sanPham.getMaVach() == null || sanPham.getMaVach().equals(""))) {
//			throw new GeneralException("Vui lòng cung cấp mã vạch mặc định", HttpStatus.BAD_REQUEST);
//		}
//
//		if (sanPham.getBienTheKhongLe().size() == 0) {
//			BienThe bienThe = new BienThe();
//			bienThe.setConSuDung(true);
//			bienThe.setBanLe(false);
//			
//			String check = check(sanPham.getMaVach());
//
//			if (check != null) {
//				throw new GeneralException("Mã vạch mặc định bị trùng với (sản phẩm- biến thể ): " + check,
//						HttpStatus.BAD_REQUEST);
//			}
//			bienThe.setTen("DEFAULT_SYSTEM_NAME_CLASSIFY");
//			bienThe.setTiLeBanLe("");
//			bienThe.setSanPham(s);
//			bienThe.setGia(sanPham.getGia());
//			bienThe.setAnhBia(s.getAnhBia());
//			bienThe.setMaVach(sanPham.getMaVach());
//			bienTheSerVice.save(bienThe);
//			DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
//			donGiaBanHang.setBienThe(bienThe);
//			
//			donGiaBanHang.setGia(sanPham.getGia());
//			donGiaBanHang.setThoiDiem(LocalDateTime.now());
//			donGiaBanHangService.save(donGiaBanHang);
//			DongGoiChan defaul = dongGoiChanRepository.findById(4).orElseThrow(
//					() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
//			BienTheDongGoiChan bienTheDongGoiChand = new BienTheDongGoiChan();
//			bienTheDongGoiChand.setBienThe(bienThe);
//			bienTheDongGoiChand.setDongGoiChan(defaul);
//			bienTheDongGoiChand.getId().setSOLUONG(1);
//			bienTheDongGoiChand.setMaVach(sanPham.getMaVach());
//			bienTheDongGoiChanService.save(bienTheDongGoiChand);
//			sanPham.getDongGoiNhap().forEach(p -> {
//				if (p.getSoLuong() <= 1) {
//					throw new GeneralException("Số lượng quy cách đóng gói: " + p.getTenQuyCach() + "(" + p.getSoLuong()
//							+ ")" + " chưa phù hợp", HttpStatus.BAD_REQUEST);
//				}
//				String check2 = check(p.getMaVach());
//
//				if (check2 != null) {
//					throw new GeneralException("Mã vạch quy cách: " + p.getTenQuyCach() + "(" + p.getSoLuong() + ")"
//							+ " bị trùng với (sản phẩm- biến thể ): " + check2, HttpStatus.BAD_REQUEST);
//				}
//				DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
//						() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
//				BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
//				bienTheDongGoiChan.setBienThe(bienThe);
//				bienTheDongGoiChan.setDongGoiChan(defaul2);
//				bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
//				bienTheDongGoiChan.setMaVach(p.getMaVach());
//				bienTheDongGoiChanService.save(bienTheDongGoiChan);
//			});
//
//		} else {
//			for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
//				if (sanPham.getBienTheKhongLe().get(i).getMaVach() == null
//						|| sanPham.getBienTheKhongLe().get(i).getMaVach() == "") {
//					throw new GeneralException(
//							"Vui lòng cung cấp mã vạch: " + sanPham.getBienTheKhongLe().get(i).getTen(),
//							HttpStatus.BAD_REQUEST);
//
//				}
//				String check = check(sanPham.getBienTheKhongLe().get(i).getMaVach());
//
//				if (check != null) {
//					throw new GeneralException("Mã vạch biến thể: " + sanPham.getBienTheKhongLe().get(i).getTen()
//							+ "bị trùng với (sản phẩm- biến thể ): " + check, HttpStatus.BAD_REQUEST);
//				}
//				sanPham.getBienTheKhongLe().get(i).getDongGoiNhap().forEach(p -> {
//					if (p.getSoLuong() <= 1) {
//						throw new GeneralException("Số lượng quy cách đóng gói: " + p.getTenQuyCach() + "("
//								+ p.getSoLuong() + ")" + " chưa phù hợp", HttpStatus.BAD_REQUEST);
//					}
//					String check2 = check(p.getMaVach());
//
//					if (check2 != null) {
//						throw new GeneralException("Mã vạch quy cách: " + p.getTenQuyCach() + "(" + p.getSoLuong() + ")"
//								+ " bị trùng với (sản phẩm- biến thể ): " + check2, HttpStatus.BAD_REQUEST);
//					}
//
//				});
//
//			}
//		}
//		// ảnh bìa
//		s.setAnhBia(cloudinaryService.uploadImage(anhbia));
//
//		// ảnh phụ
//		anhphu.forEach((d) -> {
//			AnhGioiThieu a = new AnhGioiThieu();
//			a.setSanPham(s);
//			a.setDuongDan(cloudinaryService.uploadImage(d));
//			anhGioiThieuService.save(a);
//		});
//		// save trước sản phẩm
//		sanPhamRepository.save(s);
//		// thuế sản phẩm VAT
//		ThueVATSanPham thueVATSanPham = thueVATSanPhamSerVice.save(s, sanPham.getThue());
//
//		// tạo các biến thể, lưu ảnh biến thể
//
//		for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
//
//			BienThe bienThe = new BienThe();
//			bienThe.setConSuDung(true);
//			bienThe.setBanLe(false);
//			bienThe.setTiLeBanLe("");
//			bienThe.setTen(sanPham.getBienTheKhongLe().get(i).getTen());
//			bienThe.setAnhBia(cloudinaryService.uploadImage(anhbienthe.get(i)));
//			bienThe.setSanPham(s);
//			bienThe.setKhoiLuong(sanPham.getBienTheKhongLe().get(i).getKhoiLuong());
//			bienThe.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
//			bienThe.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
//			bienTheSerVice.save(bienThe);
//			// set thông tin đóng gói nhập
//			// tạo đóng gói mặc định
//
//			DongGoiChan defaul = dongGoiChanRepository.findById(4).orElseThrow(
//					() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
//			BienTheDongGoiChan bienTheDongGoiChand = new BienTheDongGoiChan();
//			bienTheDongGoiChand.setBienThe(bienThe);
//			bienTheDongGoiChand.setDongGoiChan(defaul);
//			bienTheDongGoiChand.getId().setSOLUONG(1);
//			bienTheDongGoiChand.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
//			bienTheDongGoiChanService.save(bienTheDongGoiChand);
//			sanPham.getBienTheKhongLe().get(i).getDongGoiNhap().forEach(p -> {
//
//				DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
//						() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
//
//				BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
//				bienTheDongGoiChan.setBienThe(bienThe);
//				bienTheDongGoiChan.setDongGoiChan(defaul2);
//				bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
//				bienTheDongGoiChan.setMaVach(p.getMaVach());
//				bienTheDongGoiChanService.save(bienTheDongGoiChan);
//			});
//			// giá biến thể
//			DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
//			donGiaBanHang.setBienThe(bienThe);
//			donGiaBanHang.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
//			donGiaBanHang.setThoiDiem(LocalDateTime.now());
//			donGiaBanHangService.save(donGiaBanHang);
//
//			bienTheSerVice.save(bienThe);
//		}
//
//	}
	
	 public Map<String, Object> tinhGiaVonHaoHut(int idBienThe, LocalDateTime bd, LocalDateTime kt, int active) {
	        // Tạo Map để chứa kết quả
	        Map<String, Object> ketQua = new HashMap<>();

	        // Lấy thông tin biến thể
	        BienThe bienThe = bienTheSerVice.getById(idBienThe);
	        if (bienThe == null) {
	            throw new IllegalArgumentException("Biến thể không tồn tại với ID: " + idBienThe);
	        }

	        // Lấy danh sách sản phẩm hao hụt trong khoảng thời gian bd -> kt
	        List<SanPhamKiem> danhSachHaoHut = new ArrayList<SanPhamKiem>();
	        
	        danhSachHaoHut=sanPhamKiemService.getSanPhamKiemOfBienTheCondition(bd, kt, bienThe.getId(),active);
	        
	        
	        System.out.println("TỔNG LƯỢNG DANH SÁCH HAO HỤT: "+danhSachHaoHut.size());
	        long soLuongHaoHut = danhSachHaoHut.stream().mapToLong(SanPhamKiem::getSoLuong).sum();
	        
	        // Nếu không có hao hụt, trả về Map với số lượng và giá trị bằng 0
	        if (soLuongHaoHut == 0) {
	            ketQua.put("tongSoLuongHao", 0L);
	            ketQua.put("tongGiaTriHao", 0f);
	            return ketQua;
	        }

	        // Lấy số lượng hao hụt từ trước thời điểm bd
	        List<SanPhamKiem> danhSachHaoHutTruoc = sanPhamKiemService.getSanPhamKiemOfBienThe(
	                LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), bienThe.getId());
	        long soLuongHaoHutTruoc = danhSachHaoHutTruoc.stream().mapToLong(SanPhamKiem::getSoLuong).sum();

	        // Lấy danh sách phiếu nhập đến thời điểm kt
	        List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapService.getAllChiTietPhieuNhapBienThe(bienThe.getId(), kt);
	        
	        // Gọi hàm getNSanPhamBanCuoiFIFO để lấy giá vốn cho hao hụt (tinhChoHoaDon = false)
	        List<Map<String, Object>> danhSachGiaVonHaoHut = getNSanPhamBanCuoiFIFO(
	                dsNhap, soLuongHaoHutTruoc, false, null, danhSachHaoHut);

	        // Tính tổng giá trị hao hụt
	        float tongGiaTriHao = 0f;
	        for (Map<String, Object> item : danhSachGiaVonHaoHut) {
	            long soLuong = ((Number) item.get("soLuong")).longValue();
	            float gia = (float) item.get("gia");
	            tongGiaTriHao += soLuong * gia;
	        }

	        // Đưa kết quả vào Map
	        ketQua.put("tongSoLuongHao", soLuongHaoHut);
	        ketQua.put("tongGiaTriHao", tongGiaTriHao);

	        return ketQua;
	    }

	@Transactional
	public void update(com.example.e_commerce.model.SanPham sanPham) {
		sanPhamRepository.save(sanPham);
	}

	@Transactional
	public void deleteSanPhamThongSoBySanPham(List<Long> idsp, List<Long> idtsct) {
		if (idsp.size() != 0 && idtsct.size() != 0) {
			sanPhamRepository.deleteSanPhamThongSoBySanPham(idsp, idtsct);
		}
	}

	public Map<String, Object> getManagerProduct(boolean conSuDung, String ten, int hetHang, int idDanhMuc, int page) {
		List<DanhMuc> dm= new ArrayList<DanhMuc>();
		if(idDanhMuc==0) {
			dm=danhMucSerVice.findAll();
		}
		else {
			danhMucSerVice.getAllChildDanhMuc(dm, danhMucSerVice.getDanhMucById(idDanhMuc));
		}
		
		Page<com.example.e_commerce.model.SanPham> danhSach = sanPhamRepository.getSanPhamFilter(conSuDung, 
				dm==null ?null :(dm.stream().map(d->d.getId()).collect(Collectors.toList())),
				ten, 1, 25, hetHang, PageRequest.of(page, 5));
		List<com.example.e_commerce.model.SanPham> sanPhams = danhSach.getContent();
		List<Long> f = Arrays.asList((long) 2, (long) 3);
		List<Map<String, Object>> result = sanPhams.stream().map(data -> {
			Map<String, Object> itemSanPham = new HashMap<>();
			itemSanPham.put("id", data.getId());
			itemSanPham.put("ten", data.getTen());
			itemSanPham.put("anhGioiThieu", data.getAnhBia());
			itemSanPham.put("conSuDung", data.isConDung());
			List<BienThe> udo2 = data.getOnlyNotDefault(1);
			List<Map<String, Object>> danhSachBienThe = data.getAllBienTheNotCheckActive(true).stream().map(dat -> {
				Map<String, Object> dataBienThe = new HashMap<>();
				List<ChiTietHoaDon> c = bienTheSerVice.get(dat.getId(), null);
				long y = c.stream().mapToLong(o -> o.getSoLuong()).sum();
				int tongHaoHut= soLuongHaoHienGio(dat);
				dataBienThe.put("id", dat.getId());
				dataBienThe.put("ten", dat.getTen());
				dataBienThe.put("anhGioiThieu", dat.getAnhBia());
				dataBienThe.put("gia", dat.getGia());
				dataBienThe.put("daBan", y);
				dataBienThe.put("haoDuKien", tongHaoHut);
				dataBienThe.put("notUpdate", false);
				if (udo2.size() != 0 && dat.getTen().equals("Mặc định")) {
					dataBienThe.put("notUpdate", true);
				}
				if (dat.isConSuDung() == false && !dat.getTen().equals("Mặc định")) {
					dataBienThe.put("notUpdate", true);
				}
				dataBienThe.put("soLuongKho", dat.getSoLuongKho());
				return dataBienThe;
			}).collect(Collectors.toList());

			itemSanPham.put("bienThe", danhSachBienThe);
			return itemSanPham;
		}).collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		response.put("data", result);
		response.put("totalPages", danhSach.getTotalPages());
		response.put("totalElements", danhSach.getTotalElements());
		response.put("currentPage", danhSach.getNumber());

		return response;
	}

	@Transactional
	public void Update(SanPham sanPham, List<MultipartFile> anhPhanLoaiNew, List<MultipartFile> anhGioiThieuNew,
			MultipartFile anhBiaNew, Long id) {
		com.example.e_commerce.model.SanPham s = getSanPhamById(id);
		s.setTen(sanPham.getTen());
		s.setMoTa(sanPham.getMoTa());
		s.setThanhPhan(sanPham.getThanhPhan());
		s.setCachDung(sanPham.getCachDung());
		ThuongHieu thuongHieu = thuongHieuService.getById(sanPham.getThuongHieu());
		s.setThuongHieu(thuongHieu);
		s.setDanhMuc(danhMucSerVice.getDanhMucById(sanPham.getDanhMuc()));
		s.setThuevat(sanPham.getThue());
		if (anhBiaNew != null) {
			s.setAnhBia(cloudinaryService.uploadImage(anhBiaNew));
			;
		}
		s.setDanhMuc(danhMucSerVice.getDanhMucById(sanPham.getDanhMuc()));
		// xử lý lưu phần thông số

		List<ThongSoCuThe> danhSachCu = s.getThongSoCuThe();
		danhSachCu.removeAll(danhSachCu);
		sanPham.getThongSo().forEach(data -> {
			s.getThongSoCuThe().add(thongSoCuTheService.getThongCuTheById(data.getId()));
		});

		// xử lý ảnh giới thiệu
		int soAnhHienTai = s.getAnhGioiThieus().size();
		int soAnhThem = anhGioiThieuNew != null ? anhGioiThieuNew.size() : 0;
		int soAnhXoa = sanPham.getAnhGioiThieuXoa() != null ? sanPham.getAnhGioiThieuXoa().size() : 0;

		int tongAnhSauCapNhat = soAnhHienTai + soAnhThem - soAnhXoa;

		if (tongAnhSauCapNhat < 3 || tongAnhSauCapNhat > 9) {
			throw new GeneralException("Số lượng ảnh giới thiệu phải lớn hơn 3 và ít hơn 9 ảnh",
					HttpStatus.BAD_REQUEST);
		}
		if (anhGioiThieuNew != null) {
			anhGioiThieuNew.forEach((d) -> {
				AnhGioiThieu a = new AnhGioiThieu();
				a.setSanPham(s);
				a.setDuongDan(cloudinaryService.uploadImage(d));
				anhGioiThieuService.save(a);
			});
		}
		sanPham.getAnhGioiThieuXoa().forEach(d -> {
			anhGioiThieuService.delete(d);
		});

		// xử lý phân loại giá mặc định
		List<Integer> idBienThe = s.getOnlyNotDefault(0).stream().map(d -> d.getId()).collect(Collectors.toList());
		List<BienThe> bienTheCu = s.getOnlyNotDefault(0);
		List<com.example.e_commerce.DTO.request.BienThe> bienTheTrung = sanPham.getBienTheKhongLe().stream()
				.filter(d -> {
					return idBienThe.contains(d.getId());
				}).collect(Collectors.toList());
		List<com.example.e_commerce.DTO.request.BienThe> bienTheKhongTrung = sanPham.getBienTheKhongLe().stream()
				.filter(d -> {
					return !(idBienThe.contains(d.getId()));
				}).collect(Collectors.toList());

		BienThe bienThe = s.getbienTheDefault();
		if (bienTheKhongTrung.size() == 0 && bienTheTrung.stream().filter(d -> d.isConsuDung()).count() == 0) {
			if(sanPham.getKhoiLuong()==0) {
				throw new GeneralException("Vui lòng cung cấp Khối lượng cho  mặc định", HttpStatus.BAD_REQUEST);
			}
			if (sanPham.getGia() == 0) {
				throw new GeneralException("Vui lòng cung cấp giá cho sản phẩm mặc định", HttpStatus.BAD_REQUEST);
			}
			s.getOnlyNotDefault(0).forEach(j -> {
				j.setConSuDung(false);
				bienTheSerVice.save(j);
			});
			if (sanPham.getMaVach() == null || sanPham.getMaVach().trim().equals("")) {
				throw new GeneralException("Vui lòng cung cấp mã vạch mặc định khác rỗng", HttpStatus.BAD_REQUEST);
			}
			if (bienThe == null) {
				bienThe = new BienThe();
				bienThe.setConSuDung(true);
				bienThe.setBanLe(false);
				bienThe.setTen("DEFAULT_SYSTEM_NAME_CLASSIFY");
				bienThe.setKhoiLuong(sanPham.getKhoiLuong());
				bienThe.setTiLeBanLe("");
				bienThe.setSanPham(s);
				bienThe.setGia(sanPham.getGia());
				s.setGiaMacDinh(sanPham.getGia());
				bienThe.setAnhBia(s.getAnhBia());
				// lưu mới maVach
				DongGoiChan defaul2 = dongGoiChanRepository.findById(1).orElseThrow(
						() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
				BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
				bienTheDongGoiChan.setBienThe(bienThe);
				bienTheDongGoiChan.setDongGoiChan(defaul2);
				bienTheDongGoiChan.getId().setSOLUONG(1);
				bienTheDongGoiChan.setMaVach(sanPham.getMaVach());
				bienTheDongGoiChanService.save(bienTheDongGoiChan);
			} else {
				bienThe.setConSuDung(true);
				bienThe.setBanLe(false);
				bienThe.setKhoiLuong(sanPham.getKhoiLuong());
//				bienThe.setAnhBia(s.getAnhBia());
				System.out.println(s.getbienTheDefault().getMaVach());
				if (s.getbienTheDefault().getMaVach().equals(sanPham.getMaVach()) == false) {
					BienTheDongGoiChan test = bienTheDongGoiChanService
							.getBienTheDongGoiByMaVach(s.getbienTheDefault().getMaVach());

					boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(test.getId().getSOLUONG(), bienThe.getId(),
							test.getDongGoiChan().getId());
					if (kiemTra == true) {
						throw new GeneralException("Mã vạch mặc định đã được sử dụng nhập hàng, không thể thay đổi",
								HttpStatus.BAD_REQUEST);
					}
					String check = check(sanPham.getMaVach());
					if (check != null) {
						throw new GeneralException("Mã vạch mặc định " + "bị trùng với (sản phẩm- biến thể ): " + check,
								HttpStatus.BAD_REQUEST);
					}
					if (sanPham.getMaVach() == null || sanPham.getMaVach().trim().equals("")) {
						throw new GeneralException("Vui lòng cung cấp mã vạch mặc định khác rỗng",
								HttpStatus.BAD_REQUEST);
					}
					bienThe.setMaVach(sanPham.getMaVach());
					test.setMaVach(sanPham.getMaVach());
					bienTheDongGoiChanService.save(test);

				}
				bienTheSerVice.save(bienThe);
			}

			// xử lý giá biến thể mặc định
			if (sanPham.getGia() != bienThe.getGia()) {
				if (sanPham.getGia() == 0) {
					throw new GeneralException("Vui lòng cung cấp giá cho sản phẩm mặc định", HttpStatus.BAD_REQUEST);
				}
				DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
				donGiaBanHang.setBienThe(bienThe);
				donGiaBanHang.setGia(sanPham.getGia());
				donGiaBanHang.setThoiDiem(LocalDateTime.now());
				donGiaBanHangService.save(donGiaBanHang);
			}
			s.setGiaMacDinh(sanPham.getGia());
			bienThe.setGia(s.getGiaMacDinh());
			sanPhamRepository.save(s);
			bienTheSerVice.save(bienThe);
			// quản lys thong tin quy cach nhap
			List<ItemQuyCachDongGoi> itemQuyCachDongGois = sanPham.getDongGoiNhap();
			List<BienTheDongGoiChan> danhSachDongGoiCu = bienThe.getBienTheDongGoiChans();
			Set<String> stringitemQuyCachDongGois = itemQuyCachDongGois.stream().map(d -> d.getMaVach())
					.collect(Collectors.toSet());
			Set<String> stringdanhSachDongGoiCu = new HashSet<String>();
			if(danhSachDongGoiCu!=null) {
				danhSachDongGoiCu.stream().map(d -> d.getMaVach())
				.collect(Collectors.toSet());
			}
			// danh sách mới thêm vào
			List<ItemQuyCachDongGoi> news = itemQuyCachDongGois.stream()
					.filter(d -> !stringdanhSachDongGoiCu.contains(d.getMaVach())).collect(Collectors.toList());

			// danh sách bị xóa
			List<BienTheDongGoiChan> deletes = new ArrayList<BienTheDongGoiChan>();
			if(danhSachDongGoiCu!=null) {
				deletes=danhSachDongGoiCu.stream().filter(p -> p.getDongGoiChan().getId() != 4)
						.filter(d -> !stringitemQuyCachDongGois.contains(d.getMaVach())).collect(Collectors.toList());

			}
			// danh sách cũ còn tồn tại (update)
			List<ItemQuyCachDongGoi> olds = itemQuyCachDongGois.stream()
					.filter(d -> stringdanhSachDongGoiCu.contains(d.getMaVach())).collect(Collectors.toList());
			// thêm quy cách đóng gói mới
			news.forEach(p -> {
				String check = check(p.getMaVach());
				if (check != null) {
					throw new GeneralException("Mã vạch quy cách đóng gói: " + p.getTenQuyCach() + "(" + p.getSoLuong()
							+ ")" + "bị trùng với (sản phẩm- biến thể ): " + check, HttpStatus.BAD_REQUEST);
				}
				DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
						() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
				BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
				bienTheDongGoiChan.setBienThe(s.getbienTheDefault());
				bienTheDongGoiChan.setDongGoiChan(defaul2);
				if (p.getMaVach() == null || p.getMaVach().trim().equals("")) {
					throw new GeneralException("Vui lòng cung cấp mã vạch cho quy cách nhập: " + p.getTenQuyCach()
							+ " (" + p.getSoLuong() + ")", HttpStatus.BAD_REQUEST);
				}
				bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
				bienTheDongGoiChan.setMaVach(p.getMaVach());
				bienTheDongGoiChanService.save(bienTheDongGoiChan);
			});
			// xử lý quy cách đóng gói cũ
			olds.stream().forEach(p -> {
				BienTheDongGoiChan bk = bienTheDongGoiChanService.getBienTheDongGoiByMaVach(p.getMaVach());

				if (p.getSoLuong() != bk.getId().getSOLUONG()) {
					boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(bk.getId().getSOLUONG(),
							s.getbienTheDefault().getId(), bk.getDongGoiChan().getId());
					if (kiemTra == true) {
						throw new GeneralException("Mã vạch quy cách đóng gói: " + bk.getDongGoiChan().getTenQuyCach()
								+ "(" + bk.getId().getSOLUONG() + ")"
								+ " đã được sử dụng nhập hàng, không thể thay đổi", HttpStatus.BAD_REQUEST);
					}
					bk.getId().setSOLUONG(p.getSoLuong());
					bienTheDongGoiChanService.save(bk);
				}
			});
			// xử lý xóa
			deletes.stream().forEach(p -> {
				boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(p.getId().getSOLUONG(),
						s.getbienTheDefault().getId(), p.getDongGoiChan().getId());
				if (kiemTra == true) {
					throw new GeneralException(
							"Mã vạch quy cách đóng gói" + p.getDongGoiChan().getTenQuyCach() + "("
									+ p.getId().getSOLUONG() + ")" + " đã được sử dụng nhập hàng, không thể thay đổi",
							HttpStatus.BAD_REQUEST);
				}
				bienTheDongGoiChanService.delete(p);
			});

		}
		// cập nhật thông tin biến thể cũ
		for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
			for (int u = 0; u < bienTheCu.size(); u++) {
				BienThe d = bienTheCu.get(u);
				if (sanPham.getBienTheKhongLe().get(i).getId() == d.getId()) {
					if (sanPham.getBienTheKhongLe().get(i).getTen().equals("")) {
						throw new GeneralException("Tên phân loại không được để trống", HttpStatus.BAD_REQUEST);
					}
					d.setTen(sanPham.getBienTheKhongLe().get(i).getTen());
					d.setKhoiLuong(sanPham.getBienTheKhongLe().get(i).getKhoiLuong());
					if (sanPham.getBienTheKhongLe().get(i).getGia() != d.getGia()) {
						if (d.getGia() == 0) {
							throw new GeneralException("Vui lòng cung cấp giá cho phân loại: "
									+ sanPham.getBienTheKhongLe().get(i).getTen(), HttpStatus.BAD_REQUEST);
						}
						DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
						donGiaBanHang.setBienThe(d);
						donGiaBanHang.setGia(sanPham.getGia());
						donGiaBanHang.setThoiDiem(LocalDateTime.now());
					
						donGiaBanHangService.save(donGiaBanHang);
						d.setGia(i);

						d.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
					}
					d.setConSuDung(sanPham.getBienTheKhongLe().get(i).isConsuDung());
					if (anhPhanLoaiNew.size() != 0 && anhPhanLoaiNew.get(i).isEmpty() == false) {
						d.setAnhBia(cloudinaryService.uploadImage(anhPhanLoaiNew.get(i)));
					}

					// lưu thông tin mã vạch

					com.example.e_commerce.DTO.request.BienThe be = sanPham.getBienTheKhongLe().get(i);

					// lưu thông tin của mã vạch mặc định trước
					if (d.getMaVach().equals(be.getMaVach()) == false) {
						if (d.getMaVach() == null || d.getMaVach().trim().equals("")) {
							throw new GeneralException("Vui lòng cung cấp mã vạch cho phân loại: " + be.getTen(),
									HttpStatus.BAD_REQUEST);
						}
						BienTheDongGoiChan test = bienTheDongGoiChanService.getBienTheDongGoiByMaVach(d.getMaVach());

						boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(test.getId().getSOLUONG(), d.getId(),
								test.getDongGoiChan().getId());
						if (kiemTra == true) {
							throw new GeneralException("Mã vạch phân loại: " + d.getTen()
									+ "đã được sử dụng nhập hàng, không thể thay đổi", HttpStatus.BAD_REQUEST);
						}
						String check = check(sanPham.getMaVach());
						if (check != null) {
							throw new GeneralException(
									"Mã vạch phân loại: " + d.getTen() + "bị trùng với (sản phẩm- biến thể ): " + check,
									HttpStatus.BAD_REQUEST);
						}

						d.setMaVach(sanPham.getMaVach());
						test.setMaVach(sanPham.getMaVach());
						bienTheDongGoiChanService.save(test);

					}
					List<ItemQuyCachDongGoi> itemQuyCachDongGois = be.getDongGoiNhap();
					List<BienTheDongGoiChan> danhSachDongGoiCu = d.getBienTheDongGoiChans();
					Set<String> stringitemQuyCachDongGois = itemQuyCachDongGois.stream().map(m -> m.getMaVach())
							.collect(Collectors.toSet());
					Set<String> stringdanhSachDongGoiCu = danhSachDongGoiCu.stream().map(m -> m.getMaVach())
							.collect(Collectors.toSet());

					List<ItemQuyCachDongGoi> news = be.getDongGoiNhap().stream()
							.filter(m -> !stringdanhSachDongGoiCu.contains(m.getMaVach())).collect(Collectors.toList());

					// danh sách bị xóa
					List<BienTheDongGoiChan> deletes = d.getBienTheDongGoiChans().stream()
							.filter(p -> p.getDongGoiChan().getId() != 4)
							.filter(m -> !stringitemQuyCachDongGois.contains(m.getMaVach()))
							.collect(Collectors.toList());

					// danh sách cũ còn tồn tại (update)
					List<ItemQuyCachDongGoi> olds = itemQuyCachDongGois.stream()
							.filter(m -> stringdanhSachDongGoiCu.contains(m.getMaVach())).collect(Collectors.toList());

					// thêm quy cách đóng gói mới
					news.forEach(p -> {
						String check = check(p.getMaVach());
						if (check != null) {
							throw new GeneralException("Mã vạch quy cách đóng gói: " + p.getTenQuyCach() + "("
									+ p.getSoLuong() + ")" + "bị trùng với (sản phẩm- biến thể ): " + check,
									HttpStatus.BAD_REQUEST);
						}
						if (p.getMaVach() == null || p.getMaVach().trim().equals("")) {
							throw new GeneralException("Vui lòng cung cấp mã vạch cho quy cách nhập: "
									+ p.getTenQuyCach() + " (" + p.getSoLuong() + ")", HttpStatus.BAD_REQUEST);
						}
						DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
								() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
						BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
						bienTheDongGoiChan.setBienThe(d);
						bienTheDongGoiChan.setDongGoiChan(defaul2);
						bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
						bienTheDongGoiChan.setMaVach(p.getMaVach());
						bienTheDongGoiChanService.save(bienTheDongGoiChan);
					});
					// xử lý quy cách đóng gói cũ
					olds.stream().forEach(p -> {
						BienTheDongGoiChan bk = bienTheDongGoiChanService.getBienTheDongGoiByMaVach(p.getMaVach());

						if (p.getSoLuong() != bk.getId().getSOLUONG()) {
							boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(bk.getId().getSOLUONG(), d.getId(),
									bk.getDongGoiChan().getId());
							if (kiemTra == true) {
								throw new GeneralException(
										"Mã vạch quy cách đóng gói: " + bk.getDongGoiChan().getTenQuyCach() + "("
												+ bk.getId().getSOLUONG() + ")"
												+ " đã được sử dụng nhập hàng, không thể thay đổi",
										HttpStatus.BAD_REQUEST);
							}
							bk.getId().setSOLUONG(p.getSoLuong());
							bienTheDongGoiChanService.save(bk);
						}
					});
					// xử lý xóa
					deletes.stream().forEach(p -> {
						boolean kiemTra = chiTietPhieuNhapService.kiemTraTonTai(p.getId().getSOLUONG(), d.getId(),
								p.getDongGoiChan().getId());
						if (kiemTra == true) {
							throw new GeneralException("Mã vạch quy cách đóng gói" + p.getDongGoiChan().getTenQuyCach()
									+ "(" + p.getId().getSOLUONG() + ")"
									+ " đã được sử dụng nhập hàng, không thể thay đổi", HttpStatus.BAD_REQUEST);
						}
						bienTheDongGoiChanService.delete(p);
					});
					bienTheSerVice.save(d);
				}
			}
		}
		// thêm thông tin biến thể mới

		for (int i = 0; i < sanPham.getBienTheKhongLe().size(); i++) {
			if (!idBienThe.contains(sanPham.getBienTheKhongLe().get(i).getId())) {
				BienThe bienThey = new BienThe();
				bienThey.setConSuDung(true);
				bienThey.setBanLe(false);
				bienThey.setTiLeBanLe("");
				bienThey.setTen(sanPham.getBienTheKhongLe().get(i).getTen());
				if (anhPhanLoaiNew.size() != 0 && anhPhanLoaiNew.get(i).isEmpty() == false) {
					bienThey.setAnhBia(cloudinaryService.uploadImage(anhPhanLoaiNew.get(i)));
				} else {
					throw new GeneralException("Vui lòng cung cấp đầy đủ ảnh cho biến thể", HttpStatus.BAD_REQUEST);
				}
				bienThey.setSanPham(s);
				bienThey.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
				bienThey.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
				if (sanPham.getBienTheKhongLe().get(i).getMaVach() == null
						|| sanPham.getBienTheKhongLe().get(i).getMaVach().trim().equals("")) {
					throw new GeneralException(
							"Vui lòng cung cấp mã vạch cho phân loại: " + sanPham.getBienTheKhongLe().get(i).getTen(),
							HttpStatus.BAD_REQUEST);
				}

				bienTheSerVice.save(bienThey);

				// giá biến thể
				if (sanPham.getBienTheKhongLe().get(i).getGia() <= 0) {
					throw new GeneralException("Vui lòng cung cấp giá phân loại lớn hơn 0 ", HttpStatus.BAD_REQUEST);
				}
				DonGiaBanHang donGiaBanHang = new DonGiaBanHang();
				donGiaBanHang.setBienThe(bienThey);

				donGiaBanHang.setGia(sanPham.getBienTheKhongLe().get(i).getGia());
				donGiaBanHang.setThoiDiem(LocalDateTime.now());
				donGiaBanHangService.save(donGiaBanHang);
				// tạo đóng gói mặc định

				String check = check(sanPham.getBienTheKhongLe().get(i).getMaVach());

				if (check != null) {
					throw new GeneralException("Mã vạch biến thể: " + sanPham.getBienTheKhongLe().get(i).getTen()
							+ "bị trùng với (sản phẩm- biến thể ): " + check, HttpStatus.BAD_REQUEST);
				}
				DongGoiChan defaul = dongGoiChanRepository.findById(4).orElseThrow(
						() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
				BienTheDongGoiChan bienTheDongGoiChand = new BienTheDongGoiChan();
				bienTheDongGoiChand.setBienThe(bienThey);
				bienTheDongGoiChand.setDongGoiChan(defaul);
				bienTheDongGoiChand.getId().setSOLUONG(1);
				bienTheDongGoiChand.setMaVach(sanPham.getBienTheKhongLe().get(i).getMaVach());
				bienTheDongGoiChanService.save(bienTheDongGoiChand);
				sanPham.getBienTheKhongLe().get(i).getDongGoiNhap().forEach(p -> {
					String check2 = check(p.getMaVach());
					if (p.getMaVach() == null || p.getMaVach().trim().equals("")) {
						throw new GeneralException("Vui lòng cung cấp mã vạch cho quy cách nhập: " + p.getTenQuyCach()
								+ " (" + p.getSoLuong() + ")", HttpStatus.BAD_REQUEST);
					}
					if (check2 != null) {
						throw new GeneralException("Mã vạch quy cách nhập: " + p.getTenQuyCach() + "(" + p.getSoLuong()
								+ ") " + "bị trùng với (sản phẩm- biến thể ): " + check, HttpStatus.BAD_REQUEST);
					}
					DongGoiChan defaul2 = dongGoiChanRepository.findById(p.getId()).orElseThrow(
							() -> new GeneralException("Không tìm thấy biến thể đóng gói", HttpStatus.BAD_REQUEST));
					BienTheDongGoiChan bienTheDongGoiChan = new BienTheDongGoiChan();
					bienTheDongGoiChan.setBienThe(bienThey);
					bienTheDongGoiChan.setDongGoiChan(defaul2);
					bienTheDongGoiChan.getId().setSOLUONG(p.getSoLuong());
					bienTheDongGoiChan.setMaVach(p.getMaVach());
					bienTheDongGoiChanService.save(bienTheDongGoiChan);
				});
				bienTheSerVice.save(bienThey);
			}
		}

		sanPhamRepository.save(s);
//		BienThe bienThe= sanPhamRepository.getBienTheOfSanPhamDefault(id, false) ;

	}

	public Page<Map<String, Object>> getSanPhamChoKhuyenMai(int id, String ten, int trang, String Task) {
		Pageable pageable = PageRequest.of(trang, 5);
		Page<com.example.e_commerce.model.SanPham> pageSanPham = sanPhamRepository.getAllSanPham(true, id, ten,
				pageable);
		List<com.example.e_commerce.model.SanPham> danhSachSanPham = pageSanPham.getContent();
		List<Map<String, Object>> content = new ArrayList<>();

		for (com.example.e_commerce.model.SanPham sp : danhSachSanPham) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", sp.getId());
			map.put("anhSanPham", sp.getAnhBia());
			map.put("ten", sp.getTen());

			long soLuongTrongKho = 0;
			float giaMin = 1000000000;
			float giaMax = -30;

			List<BienThe> bienThe = sp.getBienTheOfConditionCheckActive(true);

			for (BienThe bt : bienThe) {
				float gia = bt.getGia();
				giaMin = Math.min(giaMin, gia);
				giaMax = Math.max(giaMax, gia);
				soLuongTrongKho += bt.getSoLuongKho();
			}

			List<Map<String, Object>> bienthe = bienThe.stream().map(dd -> {
				Map<String, Object> dat = new HashMap<>();
				dat.put("ten", dd.getTen());
				dat.put("hinhAnh", dd.getAnhBia());
				dat.put("gia", dd.getGia());
				dat.put("soLuongKho", dd.getSoLuongKho());
				dat.put("id", dd.getId());
				List<Map<String, Object>> danhSachFlashSale = new ArrayList<Map<String, Object>>();
				if (Task.equals("flash")) {
					danhSachFlashSale = bienTheFlashSaleService.getBienTheFlashSaleOfBienTHe(dd.getId()).stream()
							.map(di -> {
								if (di.getSoLuongGioiHan() - di.getSoLuongDaDung() > 0
										&& di.getFlashSale().isConSuDung() && di.isConSuDung()) {
									Map<String, Object> mi = new HashMap<>();
									mi.put("thoiGianChay", di.getFlashSale().getThoiGianApDung());
									mi.put("thoiGianNgung", di.getFlashSale().getThoiGianNgung());
									mi.put("thoiDiemBatDau", di.getFlashSale().getGioBatDau());
									mi.put("thoiDiemKetThuc", di.getFlashSale().getGioKetThuc());
									mi.put("giaTriGiam", di.getGiaTriGiam());
									mi.put("idFS", di.getFlashSale().getId());
									mi.put("id", di.getFlashSale().getId());
									return mi;
								}
								return null;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					dat.put("danhSachFlasSale", danhSachFlashSale);
					List<Map<String, Object>> bienTheDealChinh = dealService.getDealChinhOfBienThe(dd.getId()).stream()
							.map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					List<Map<String, Object>> bienTheDealPhu = dealService.getDealPhuOfBienThe(dd.getId()).stream()
							.map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					dat.put("dealChinh", bienTheDealChinh);
					dat.put("dealPhu", bienTheDealPhu);
				} else if (Task.equals("deal")) {
					danhSachFlashSale = bienTheFlashSaleService.getBienTheFlashSaleOfBienTHe(dd.getId()).stream()
							.map(di -> {
								if (di.getSoLuongGioiHan() - di.getSoLuongDaDung() > 0
										&& di.getFlashSale().isConSuDung() && di.isConSuDung()) {
									Map<String, Object> mi = new HashMap<>();
									mi.put("thoiGianChay", di.getFlashSale().getThoiGianApDung());
									mi.put("thoiGianNgung", di.getFlashSale().getThoiGianNgung());
									mi.put("thoiDiemBatDau", di.getFlashSale().getGioBatDau());
									mi.put("thoiDiemKetThuc", di.getFlashSale().getGioKetThuc());
									mi.put("giaTriGiam", di.getGiaTriGiam());
									mi.put("idFS", di.getFlashSale().getId());
									mi.put("id", di.getFlashSale().getId());
									return mi;
								}
								return null;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					dat.put("danhSachFlasSale", danhSachFlashSale);
					List<Map<String, Object>> bienTheDealChinh = dealService.getDealChinhOfBienThe(dd.getId()).stream()
							.map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					List<Map<String, Object>> bienTheDealPhu = dealService.getDealPhuOfBienThe(dd.getId()).stream()
							.map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					dat.put("dealChinh", bienTheDealChinh);
					dat.put("dealPhu", bienTheDealPhu);
				} else if (Task.equals("bonus")) {

					List<Map<String, Object>> bienTheDealChinh = khuyenMaiTangKemSerivce
							.getKhuyenMaiTangKemChinhOfBienThe(dd.getId()).stream().map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuongGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					List<Map<String, Object>> bienTheDealPhu = khuyenMaiTangKemSerivce
							.getKhuyenMaiTangKemPhuOfBienThe(dd.getId()).stream().map(di -> {
								Map<String, Object> mi = new HashMap<>();
								mi.put("thoiGianChay", di.getThoiGianApDung());
								mi.put("thoiGianNgung", di.getThoiGianNgung());
								mi.put("soLuongGioiHan", di.getSoLuongGioiHan());
								mi.put("soLuongDaDung", di.getSoLuongDaDung());
								return mi;
							}).filter(Objects::nonNull).collect(Collectors.toList());
					dat.put("tangKemChinh", bienTheDealChinh);
					dat.put("tangKemPhu", bienTheDealPhu);
				}
				return dat;
			}).collect(Collectors.toList());

			map.put("bienThe", bienthe);
			map.put("giaMin", giaMin);
			map.put("GiaMax", giaMax);
			map.put("Tongkho", soLuongTrongKho);

			content.add(map);
		}

		return new PageImpl<>(content, pageable, pageSanPham.getTotalElements());
	}

	// tổng quan doanh thu
	public Map<String, Map<String, Object>> getThongKeSanPham(long id, LocalDateTime bd, LocalDateTime kt) {
		List<Integer> danhSachTrangThaiChapNhan = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13,14,15,16,17);
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);
		ChiTietHoaDonService chiTietHoaDonService = ServiceLocator.getBean(ChiTietHoaDonService.class);
		List<TruyCap> truyCap1 = truyCapService.getTruyCapBySanPhamInTime(bd, kt, sanPham);
		List<TruyCap> truyCap2 = truyCapService.getTruyCapBySanPhamInTime(bd.minus(Duration.between(bd, kt)),
				bd.minusDays(1), sanPham);
		float tiLeTruyCap;
		int tongSoLuongTruyCap = truyCap1.size();
		if (truyCap2.size() == 0) {
			if (truyCap1.size() == 0) {
				tiLeTruyCap = 0;
			} else {
				tiLeTruyCap = 100;
			}
		} else {
			tiLeTruyCap = ((float) (truyCap1.size() - truyCap2.size()) / truyCap2.size()) * 100;
		}

		AtomicLong tongNguoiMua1 = new AtomicLong(0);
		AtomicReference<Float> tongDoanhThu1 = new AtomicReference<>(0f);
		AtomicLong tongNguoiMua2 = new AtomicLong(0);
		AtomicReference<Float> tongDoanhThu2 = new AtomicReference<>(0f);
		AtomicReference<Float> doanhThuTrungBinh = new AtomicReference<>(0f);

		// XỬ LÝ PHẦN HÓA ĐƠN HIỆN TẠI TRƯỚC
		List<ChiTietHoaDon> danhSachHoaDon = chiTietHoaDonService.getHoaDonOfSanPham(
				sanPham.getAllBienTheNotCheckActive(true).stream().map(d -> d.getId()).collect(Collectors.toList()), bd,
				kt, danhSachTrangThaiChapNhan);
		List<ChiTietHoaDon> danhSachTaiQuay = danhSachHoaDon.stream()
				.filter(d -> d.getHoaDon() instanceof HoaDonTaiQuay).collect(Collectors.toList());
		List<ChiTietHoaDon> danhSachOnline = danhSachHoaDon.stream().filter(d -> d.getHoaDon() instanceof HoaDonOnline)
				.collect(Collectors.toList());
		/// CÓ CHỈNH SỬA THAY VÌ danhSachOnline.stream()... SỬA LẠI THÀNH DANHSACHHOADON
		/// LẤY HẾT LUÔN
		long tongDonDat = danhSachHoaDon.stream().map(d -> d.getHoaDon().getId()).distinct().count();
		/// CÓ CHỈNH SỬA THAY VÌ danhSachOnline.stream()... SỬA LẠI THÀNH DANHSACHHOADON
		/// LẤY HẾT LUÔN
		long tongDonThanhCong = danhSachHoaDon.stream().filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).map(d -> d.getHoaDon().getId()).distinct().count();
		// lấy tổng doanh thu
		danhSachHoaDon.stream().forEach(d -> {
		    long tongTienChuanHoa = (long) (Math.ceil(d.getTongTien() / 1000.0) * 1000);
		    
		    tongDoanhThu1.updateAndGet(v -> v + tongTienChuanHoa);
		    if ((d.getHoaDon()).getTrangThai().getId() == 1) {
		        tongDoanhThu2.updateAndGet(v -> v + tongTienChuanHoa);
		    }
		});
		List<Long> tong1 = danhSachOnline.stream().map(data -> data.getHoaDon().getKhachHang().getId()).distinct()
				.collect(Collectors.toList());
		List<Long> tong2 = danhSachOnline.stream().filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).map(data -> data.getHoaDon().getKhachHang().getId()).distinct().collect(Collectors.toList());
		List<Long> tong3 = danhSachTaiQuay.stream().filter(d -> d.getHoaDon().getKhachHang() != null)
				.map(data -> data.getHoaDon().getKhachHang().getId()).distinct().collect(Collectors.toList());
		List<Long> tong33 = danhSachTaiQuay.stream().filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).filter(d -> d.getHoaDon().getKhachHang() != null).map(data -> data.getHoaDon().getKhachHang().getId())
				.distinct().collect(Collectors.toList());
		long a = danhSachTaiQuay.stream().filter(d -> d.getHoaDon().getKhachHang() == null).distinct().count();
		long aa = danhSachTaiQuay.stream().filter(d -> d.getHoaDon().getKhachHang() == null).filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;

		}).distinct().count();
		Set<Long> chungQuy1 = new HashSet<Long>();
		Set<Long> chungQuy2 = new HashSet<Long>();

		chungQuy1.addAll(tong2);
		chungQuy1.addAll(tong33);
		chungQuy2.addAll(tong1);
		chungQuy2.addAll(tong3);
		tongNguoiMua1.addAndGet(chungQuy2.stream().distinct().count() + a);
		tongNguoiMua2.addAndGet(chungQuy1.stream().distinct().count() + aa);

		doanhThuTrungBinh.updateAndGet(v -> tongDoanhThu2.get() / Duration.between(bd, kt).toDays());
		AtomicLong tongNguoiMua12 = new AtomicLong(0);
		AtomicReference<Float> tongDoanhThu12 = new AtomicReference<>(0f);
		AtomicLong tongNguoiMua22 = new AtomicLong(0);
		AtomicReference<Float> tongDoanhThu22 = new AtomicReference<>(0f);
		AtomicReference<Float> doanhThuTrungBinh2 = new AtomicReference<>(0f);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime start = bd.minus(Duration.between(bd, kt));
		LocalDateTime end = bd;
		List<ChiTietHoaDon> danhSachHoaDonBefor = chiTietHoaDonService.getHoaDonOfSanPham(
				sanPham.getAllBienTheNotCheckActive(true).stream().map(d -> d.getId()).collect(Collectors.toList()),
				start, end, danhSachTrangThaiChapNhan);

		List<ChiTietHoaDon> danhSachTaiQuay2 = danhSachHoaDonBefor.stream()
				.filter(d -> d.getHoaDon() instanceof HoaDonTaiQuay).collect(Collectors.toList());
		List<ChiTietHoaDon> danhSachOnline2 = danhSachHoaDonBefor.stream()
				.filter(d -> d.getHoaDon() instanceof HoaDonOnline).collect(Collectors.toList());
		danhSachHoaDonBefor.stream().forEach(d -> {
		    // Chuẩn hóa tổng tiền: làm tròn lên đến hàng nghìn
		    long tongTienChuanHoa = (long) (Math.ceil(d.getTongTien() / 1000.0) * 1000);
		    
		    tongDoanhThu12.updateAndGet(v -> v + tongTienChuanHoa);
		    
		    // Kiểm tra trạng thái hóa đơn và cộng vào tổng doanh thu 22 nếu thỏa mãn
		    if (d.getHoaDon().getTrangThai().getId() == 1) {
		        tongDoanhThu22.updateAndGet(v -> v + tongTienChuanHoa);
		    }
		});
		List<Long> tong12 = danhSachOnline2.stream().map(data -> data.getHoaDon().getKhachHang().getId()).distinct()
				.collect(Collectors.toList());
		List<Long> tong22 = danhSachOnline2.stream().filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).map(data -> data.getHoaDon().getKhachHang().getId()).distinct().collect(Collectors.toList());
		List<Long> tong32 = danhSachTaiQuay2.stream().filter(d -> d.getHoaDon().getKhachHang() != null)
				.map(data -> data.getHoaDon().getKhachHang().getId()).distinct().collect(Collectors.toList());
		List<Long> tong332 = danhSachTaiQuay2.stream().filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).filter(d -> d.getHoaDon().getKhachHang() != null).map(data -> data.getHoaDon().getKhachHang().getId())
				.distinct().collect(Collectors.toList());
		long a2 = danhSachTaiQuay2.stream().filter(d -> d.getHoaDon().getKhachHang() == null).distinct().count();
		long aa2 = danhSachTaiQuay2.stream().filter(d -> d.getHoaDon().getKhachHang() == null).filter(d -> {
			return d.getHoaDon().getTrangThai().getId() == 1;
		}).distinct().count();
		List<Long> chungQuy12 = new ArrayList<Long>();
		List<Long> chungQuy22 = new ArrayList<Long>();
		chungQuy12.addAll(tong22);
		chungQuy12.addAll(tong332);
		chungQuy22.addAll(tong12);
		chungQuy22.addAll(tong32);
		tongNguoiMua12.addAndGet(chungQuy22.stream().distinct().count() + a2);
		tongNguoiMua22.addAndGet(chungQuy12.stream().distinct().count() + aa2);

		doanhThuTrungBinh2.updateAndGet(v -> tongDoanhThu22.get() / tongNguoiMua22.get());
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

		// Truy cập
		Map<String, Object> truyCapSoLuong = new HashMap<>();
		truyCapSoLuong.put("soLuong", truyCap1.size());
		truyCapSoLuong.put("soLuongTruocDo", truyCap2.size());
		truyCapSoLuong.put("tiLe", truyCap2.size() == 0 ? (truyCap1.size() > 0 ? 100.0 : 0.0)
				: ((double) (truyCap1.size() - truyCap2.size()) / truyCap2.size()) * 100);

		result.put("truyCap", truyCapSoLuong);

		// Người mua đơn đặt
		Map<String, Object> nguoiMuaDatHangThongKe = new HashMap<>();

		double nguoiMuaHienTai = tongNguoiMua1.get();
		double nguoiMuaTruocDo = tongNguoiMua12.get();
		double tyLeTangTruongNguoiMua;

		if (nguoiMuaTruocDo == 0) {
			tyLeTangTruongNguoiMua = nguoiMuaHienTai == 0 ? 0.0 : nguoiMuaHienTai * 100;
		} else {
			tyLeTangTruongNguoiMua = ((nguoiMuaHienTai - nguoiMuaTruocDo) / nguoiMuaTruocDo) * 100;
		}

		nguoiMuaDatHangThongKe.put("soLuong", nguoiMuaHienTai);
		nguoiMuaDatHangThongKe.put("soLuongTruocDo", nguoiMuaTruocDo);
		nguoiMuaDatHangThongKe.put("tiLe", tyLeTangTruongNguoiMua);

		result.put("nguoiMuaDonDat", nguoiMuaDatHangThongKe);

		// Doanh số đơn đặt
		Map<String, Object> donDatDoanhSoData = new HashMap<>();

		double doanhSoHienTai = tongDoanhThu1.get();
		double doanhSoTruocDo = tongDoanhThu12.get();
		double tiLeTangTruong;

		if (doanhSoTruocDo == 0) {
			tiLeTangTruong = doanhSoHienTai == 0 ? 0.0 : doanhSoHienTai * 100;
		} else {
			tiLeTangTruong = ((doanhSoHienTai - doanhSoTruocDo) / doanhSoTruocDo) * 100;
		}

		// Làm tròn lên nếu bạn vẫn muốn dùng Math.ceil
		donDatDoanhSoData.put("soLuong", doanhSoHienTai);
		donDatDoanhSoData.put("soLuongTruocDo", doanhSoTruocDo);
		donDatDoanhSoData.put("tiLe", Math.ceil(tiLeTangTruong));

		result.put("doanhSoDonDat", donDatDoanhSoData);

		// Người mua thành công
		Map<String, Object> tcNguoiMuaData = new HashMap<>();

		double soLuongHienTai = tongNguoiMua2.get();
		double soLuongTruocDo = tongNguoiMua22.get();
		double tyLeTangTruong;

		if (soLuongTruocDo == 0) {
			tyLeTangTruong = soLuongHienTai == 0 ? 0.0 : soLuongHienTai * 100;
		} else {
			tyLeTangTruong = ((soLuongHienTai - soLuongTruocDo) / soLuongTruocDo) * 100;
		}

		tcNguoiMuaData.put("soLuong", soLuongHienTai);
		tcNguoiMuaData.put("soLuongTruocDo", soLuongTruocDo);
		tcNguoiMuaData.put("tiLe", tyLeTangTruong);

		result.put("nguoiMuaThanhCong", tcNguoiMuaData);

		// Doanh số thành công
		Map<String, Object> dstcSoLuong = new HashMap<>();

		double hienTai = tongDoanhThu2.get();
		double truocDo = tongDoanhThu22.get();
		double tiLeTangTruongDoanhSo;

		if (truocDo == 0) {
			tiLeTangTruongDoanhSo = hienTai == 0 ? 0.0 : hienTai * 100;
		} else {
			tiLeTangTruongDoanhSo = ((hienTai - truocDo) / truocDo) * 100;
		}

		dstcSoLuong.put("soLuong", hienTai);
		dstcSoLuong.put("soLuongTruocDo", truocDo);
		dstcSoLuong.put("tiLe", tiLeTangTruongDoanhSo);

		result.put("doanhSoThanhCong", dstcSoLuong);

		// Doanh số trung bình trên mỗi người
		Map<String, Object> dstmnSoLuong = new HashMap<>();

		float doanhSoTrungBinhHienTai = (tongNguoiMua2.get() == 0) ? 0f
				: tongDoanhThu2.get() / Duration.between(bd, kt).toDays();

		float doanhSoTrungBinhTruocDo = (tongNguoiMua22.get() == 0) ? 0f
				: tongDoanhThu22.get() / Duration.between(start, end).toDays();

		// ✅ Không giới hạn, không null
		float tiLeTangTruongDSTB;
		if (doanhSoTrungBinhTruocDo == 0f) {
			tiLeTangTruongDSTB = doanhSoTrungBinhHienTai == 0f ? 0f : doanhSoTrungBinhHienTai * 100f;
		} else {
			tiLeTangTruongDSTB = ((doanhSoTrungBinhHienTai - doanhSoTrungBinhTruocDo) / doanhSoTrungBinhTruocDo) * 100f;
		}
		long roundedDoanhSoHienTai = (doanhSoTrungBinhHienTai < 1000 && doanhSoTrungBinhHienTai > 0) 
			    ? 1000 
			    : (long) Math.floor(doanhSoTrungBinhHienTai / 1000) * 1000;
			long roundedDoanhSoTruocDo = (doanhSoTrungBinhTruocDo < 1000 && doanhSoTrungBinhTruocDo > 0) 
			    ? 1000 
			    : (long) Math.floor(doanhSoTrungBinhTruocDo / 1000) * 1000;

		dstmnSoLuong.put("doanhSo", roundedDoanhSoHienTai);
		dstmnSoLuong.put("doanhSoTruocDo", roundedDoanhSoTruocDo);
		dstmnSoLuong.put("tiLe", tiLeTangTruongDSTB);

		result.put("doanhSoTrungBinh", dstmnSoLuong);

		Map<String, Object> base = new HashMap<String, Object>();
		Map<String, Object> duLieuSanPham = new HashMap<String, Object>();
		duLieuSanPham.put("ten", sanPham.getTen());
		duLieuSanPham.put("hinhAnh", sanPham.getAnhBia());
		duLieuSanPham.put("bienThe", sanPham.getAllBienTheNotCheckActive(true).stream().map(d -> {
			Map<String, Object> bt = new HashMap<String, Object>();
			bt.put("ten", d.getTen());
			bt.put("anhBia", d.getAnhBia());
			bt.put("id", d.getId());
			return bt;
		}));

		base.put("tongDonDat", danhSachHoaDon.stream().map(d -> d.getHoaDon().getId()).distinct().count());
		base.put("tongThanhCong", danhSachOnline.stream().filter(d -> d.getHoaDon().getTrangThai().getId() == 1)
				.map(d -> d.getHoaDon().getId()).distinct().count());
		result.put("base", base);
		result.put("thongTinCoBan", duLieuSanPham);
		// tiếp đến phần mức lợi nhuận qua các thời gian

		return result;

	}

	// Lưu ý phần chi tiết doanh thu
	public Map<String, Object> getChiTietDoanhThu(long id, LocalDateTime bd, LocalDateTime kt, int kieuBan,
			int PhanLoaiChon) {
		List<Integer> danhSachTrangThaiChapNhan = Arrays.asList(1);
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);

		Map<Integer, Map<String, Object>> tongQuanThongKe = new HashMap<Integer, Map<String, Object>>();
		List<BienThe> bienThe = sanPham.getAllBienTheNotCheckActive(true);
		bienThe.forEach(data -> {
			Map<String, Object> map = new HashMap<>();
			map.put("tongDoanhThu", 0.f);
			map.put("soLuongBanRa", 0);
			map.put("tongDonBan", 0);
			map.put("id", data.getId());
			map.put("ten", data.getTen());
			tongQuanThongKe.put(data.getId(), map);
		});
		List<ChiTietHoaDon> chiTietHoaDonDuPhong = chiTietHoaDonService.getHoaDonOfSanPham(
				bienThe.stream().map(d -> d.getId()).collect(Collectors.toList()), bd, kt, danhSachTrangThaiChapNhan);
		List<Integer> iddd = new ArrayList<Integer>();
		if (PhanLoaiChon != 0) {
			iddd.add(PhanLoaiChon);
		} else {
			iddd = bienThe.stream().map(d -> d.getId()).collect(Collectors.toList());
		}
		List<ChiTietHoaDon> chiTietHoaDon = chiTietHoaDonService.getHoaDonOfSanPham(iddd, bd, kt,
				danhSachTrangThaiChapNhan);

		if (kieuBan == 1) {
			chiTietHoaDon = chiTietHoaDon.stream().filter(d -> d.getHoaDon() instanceof HoaDonTaiQuay)
					.collect(Collectors.toList());
		}
		if (kieuBan == 2) {
			chiTietHoaDon = chiTietHoaDon.stream().filter(d -> d.getHoaDon() instanceof HoaDonOnline)
					.collect(Collectors.toList());
		}
		AtomicInteger tongSoLuongBanRa = new AtomicInteger(0);
		AtomicReference<Float> tonDoanhSoBanRa = new AtomicReference<Float>(0f);
		AtomicInteger tongDonBan = new AtomicInteger(0);
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("data", chiTietHoaDon.stream().map(d -> {

			Map<String, Object> map = new HashMap<String, Object>();

			List<ParentCartLast> parentCartLasts = hoaDonOnlineService.mapToHoaDonConver(d.getHoaDon().getId());

			map.put("donGiaBanRa",
					parentCartLasts.stream().filter(k -> k.getIdBienThe() == d.getBienThe().getId()).map(m -> {
						m.getCartItemLasts();
						return m;
					}));

			map.put("thoiDiemMua", d.getHoaDon().getNgayLap());
			map.put("tongSoLuong", d.getSoLuong());
			map.put("donGiaBan", d.getDonGia());
			map.put("tongTien", d.getTongTien());

			tonDoanhSoBanRa.updateAndGet(tong -> tong + d.getTongTien());
			tongDonBan.addAndGet(1);
			tongSoLuongBanRa.addAndGet(d.getSoLuong());
			return map;
		}).collect(Collectors.toList()));

		chiTietHoaDonDuPhong.stream().filter(d -> {
			if (kieuBan == 1) {
				return d.getHoaDon() instanceof HoaDonTaiQuay;
			}
			if (kieuBan == 2) {
				return d.getHoaDon() instanceof HoaDonOnline;
			}
			return true;
		}).forEach(d -> {
			Map<String, Object> thongKeBienThe = tongQuanThongKe.get(d.getBienThe().getId());
			if (thongKeBienThe != null) {
				Float doanhThuHienTai = (Float) thongKeBienThe.get("tongDoanhThu");
				thongKeBienThe.put("tongDoanhThu", doanhThuHienTai + d.getTongTien());

				Integer soLuongHienTai = (Integer) thongKeBienThe.get("soLuongBanRa");
				thongKeBienThe.put("soLuongBanRa", soLuongHienTai + d.getSoLuong());

				Integer tongDonHienTai = (Integer) thongKeBienThe.get("tongDonBan");
				thongKeBienThe.put("tongDonBan", tongDonHienTai + 1);
			}
		});
		result.put("dulieuPhanLoai", tongQuanThongKe.values().stream().collect(Collectors.toList()));
		result.put("tongDoanhThu", tonDoanhSoBanRa.get());
		result.put("soLuongBanRa", tongSoLuongBanRa);
		result.put("tongDonBan", tongDonBan);
		return result;
	}

	public Map<String, Object> getLuuLuongTruyCap(long id, LocalDateTime bd, LocalDateTime kt, int timeduration,
			int kieuBan) {
		Map<String, Object> result = new HashMap<String, Object>();
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);
		List<TruyCap> truyCap1 = truyCapService.getTruyCapBySanPhamInTime(bd, kt, sanPham);
		AtomicInteger tonLuongTruyCapMoi = new AtomicInteger(0);
		AtomicInteger tongLuongTruyCapCu = new AtomicInteger(0);
		AtomicInteger chuyenDoiThanhCong = new AtomicInteger(0);
		AtomicInteger chuyeDoiThatBai = new AtomicInteger(0);

		List<Map<String, Object>> dataTruyCap = new ArrayList<Map<String, Object>>();
		Map<Long, Map<String, Object>> nguoiDungTruyCap = new HashMap<Long, Map<String, Object>>();
		truyCap1.stream().forEach(d -> {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ngayGioTruyCap", d.getNgayGioiTruyCap());
			dataTruyCap.add(map);
			// kiểm tra lưu thông tin người dùng truy cập vào trong đây
			Map<String, Object> nguoiDung = nguoiDungTruyCap.get(d.getKhachHang().getId());
			if (nguoiDung == null) {
				nguoiDung = new HashMap<String, Object>();
				nguoiDung.put("id", d.getKhachHang().getId());
				nguoiDung.put("ten", d.getKhachHang().getTen());
				nguoiDung.put("tongLanChuyenDoiThanhCong", 0);
				nguoiDungTruyCap.put(d.getKhachHang().getId(), nguoiDung);
			}

			if (truyCapService.getTruyCaKhacCuaTruyCap(d.getId(), id, d.getKhachHang().getId(), d.getNgayGioiTruyCap())
					.size() == 0) {
				tonLuongTruyCapMoi.addAndGet(1);
			} else {
				tongLuongTruyCapCu.addAndGet(1);
			}
			List<ChiTietHoaDon> ds = chiTietHoaDonService.getHoaDonOfSanPhamInTime(d.getKhachHang().getId(),
					d.getNgayGioiTruyCap(), d.getNgayGioiTruyCap().plusMinutes(timeduration), id);
			if (kieuBan == 1) {
				ds = ds.stream().filter(dd -> dd.getHoaDon() instanceof HoaDonTaiQuay).collect(Collectors.toList());
			}
			if (kieuBan == 2) {
				ds = ds.stream().filter(dd -> dd.getHoaDon() instanceof HoaDonOnline).collect(Collectors.toList());
			}
			if (ds.size() != 0) {
				int sl1 = (int) nguoiDung.get("tongLanChuyenDoiThanhCong");
				nguoiDung.put("tongLanChuyenDoiThanhCong", sl1 + 1);
				chuyenDoiThanhCong.addAndGet(1);
			} else {
				chuyeDoiThatBai.addAndGet(1);
			}
		});
		result.put("tonLuongTruyCapMoi", tonLuongTruyCapMoi);
		result.put("tongLuongTruyCapCu", tongLuongTruyCapCu);
		result.put("chuyenDoiThanhCong", chuyenDoiThanhCong);
		result.put("chuyeDoiThatBai", chuyeDoiThatBai);
		result.put("dataTruyCap", dataTruyCap);
		result.put("nguoiDungTruyCap", nguoiDungTruyCap.values().stream().collect(Collectors.toList()));
		return result;
	}

	public Map<String, Object> getSanPhamForUpdate(long id) {
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);
		SanPham sanPham2 = new SanPham();
		Map<String, Object> result = new HashMap<String, Object>();
		sanPham2.setThuongHieu((int) sanPham.getThuongHieu().getId());
		sanPham2.setThue(sanPham.getThuevat());
		sanPham2.setThanhPhan(sanPham.getThanhPhan());
		sanPham2.setTen(sanPham.getTen());
		sanPham2.setMoTa(sanPham.getMoTa());
		sanPham2.setGia(sanPham.getGiaMacDinh());
		sanPham2.setCachDung(sanPham.getCachDung());
		sanPham2.setThongSo(sanPham.getThongSoCuThe());
		sanPham2.setDanhMuc(sanPham.getDanhMuc().getId());
		List<com.example.e_commerce.DTO.request.BienThe> bienThes = sanPham.getOnlyNotDefault(0).stream().map(d -> {
			com.example.e_commerce.DTO.request.BienThe bienThe = new com.example.e_commerce.DTO.request.BienThe();
			bienThe.setGia(d.getGia());
			bienThe.setTen(d.getTen());
			bienThe.setDuongDanAnh(d.getAnhBia());
			bienThe.setConsuDung(d.isConSuDung());
			bienThe.setSoLuong(d.getSoLuongKho());
			bienThe.setOld(true);
			bienThe.setId(d.getId());
			bienThe.setMaVach(d.getMaVach());
			bienThe.setKhoiLuong(d.getKhoiLuong());
			bienThe.setDongGoiNhap(
					d.getBienTheDongGoiChans().stream().filter(p -> p.getDongGoiChan().getId() != 4).map(k -> {
						ItemQuyCachDongGoi itemQuyCachDongGoi = new ItemQuyCachDongGoi();
						itemQuyCachDongGoi.setId(k.getId().getDGC_ID());
						itemQuyCachDongGoi.setMaVach(k.getMaVach());
						itemQuyCachDongGoi.setSoLuong(k.getId().getSOLUONG());
						itemQuyCachDongGoi.setTenQuyCach(k.getDongGoiChan().getTenQuyCach());
						itemQuyCachDongGoi.setDuongDan(k.getDongGoiChan().getDuongDan());
						return itemQuyCachDongGoi;
					}).collect(Collectors.toList()));
			return bienThe;
		}).collect(Collectors.toList());
		sanPham2.setBienTheKhongLe(bienThes);
		if(sanPham.getbienTheDefault()!=null) {
			BienThe t = sanPham.getbienTheDefault();
			sanPham2.setMaVach(t.getMaVach());
			sanPham2.setKhoiLuong(t.getKhoiLuong());
			sanPham2.setDongGoiNhap(
					t.getBienTheDongGoiChans().stream().filter(p -> p.getDongGoiChan().getId() != 4).map(k -> {
						ItemQuyCachDongGoi itemQuyCachDongGoi = new ItemQuyCachDongGoi();
						itemQuyCachDongGoi.setId(k.getId().getDGC_ID());
						itemQuyCachDongGoi.setMaVach(k.getMaVach());
						itemQuyCachDongGoi.setSoLuong(k.getId().getSOLUONG());
						itemQuyCachDongGoi.setTenQuyCach(k.getDongGoiChan().getTenQuyCach());
						itemQuyCachDongGoi.setDuongDan(k.getDongGoiChan().getDuongDan());
						return itemQuyCachDongGoi;
					}).collect(Collectors.toList()));
			sanPham2.setKhoiLuong(t.getKhoiLuong());
		}
		
		List<DanhMuc> danhMucs = new ArrayList<DanhMuc>();
		danhMucSerVice.getDanhMucCha(sanPham.getDanhMuc(), danhMucs);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", sanPham2);
		Collections.reverse(danhMucs);
		data.put("danhMuc", danhMucs);
		data.put("anhGioiThieu", sanPham.getAnhGioiThieus());
		data.put("anhBia", sanPham.getAnhBia());
		return data;

	}

	public Page<SanPhamPhieuNhap> getSanPhamCungCap(LocalDateTime nbd, LocalDateTime nkt, String dvcc, String tensp,
			int trang, boolean het) {
		BienTheSerVice bienTheSerVice = ServiceLocator.getBean(BienTheSerVice.class);
		int sol=10;
		if(het) {
			sol=100000;
		}
		Page<Object[]> danhSac = sanPhamRepository.getBienTheDonViCungCap(nbd, nkt, dvcc, tensp,
				PageRequest.of(trang, sol));
		List<Object[]> danhSach = danhSac.getContent();
		Map<Long, SanPhamPhieuNhap> sanPhamPhieuNhap = new HashMap<>();
		com.example.e_commerce.model.SanPham s;
		BienThe b;
		SanPhamPhieuNhap sp;

		for (int i = 0; i < danhSach.size(); i++) {
			b = (BienThe) danhSach.get(i)[0];
			s = b.getSanPham();
			if (sanPhamPhieuNhap.containsKey(s.getId())) {
				sp = sanPhamPhieuNhap.get(s.getId());
			} else {
				sp = new SanPhamPhieuNhap();
				sp.setAnhSanPham(s.getAnhBia());
				sp.setIdSanPham(s.getId());
				sp.setTenSanPham(s.getTen());
				sanPhamPhieuNhap.put(s.getId(), sp);
			}
			sp.addBienThe(b.getId(),((b.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY"))?"Mặc định":b.getTen()), b.getAnhBia());
			sp.addLanNhap(b.getId(), (Integer) danhSach.get(i)[2], (Float) danhSach.get(i)[3],
					(LocalDateTime) danhSach.get(i)[1], (Long) danhSach.get(i)[4], (String) danhSach.get(i)[5]);
		}

		return new PageImpl<>(sanPhamPhieuNhap.values().stream().toList(), PageRequest.of(trang, 10),
				sanPhamPhieuNhap.size());

	}

	public Page<Map<String, Object>> getXuatTonKho(String tenSanPham, int idDanhMuc, int trang, LocalDateTime bd,
			LocalDateTime kt,boolean het,String status) {
		ArrayList<Integer> ds = new ArrayList<>();
		for (String s : status.trim().split("\\s+")) {
		    ds.add(Integer.parseInt(s));
		}
		int sol=10;
		if(het) {
			sol=100000;
		}
		Page<com.example.e_commerce.model.SanPham> sanPhamPage = sanPhamRepository.getAllSanPhamNotCondition(idDanhMuc,
				tenSanPham, PageRequest.of(trang, sol));

		List<Map<String, Object>> content = sanPhamPage.getContent().stream().map(d -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", d.getId());
			map.put("ten", d.getTen());
			map.put("anhBia", d.getAnhBia());

			List<Map<String, Object>> map3 = d.getAllBienTheNotCheckActive(true).stream().map(f -> {
				Map<String, Object> map2 = new HashMap<>();
				Integer soLuongNhapKyTruoc = chiTietPhieuNhapService.GetTongSoLuongNhap(f.getId(),
						LocalDateTime.of(2000, 1, 1, 0, 0), bd);
				if (soLuongNhapKyTruoc == null)
					soLuongNhapKyTruoc = 0;

				Long soLuongXuatKyTruoc = chiTietHoaDonService.getTongSoLuotBan(f.getId(),
						LocalDateTime.of(2000, 1, 1, 0, 0), bd, ds);
				if (soLuongXuatKyTruoc == null)
					soLuongXuatKyTruoc = 0L;
				Integer soLuongNhapTrongKy = chiTietPhieuNhapService.GetTongSoLuongNhap(f.getId(), bd, kt);
				if (soLuongNhapTrongKy == null)
					soLuongNhapTrongKy = 0;

				Long soLuongXuatTrongKy = chiTietHoaDonService.getTongSoLuotBan(f.getId(), bd, kt, ds);
				if (soLuongXuatTrongKy == null)
					soLuongXuatTrongKy = 0L;

				Integer tonKyTruoc = soLuongNhapKyTruoc - soLuongXuatKyTruoc.intValue();
				List<SanPhamKiem> spk = sanPhamKiemService.getSanPhamKiemOfBienThe(bd, kt, f.getId());
				int soLuongHuHao = soLuongHuHao = spk.stream().mapToInt(p -> p.getSoLuong()).sum();
				Integer tonKyNay = tonKyTruoc + soLuongNhapTrongKy - soLuongXuatTrongKy.intValue() - soLuongHuHao;

				map2.put("soLuongHuHao", soLuongHuHao);
				map2.put("tonKyTruoc", tonKyTruoc);
				map2.put("soLuongNhapKyNay", soLuongNhapTrongKy);
				map2.put("soLuongXuatTrongKy", soLuongXuatTrongKy);
				map2.put("tonKynay", tonKyNay);
				map2.put("id", f.getTen());

				return map2;
			}).collect(Collectors.toList());

			map.put("xuatTonKho", map3);
			return map;
		}).collect(Collectors.toList());

		return new PageImpl<>(content, sanPhamPage.getPageable(), sanPhamPage.getTotalElements());
	}

	public void TinhGiaCuaMoSanPhamTronKhoangThoiGian(int idBienThe, LocalDateTime bd, LocalDateTime kt) {
		// get Chi Tiet Phieu Nhap Gan nhat cua bien the do
		ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService
				.findLatestPhieuNhapByBienTheIdAndNgayNhap(idBienThe, bd).orElse(null);

		if (chiTietPhieuNhap != null) {
			Queue<Map<String, Object>> queue = new LinkedList<Map<String, Object>>();

		}
	}

//	public void deQuyPhieuNhap(Queue<itemPrice> itemPrice, int idBienThe, LocalDateTime bd, List<Integer> i) {
//		ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService
//				.findLatestPhieuNhapByBienTheIdAndNgayNhap(idBienThe, bd).orElse(null);
//		if (chiTietPhieuNhap == null) {
//			return;
//		}
//		ChiTietPhieuNhap reverse = chiTietPhieuNhapService
//				.findLatestPhieuNhapByBienTheIdAndNgayNhap(idBienThe, chiTietPhieuNhap.getPhieuNhap().getNgayNhapHang())
//				.orElse(null);
//		Long soLuongBanRa = chiTietHoaDonService.getTongSoLuotBan(idBienThe,
//				chiTietPhieuNhap.getPhieuNhap().getNgayNhapHang(), reverse.getPhieuNhap().getNgayNhapHang(), i);
//		int soLuongTonKho = chiTietPhieuNhapService.GetTongSoLuongNhap(idBienThe,
//				chiTietPhieuNhap.getPhieuNhap().getNgayNhapHang(), reverse.getPhieuNhap().getNgayNhapHang());
//
//		if (soLuongBanRa < soLuongTonKho) {
//			deQuyPhieuNhap(itemPrice, idBienThe, chiTietPhieuNhap.getPhieuNhap().getNgayNhapHang().minusSeconds(15), i);
//		}
//
//		long soLuongDonVao = 0;
//		if (soLuongBanRa > soLuongTonKho) {
//			soLuongDonVao = reverse.getSoLuong() + soLuongTonKho - soLuongBanRa;
//			itemPrice ii = new itemPrice();
//			ii.setGia(reverse.getDonGia());
//			ii.setSoLuong(soLuongDonVao);
//			itemPrice.add(ii);
//		} else {
//			int soLuongDaTru = (int) (soLuongBanRa - itemPrice.peek().getSoLuong());
//			if (soLuongDaTru < 0) {
//				itemPrice.remove();
//			}
//			while (soLuongDaTru < 0 && itemPrice.size() > 0) {
//				itemPrice ii = itemPrice.peek();
//				long soLuongHienTai = ii.getSoLuong();
//				soLuongDaTru += soLuongHienTai;
//				itemPrice.remove();
//				if (soLuongDaTru < 0 && itemPrice.size() > 0) {
//					ii = itemPrice.peek();
//					soLuongDaTru += ii.getSoLuong();
//					if (soLuongDaTru <= 0) {
//						itemPrice.remove();
//					} else {
//						ii.setSoLuong(soLuongDaTru);
//						soLuongDaTru = 0;
//					}
//				}
//			}
//			if (soLuongDaTru > 0 && itemPrice.size() > 0) {
//				itemPrice ii = itemPrice.peek();
//				ii.setSoLuong(soLuongDaTru);
//			}
//		}
//	}

	public List<Map<String, Object>> getNSanPhamBanCuoiFIFO(List<ChiTietPhieuNhap> danhSachNhap, long soLuongBan,
			boolean tinhChoHoaDon, List<ChiTietHoaDon> chiTietHoaDons, List<SanPhamKiem> sanPhamKiems) {
		List<Map<String, Object>> ketQuaDaoNguoc = new ArrayList<>();

		if (danhSachNhap == null || danhSachNhap.isEmpty()) {
			return ketQuaDaoNguoc;
		}
		danhSachNhap.sort(Comparator.comparing(ct -> ct.getPhieuNhap().getNgayNhapHang()));

		List<ChiTietPhieuNhap> danhSachNhapConLai = new ArrayList<>();
		for (ChiTietPhieuNhap nhap : danhSachNhap) {
			ChiTietPhieuNhap nhapCopy = new ChiTietPhieuNhap();
			nhapCopy.setSoLuong(nhap.getSoLuong());
			nhapCopy.setDonGia(nhap.getDonGia());
			nhapCopy.setPhieuNhap(nhap.getPhieuNhap());
			danhSachNhapConLai.add(nhapCopy);
		}

		long soLuongConLaiBan = soLuongBan;
//		System.out.println("SỐ LƯỢ");
		for (ChiTietPhieuNhap lanNhap : danhSachNhapConLai) {
			if (lanNhap == null || lanNhap.getPhieuNhap() == null || soLuongConLaiBan == 0) {
				continue;
			}
			long soLuongNhap = lanNhap.getSoLuong();
			long soLuongLay = Math.min(soLuongConLaiBan, soLuongNhap);
			long result = soLuongNhap - soLuongLay;
			if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
			    throw new IllegalArgumentException("Giá trị vượt quá phạm vi của int");
			}
			lanNhap.setSoLuong((int) result);

			soLuongConLaiBan -= soLuongLay;
		}

		// Kiểm tra nếu không đủ hàng cho soLuongBan
		if (soLuongConLaiBan > 0) {
			throw new IllegalStateException("Không đủ hàng tồn kho để phân bổ cho số lượng đã bán/hao: " + soLuongBan);
		}

		// Gộp danh sách chiTietHoaDons và sanPhamKiems thành một danh sách sự kiện
		List<Map<String, Object>> suKien = new ArrayList<>();
		if (chiTietHoaDons != null) {
			for (ChiTietHoaDon cthd : chiTietHoaDons) {
				if (cthd != null && cthd.getHoaDon() != null) {
					Map<String, Object> suKienBan = new HashMap<>();
					suKienBan.put("type", "ban");
					suKienBan.put("soLuong", cthd.getSoLuong());
					suKienBan.put("ngay", cthd.getHoaDon().getNgayLap());
					suKien.add(suKienBan);
				}
			}
		}

		if (sanPhamKiems != null) {
			for (SanPhamKiem spk : sanPhamKiems) {
				if (spk != null && spk.getPhieuKiemHang() != null) {
					Map<String, Object> suKienHao = new HashMap<>();
					suKienHao.put("type", "hao");
					suKienHao.put("soLuong", spk.getSoLuong());
					suKienHao.put("ngay", spk.getPhieuKiemHang().getNgayYeuCau());
					suKien.add(suKienHao);
				}
			}
		}

		// Sắp xếp danh sách sự kiện theo ngày (LocalDateTime)
		suKien.sort(Comparator.comparing(s -> (LocalDateTime) s.get("ngay")));

		// Phân bổ số lượng từ danhSachNhapConLai theo FIFO
		for (Map<String, Object> suKienMap : suKien) {
			String type = (String) suKienMap.get("type");
//			long soLuongConLai = (Long) suKienMap.get("soLuong");
			long soLuongConLai = ((Number) suKienMap.get("soLuong")).longValue();


			for (ChiTietPhieuNhap lanNhap : danhSachNhapConLai) {
				if (lanNhap == null || lanNhap.getPhieuNhap() == null || soLuongConLai == 0) {
					continue;
				}

				long soLuongNhap = lanNhap.getSoLuong();
				float gia = lanNhap.getDonGia();
				long soLuongLay = Math.min(soLuongConLai, soLuongNhap);
				soLuongConLai -= soLuongLay;
				long result = soLuongNhap - soLuongLay;
				if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
				    throw new IllegalArgumentException("Giá trị vượt quá phạm vi của int");
				}
				lanNhap.setSoLuong((int) result);


				// Chỉ thêm vào kết quả nếu khớp với điều kiện tinhChoHoaDon
				if ((tinhChoHoaDon && type.equals("ban")) || (!tinhChoHoaDon && type.equals("hao"))) {
					Map<String, Object> item = new HashMap<>();
					item.put("soLuong", soLuongLay);
					item.put("gia", gia);
					item.put("idPhieuNhap", lanNhap.getPhieuNhap().getId());
					item.put("ngayNhap", lanNhap.getPhieuNhap().getNgayNhapHang());
					ketQuaDaoNguoc.add(item);
				}
			}

			// Kiểm tra nếu không đủ hàng tồn kho
			if (soLuongConLai > 0) {
				throw new IllegalStateException("Không đủ hàng tồn kho để phân bổ cho sự kiện " + type);
			}
		}

		// Gộp các phần tử có cùng giá
		Map<Float, Map<String, Object>> gopTheoGia = new LinkedHashMap<>();
		for (Map<String, Object> item : ketQuaDaoNguoc) {
			float gia = (Float) item.get("gia");
			long soLuong = (Long) item.get("soLuong");

			if (gopTheoGia.containsKey(gia)) {
				Map<String, Object> existing = gopTheoGia.get(gia);
				long soLuongHienTai = (Long) existing.get("soLuong");
				existing.put("soLuong", soLuongHienTai + soLuong);
				existing.put("idPhieuNhap", item.get("idPhieuNhap"));
				existing.put("ngayNhap", item.get("ngayNhap"));
			} else {
				Map<String, Object> newItem = new HashMap<>();
				newItem.put("soLuong", soLuong);
				newItem.put("gia", gia);
				newItem.put("idPhieuNhap", item.get("idPhieuNhap"));
				newItem.put("ngayNhap", item.get("ngayNhap"));
				gopTheoGia.put(gia, newItem);
			}
		}

		// Chuyển kết quả gộp về danh sách và đảo ngược để giữ thứ tự FIFO
		ketQuaDaoNguoc = new ArrayList<>(gopTheoGia.values());
//		Collections.reverse(ketQuaDaoNguoc);
		return ketQuaDaoNguoc;
	}

	public Page<Map<String, Object>> getXuatBanAndLoiNhuanBase(LocalDateTime bd, LocalDateTime kt, int trang, String ten, boolean het
			,String status) {
		ArrayList<Integer> trangThaiHoaDon = new ArrayList<>();
		for (String s : status.trim().split("\\s+")) {
			trangThaiHoaDon.add(Integer.parseInt(s));
		}
	    int soLuon=10;
	    if(het==true) {
	    	soLuon=100000;
	    }
	    // Truy vấn và phân trang sản phẩm
	    Page<com.example.e_commerce.model.SanPham> sanPhams = sanPhamRepository.getAllSanPhamNotCondition(0, ten,
	            PageRequest.of(trang, soLuon));

	    // Sử dụng Page.map để giữ nguyên phân trang
	    return sanPhams.map(sanPham -> {
	        Map<String, Object> result = new HashMap<>();
	        result.put("id", sanPham.getId());
	        result.put("ten", sanPham.getTen());

	        // Xử lý danh sách biến thể cho từng sản phẩm
	        List<Map<String, Object>> bienTheList = sanPham.getAllBienTheNotCheckActive(true).stream().map(bienThe -> {
	            // Tính số lượng bán và hao hụt trong khoảng thời gian
	            int tongSoLuongBanTrongKhoang = 0;
	            float tongGiaTriBan = 0f;
	            List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonService.getChiTietHoaDonOfBienThe(bienThe.getId(), bd, kt,
	                    trangThaiHoaDon);
	            for (ChiTietHoaDon ct : dsChiTiet) {
	                tongSoLuongBanTrongKhoang += ct.getSoLuong();
	                tongGiaTriBan += ct.getTongTien();
	            }

	            // Tính số lượng hao hụt trong khoảng thời gian
	            List<SanPhamKiem> danhSachHaoHut = sanPhamKiemService.getSanPhamKiemOfBienThe(bd, kt, bienThe.getId());
	            long soLuongHaoHut = danhSachHaoHut.stream().mapToLong(d -> d.getSoLuong()).sum();

	            // Tổng số lượng cần phân bổ (bán + hao hụt)
	            long tongSoLuongPhanBo = tongSoLuongBanTrongKhoang + soLuongHaoHut;

	            // Lấy số lượng bán/hao hụt từ trước khoảng thời gian
	            Long soLuongBanTuTruocObj = chiTietHoaDonService.getTongSoLuotBan(bienThe.getId(),
	                    LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), trangThaiHoaDon);
	            List<SanPhamKiem> danhSachHaoHutTruoc = sanPhamKiemService.getSanPhamKiemOfBienThe(
	                    LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), bienThe.getId());
	            long soLuongHaoHutTruoc = danhSachHaoHutTruoc.stream().mapToLong(d -> d.getSoLuong()).sum();
	            long soLuongBanTuTruoc = (soLuongBanTuTruocObj != null ? soLuongBanTuTruocObj : 0L) + soLuongHaoHutTruoc;

	            // Lấy danh sách phiếu nhập đến thời điểm kt
	            List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapService.getAllChiTietPhieuNhapBienThe(bienThe.getId(), kt);

	            // Tính giá vốn cho bán (tinhChoHoaDon = true)
	            List<Map<String, Object>> danhSachGiaVonBan = getNSanPhamBanCuoiFIFO(dsNhap, soLuongBanTuTruoc,
	                    true, dsChiTiet, danhSachHaoHut);
	            float tongGiaVonBan = 0f;
	            for (Map<String, Object> m : danhSachGiaVonBan) {
	                long soLuong = (Long) m.get("soLuong");
	                float gia = (Float) m.get("gia");
	                tongGiaVonBan += soLuong * gia;
	            }

	            // Tính giá vốn cho hao hụt (tinhChoHoaDon = false)
	            List<Map<String, Object>> danhSachGiaVonHao = getNSanPhamBanCuoiFIFO(dsNhap, soLuongBanTuTruoc,
	                    false, dsChiTiet, danhSachHaoHut);
	            float tongGiaVonHao = 0f;
	            for (Map<String, Object> m : danhSachGiaVonHao) {
	                long soLuong = (Long) m.get("soLuong");
	                float gia = (Float) m.get("gia");
	                tongGiaVonHao += soLuong * gia;
	            }

	            // Kết quả cho biến thể
	            Map<String, Object> ketQuaBienThe = new HashMap<>();
	            ketQuaBienThe.put("ten", bienThe.getTen());
	            ketQuaBienThe.put("soLuongBan", tongSoLuongBanTrongKhoang);
	            ketQuaBienThe.put("soLuongHaoHut", soLuongHaoHut);
	            ketQuaBienThe.put("doanhThu", tongGiaTriBan);
	            ketQuaBienThe.put("vonBan", tongGiaVonBan);
	            ketQuaBienThe.put("vonHaoHut", tongGiaVonHao);
	            ketQuaBienThe.put("loiNhuan", tongGiaTriBan - tongGiaVonBan); // Lợi nhuận chỉ tính từ bán
	            return ketQuaBienThe;
	        }).collect(Collectors.toList());

	        result.put("bienThe", bienTheList);
	        return result;
	    });
	}

// không filter những phiếu nhập , nếu filter thì danh sách chi tiết phải lớn hơn 0
//	public bienTheLoiNhuan getGiaOfChiTietHoaDon(int idbienThe, LocalDateTime bd, LocalDateTime kt,
//			List<EChiTietHoaDon> cc, List<Integer> trangThaiHoaDon) {
//		BienThe bienThe = bienTheSerVice.getById(idbienThe);
//		int tongSoLuongBanTrongKhoang = 0;
//        float tongGiaTriBan = 0f;
//        List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonService.getChiTietHoaDonOfBienThe(bienThe.getId(), bd, kt,
//                trangThaiHoaDon);
//        for (ChiTietHoaDon ct : dsChiTiet) {
//            tongSoLuongBanTrongKhoang += ct.getSoLuong();
//            tongGiaTriBan += ct.getTongTien();
//        }
//
//        // Tính số lượng hao hụt trong khoảng thời gian
//        List<SanPhamKiem> danhSachHaoHut = sanPhamKiemService.getSanPhamKiemOfBienThe(bd, kt, bienThe.getId());
//        long soLuongHaoHut = danhSachHaoHut.stream().mapToLong(d -> d.getSoLuong()).sum();
//
//        // Tổng số lượng cần phân bổ (bán + hao hụt)
//        long tongSoLuongPhanBo = tongSoLuongBanTrongKhoang + soLuongHaoHut;
//
//        // Lấy số lượng bán/hao hụt từ trước khoảng thời gian
//        Long soLuongBanTuTruocObj = chiTietHoaDonService.getTongSoLuotBan(bienThe.getId(),
//                LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), trangThaiHoaDon);
//        List<SanPhamKiem> danhSachHaoHutTruoc = sanPhamKiemService.getSanPhamKiemOfBienThe(
//                LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), bienThe.getId());
//        long soLuongHaoHutTruoc = danhSachHaoHutTruoc.stream().mapToLong(d -> d.getSoLuong()).sum();
//        long soLuongBanTuTruoc = (soLuongBanTuTruocObj != null ? soLuongBanTuTruocObj : 0L) + soLuongHaoHutTruoc;
//
//        // Lấy danh sách phiếu nhập đến thời điểm kt
//        List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapService.getAllChiTietPhieuNhapBienThe(bienThe.getId(), kt);
//
//        // Tính giá vốn cho bán (tinhChoHoaDon = true)
//        List<Map<String, Object>> danhSachGiaVonBan = getNSanPhamBanCuoiFIFO(dsNhap, soLuongBanTuTruoc,
//                true, dsChiTiet, danhSachHaoHut);
//
//		bienTheLoiNhuan b = new bienTheLoiNhuan();
//		b.setId(idbienThe);
//		b.setTen(bienThe.getTen());
//		
//		
//		
//
//		List<lanBan> danhSachLanBan = dsChiTiet.stream().map(d -> {
//
//			lanBan l = new lanBan();
//			List<ParentCartLast> parentCartLasts = hoaDonOnlineService.mapToHoaDonConver(d.getHoaDon().getId());
//			List<CartItemLast> h = parentCartLasts.stream().filter(k -> k.getIdBienThe() == d.getBienThe().getId())
//					.flatMap(k -> k.getCartItemLasts().stream()).collect(Collectors.toList());
//
//			l.setCartItemLasts(h);
//			l.setNgayBan(d.getHoaDon().getNgayLap());
//			l.setE(d.getId());
//			l.setDanhSachPhanNho(new ArrayList<>());
//			
//			//
//			
////			System.out.println("TÍNH ĐƠN GIÁ BÁN CHI TIẾT ");
////			l.setDonGiaBan2(hoaDonOnlineService.tinhDonGiaBan(d.getHoaDon().getId(), idbienThe));
//			long soLuongConLai = d.getSoLuong();
//			int i = 0;
//
//			while (soLuongConLai > 0 && i < danhSachGiaVonBan.size()) {
//				long soLuongTrongKho = (Long) danhSachGiaVonBan.get(i).get("soLuong");
//				float giaVon = (Float) danhSachGiaVonBan.get(i).get("gia");
//
//				if (soLuongTrongKho > 0) {
//					long soLuongSuDung = Math.min(soLuongConLai, soLuongTrongKho);
//					itemPrice ii = new itemPrice();
//					ii.setSoLuong(soLuongSuDung);
//					ii.setGia(giaVon);
//					l.getDanhSachPhanNho().add(ii);
//					soLuongConLai -= soLuongSuDung;
//					danhSachGiaVonBan.get(i).put("soLuong", soLuongTrongKho - soLuongSuDung);
//				}
//				i++;
//			}
//			if (!l.getDanhSachPhanNho().isEmpty()) {
//				float tongGiaVon = (float) l.getDanhSachPhanNho().stream()
//						.mapToDouble(ii -> ii.getSoLuong() * ii.getGia()).sum();
//				l.setTonGiaBan(d.getTongTien());
//
//				l.setDonGiaBan(d.getDonGia());
//			} else {
//				l.setTonGiaBan(0f);
//			}
//			if (cc == null || cc.size() == 0) {
//				return l;
//			}
//
//			else {
//
//				if (cc.contains(d.getId())) {
//					return l;
//				} else {
//					return null;
//				}
//			}
//
//		}).filter(Objects::nonNull).collect(Collectors.toList());
//		b.setLanBan(danhSachLanBan);
//		return b;
//	}
	
	public bienTheLoiNhuan getGiaOfChiTietHoaDon(int idbienThe, LocalDateTime bd, LocalDateTime kt,
	        List<EChiTietHoaDon> cc, List<Integer> trangThaiHoaDon) {
	    BienThe bienThe = bienTheSerVice.getById(idbienThe);
	    int tongSoLuongBanTrongKhoang = 0;
	    float tongGiaTriBan = 0f;
	    List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonService.getChiTietHoaDonOfBienThe(bienThe.getId(), bd, kt,
	            trangThaiHoaDon);
	    for (ChiTietHoaDon ct : dsChiTiet) {
	        tongSoLuongBanTrongKhoang += ct.getSoLuong();
	        tongGiaTriBan += ct.getTongTien();
	    }

	    // Tính số lượng hao hụt trong khoảng thời gian
	    List<SanPhamKiem> danhSachHaoHut = sanPhamKiemService.getSanPhamKiemOfBienThe(bd, kt, bienThe.getId());
	    long soLuongHaoHut = danhSachHaoHut.stream().mapToLong(d -> d.getSoLuong()).sum();

	    // Tổng số lượng cần phân bổ (bán + hao hụt)
	    long tongSoLuongPhanBo = tongSoLuongBanTrongKhoang + soLuongHaoHut;

	    // Lấy số lượng bán/hao hụt từ trước khoảng thời gian
	    Long soLuongBanTuTruocObj = chiTietHoaDonService.getTongSoLuotBan(bienThe.getId(),
	            LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), trangThaiHoaDon);
	    List<SanPhamKiem> danhSachHaoHutTruoc = sanPhamKiemService.getSanPhamKiemOfBienThe(
	            LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), bienThe.getId());
	    long soLuongHaoHutTruoc = danhSachHaoHutTruoc.stream().mapToLong(d -> d.getSoLuong()).sum();
	    long soLuongBanTuTruoc = (soLuongBanTuTruocObj != null ? soLuongBanTuTruocObj : 0L) + soLuongHaoHutTruoc;

	    // Lấy danh sách phiếu nhập đến thời điểm kt
	    List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapService.getAllChiTietPhieuNhapBienThe(bienThe.getId(), kt);

	    // Tính giá vốn cho bán (tinhChoHoaDon = true)
	    List<Map<String, Object>> danhSachGiaVonBan = getNSanPhamBanCuoiFIFO(dsNhap, soLuongBanTuTruoc,
	            true, dsChiTiet, danhSachHaoHut);

	    bienTheLoiNhuan b = new bienTheLoiNhuan();
	    b.setId(idbienThe);
	    b.setTen(bienThe.getTen());

	    List<lanBan> danhSachLanBan = dsChiTiet.stream().map(d -> {
	        lanBan l = new lanBan();
	        List<ParentCartLast> parentCartLasts = hoaDonOnlineService.mapToHoaDonConver(d.getHoaDon().getId());
	        List<CartItemLast> h = parentCartLasts.stream().filter(k -> k.getIdBienThe() == d.getBienThe().getId())
	                .flatMap(k -> k.getCartItemLasts().stream()).collect(Collectors.toList());

	        l.setCartItemLasts(h);
	        l.setNgayBan(d.getHoaDon().getNgayLap());
	        l.setE(d.getId());
	        l.setDanhSachPhanNho(new ArrayList<>());

	        long soLuongConLai = d.getSoLuong();
	        int i = 0;

	        while (soLuongConLai > 0 && i < danhSachGiaVonBan.size()) {
	            long soLuongTrongKho = (Long) danhSachGiaVonBan.get(i).get("soLuong");
	            float giaVon = (Float) danhSachGiaVonBan.get(i).get("gia");

	            if (soLuongTrongKho > 0) {
	                long soLuongSuDung = Math.min(soLuongConLai, soLuongTrongKho);
	                itemPrice ii = new itemPrice();
	                ii.setSoLuong(soLuongSuDung);
	                ii.setGia(giaVon);
	                l.getDanhSachPhanNho().add(ii);
	                soLuongConLai -= soLuongSuDung;
	                danhSachGiaVonBan.get(i).put("soLuong", soLuongTrongKho - soLuongSuDung);
	            }
	            i++;
	        }
	        if (!l.getDanhSachPhanNho().isEmpty()) {
	            float tongGiaVon = (float) l.getDanhSachPhanNho().stream()
	                    .mapToDouble(ii -> ii.getSoLuong() * ii.getGia()).sum();
	            l.setTonGiaBan(d.getTongTien());
	            l.setDonGiaBan(d.getDonGia());
	        } else {
	            l.setTonGiaBan(0f);
	        }

	        // Sửa logic lọc cc
	        if (cc == null || cc.isEmpty()) {
	            return null; // Không trả về lanBan nếu cc rỗng
	        } else {
	            if (cc.contains(d.getId())) {
	                return l;
	            } else {
	                return null;
	            }
	        }
	    }).filter(Objects::nonNull).collect(Collectors.toList());
	    b.setLanBan(danhSachLanBan);
	    return b;
	}

	public Map<String, Object> getBienTheForThongKeFlashSale(int idbienThe, LocalDateTime bd, LocalDateTime kt,
			List<EChiTietHoaDon> c) {
		List<Integer> a= Arrays.asList(1,2,3,6,12);
		bienTheLoiNhuan bienTheLoiNhuan = getGiaOfChiTietHoaDon(idbienThe, bd, kt, c,a);
		Map<String, Object> r = new HashMap<String, Object>();
		AtomicReference<Float> TongDoanhSo = new AtomicReference<Float>(0f);
		BienThe bt = bienTheSerVice.getById(idbienThe);
		AtomicReference<Float> tongGiaGoc = new AtomicReference<Float>(0f);
		AtomicLong tongSoLuongBan = new AtomicLong();
		AtomicInteger tongSoLuotBan = new AtomicInteger();
		bienTheLoiNhuan.getLanBan().forEach(d -> {
			System.out.println("Duyệt qua :"+d.getDonGiaBan());
			System.out.println("tỏng số tiền: "+d.getTonGiaBan());
			long soLuong = d.getDanhSachPhanNho().stream().mapToLong(item -> item.getSoLuong()).sum();
			tongSoLuongBan.addAndGet(soLuong);
			tongSoLuotBan.incrementAndGet();

			float doanhSoLanNay = d.getDonGiaBan() * soLuong;
			TongDoanhSo.updateAndGet(v -> v + d.getTonGiaBan());

			float giaGocLanNay = (float) d.getDanhSachPhanNho().stream().mapToDouble(p -> p.getGia() * p.getSoLuong())
					.sum();
			tongGiaGoc.updateAndGet(v -> v + giaGocLanNay);

//			float loiNhuanLanNay = doanhSoLanNay - giaGocLanNay;
//			TongDoanhSo.updateAndGet(v -> v + loiNhuanLanNay);
		});
		r.put("tongSoLuongBan", tongSoLuongBan.get());
		r.put("tongSoLuotBan", tongSoLuotBan.get());
		r.put("tongGiaGoc", tongGiaGoc.get());
		r.put("tongDoanhSo", TongDoanhSo.get());
		r.put("tenBienThe", bt.getTen());
		r.put("idSanPham", bt.getSanPham().getId());
		r.put("anhBienThe", bt.getAnhBia());
		return r;
	}

	public Map<String, Object> getLoiNhuanOfSanPham(long idSanPham, LocalDateTime bd, LocalDateTime kt,String status) {
		ArrayList<Integer> ds = new ArrayList<>();
		for (String s : status.trim().split("\\s+")) {
		    ds.add(Integer.parseInt(s));
		}
		Map<String, Object> result = new HashMap<String, Object>();
		com.example.e_commerce.model.SanPham s = getSanPhamById(idSanPham);
		result.put("tenSanPham", s.getTen());
		result.put("anhBia", s.getAnhBia());
		result.put("thonTinDoanhThu", s.getAllBienTheNotCheckActive(true).stream().map(d -> {
			return getGiaOfChiTietHoaDonV2(d.getId(), bd, kt, null,ds);
		}).collect(Collectors.toList()));
		return result;
	}

	// khách hàng
	public Map<String, Object> getViewProduct(FilterViewProduct filterViewProduct, int trang) {
		DanhMucSerVice danhMucSerVice = ServiceLocator.getBean(DanhMucSerVice.class);
		List<DanhMuc> danhMucs = new ArrayList<DanhMuc>();
		if (filterViewProduct.getCategory() != 0) {
			DanhMuc dd = danhMucSerVice.getDanhMucById(filterViewProduct.getCategory());
			danhMucSerVice.getAllChildDanhMuc(danhMucs, dd);
		} else {
			danhMucs = this.danhMucSerVice.getAll();
		}
		List<Integer> iddDanhMuc = danhMucs.stream().map(f -> f.getId()).distinct().collect(Collectors.toList());
		Page<com.example.e_commerce.model.SanPham> sanPham;
		if (filterViewProduct.getOrderBy().equals("thapDenCao")) {
			sanPham = sanPhamRepository.getForViewProductASC(filterViewProduct.getDanhSachThongSo(),
					filterViewProduct.getDanhSachThongSo().size(), filterViewProduct.getThuongHieuId(),
					filterViewProduct.getThuongHieuId().size(), filterViewProduct.getGiaBatDau(),
					filterViewProduct.getGiaKetThuc(), iddDanhMuc, filterViewProduct.getTenSanPham(),
					PageRequest.of(trang, 12));
		} else {
			sanPham = sanPhamRepository.getForViewProductDESC(filterViewProduct.getDanhSachThongSo(),
					filterViewProduct.getDanhSachThongSo().size(), filterViewProduct.getThuongHieuId(),
					filterViewProduct.getThuongHieuId().size(), filterViewProduct.getGiaBatDau(),
					filterViewProduct.getGiaKetThuc(), iddDanhMuc, filterViewProduct.getTenSanPham(),
					PageRequest.of(trang, 12));
		}
		List<com.example.e_commerce.model.SanPham> sanPhams = sanPham.getContent();

		List<Map<String, Object>> danhSachSanPhamView = sanPhams.stream().map(d -> {
			Map<String, Object> sanPhamItem = new HashMap<>();
			List<Integer> idBienThe = d.getBienTheOfConditionCheckActive(false).stream().map(df -> df.getId())
					.collect(Collectors.toList());
			List<BienTheFlashSale> bienTheFlashSales = bienTheFlashSaleService
					.getBienTheFlahSaleDangApDungOfSanPham(idBienThe);
			List<BienTheDealChinh> bienTheDealChinhs = bienTheDealChinhService.getBienTheDealChinhOfSanPham(idBienThe);
			List<BienTheDealPhu> bienTheDealPhus = bienTheDealPhuService.getBienTheDealChinhOfSanPham(idBienThe);
			sanPhamItem.put("id", d.getId());
			sanPhamItem.put("ten", d.getTen());
			sanPhamItem.put("tenThuonHieu", d.getThuongHieu().getTen());
			sanPhamItem.put("idThuongHieu", d.getThuongHieu().getId());
			sanPhamItem.put("anhGioiThieu", d.getAnhBia());
			if (bienTheDealChinhs.size() > 0 || bienTheDealPhus.size() > 0) {
				sanPhamItem.put("haveDeal", true);
			} else {
				sanPhamItem.put("haveDeal", false);
			}
			AtomicReference<Float> phanTramMax = new AtomicReference<>(0f);
			AtomicReference<Float> giaMin = new AtomicReference<>(Float.MAX_VALUE); // ban đầu để max để so sánh min
			AtomicReference<Float> giaMax = new AtomicReference<>(0f);

			d.getBienTheOfConditionCheckActive(false).stream().forEach(dh -> {
				float tongGiaTriGiam1 = 0f;
				for (var dg1 : bienTheFlashSales) {
					if (dh.getId() == dg1.getBienThe().getId() && tongGiaTriGiam1 < dg1.getGiaTriGiam()) {
						tongGiaTriGiam1 = dg1.getGiaTriGiam();
					}
				}

				float tongGiaTriGiam2 = 0f;
				for (var dg1 : bienTheDealPhus) {
					if (dh.getId() == dg1.getBienThe().getId() && tongGiaTriGiam2 < dg1.getGiaTriGiam()) {
						tongGiaTriGiam2 = dg1.getGiaTriGiam();
					}
				}

				float tongGiam = tongGiaTriGiam1 + tongGiaTriGiam2;

				if (tongGiam > phanTramMax.get()) {
					phanTramMax.set(tongGiam);
				}

				float giaSauGiam = dh.getGia() * (1 - tongGiam / 100.0f);

				if (giaSauGiam > giaMax.get()) {
					giaMax.set(giaSauGiam);
				}

				if (giaSauGiam < giaMin.get()) {
					giaMin.set(giaSauGiam);
				}
			});

			List<Object[]> list = phanHoiService.getGeneralPhanHoi(d.getId());
			Object[] phanHoi = list.isEmpty() ? new Object[] { 0, 0 } : list.get(0);

			sanPhamItem.put("soSao", phanHoi[0] != null ? phanHoi[0] : 0);
			sanPhamItem.put("soLuotDanhGia", phanHoi[1] != null ? phanHoi[1] : 0);

			sanPhamItem.put("giaMax", giaMax.get());
			sanPhamItem.put("phanTramGiam", phanTramMax.get());
			sanPhamItem.put("giaMin", giaMin.get());

			return sanPhamItem;
		}).collect(Collectors.toList());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("data", danhSachSanPhamView);
		result.put("totalElement", sanPham.getTotalElements());
		result.put("totalPage", sanPham.getTotalPages());

		return result;
	}

	public Map<String, Object> getSanPhamLienQuan(long id) {
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);
		FilterViewProduct f = new FilterViewProduct();
		f.setCategory(sanPham.getDanhMuc().getId());
		f.setGiaBatDau(0f);
		f.setGiaKetThuc(200000000f);
		return getViewProduct(f, 0);
	}

	public Map<String, Object> getDetailProduct(long id) {
		Map<String, Object> re = new HashMap<String, Object>();
		com.example.e_commerce.model.SanPham sanPham = getSanPhamById(id);
		re.put("id", sanPham.getId());
		re.put("ten", sanPham.getTen());
		re.put("thueVAT", sanPham.getThuevat());
		re.put("idthuongHieu", sanPham.getThuongHieu().getId());
		re.put("anhGioiThieuThuongHieu", sanPham.getThuongHieu().getAnhDaiDien());
		re.put("tenThuongHieu", sanPham.getThuongHieu().getTen());
		re.put("anhGioiThieu", sanPham.getAnhGioiThieus());
		re.put("anhBia", sanPham.getAnhBia());
		re.put("moTa", sanPham.getMoTa());
		re.put("thanhPhan", sanPham.getThanhPhan());
		re.put("cachDung", sanPham.getCachDung());
		List<Integer> idBienThe = sanPham.getBienTheOfConditionCheckActive(false).stream().map(df -> df.getId())
				.collect(Collectors.toList());
		List<BienTheFlashSale> bienTheFlashSales = bienTheFlashSaleService
				.getBienTheFlahSaleDangApDungOfSanPham(idBienThe);
		List<BienTheDealChinh> bienTheDealChinhs = bienTheDealChinhService.getBienTheDealChinhOfSanPham(idBienThe);
		List<BienTheDealPhu> bienTheDealPhus = bienTheDealPhuService.getBienTheDealChinhOfSanPham(idBienThe);
		re.put("thonTinBienThe",
				sanPham.getBienTheOfConditionNotCheckActive(true).stream().filter(d -> {
					int sl= bienTheSerVice.soLuongHuHao(d.getId());
					return (d.getSoLuongKho()-sl) > 0;
				}).map(g -> {
					Map<String, Object> bienThe = new HashMap<String, Object>();
					bienThe.put("id", g.getId());
					bienThe.put("ten", g.getTen());
					bienThe.put("anhBia", g.getAnhBia());
					
					bienThe.put("soLuong", g.getSoLuongKho()-soLuongHaoHienGio(g));
					bienThe.put("giaThiTruong", g.getGia());
					List<BienTheFlashSale> bb = bienTheFlashSales.stream()
							.filter(gg -> gg.getBienThe().getId() == g.getId()).collect(Collectors.toList());

					Map<String, Object> f = new HashMap<String, Object>();
					if (bb.size() != 0) {
						f.put("soLuongConLai", bb.get(0).getSoLuongDaDung());
						f.put("giaTriGiam", bb.get(0).getGiaTriGiam());
						f.put("thoiGianBatDau", bb.get(0).getFlashSale().getGioBatDau());
						f.put("thoiGianKetThuc", bb.get(0).getFlashSale().getGioKetThuc());
						bienThe.put("giaGiam", g.getGia() * (1 - bb.get(0).getGiaTriGiam() / 100f));
					} else {
						f.put("giaTriGiam", 0);
						bienThe.put("giaGiam", g.getGia());
					}
					bienThe.put("thonTinFlashsale", f);
					bienThe.put("giaGiamKem",
							bienTheDealChinhs.stream().filter(h -> h.getBienThe().getId() == g.getId())
									.flatMap(m -> m.getDeal().getBienTheDealPhu().stream()).map(j -> {
										Map<String, Object> giamGiaKem = new HashMap<String, Object>();
										giamGiaKem.put("id", j.getBienThe().getSanPham().getId());
										giamGiaKem.put("ten", j.getBienThe().getSanPham().getTen());
										giamGiaKem.put("anhDaiDien", j.getBienThe().getSanPham().getAnhBia());
										giamGiaKem.put("tiLeGiam", j.getGiaTriGiam());
										giamGiaKem.put("soLuongDuocGiam", j.getToiDaTrenDonVi());
										return giamGiaKem;
									}).collect(Collectors.toList()));
					bienThe.put("muaTrenGiamDuoi",
							bienTheDealPhus.stream().filter(h -> h.getBienThe().getId() == g.getId())
									.flatMap(m -> m.getDeal().getBienTheDealChinh().stream())
									.filter(jh -> jh.getSoLuongTu() <= jh.getBienThe().getSoLuongKho()).map(j -> {
										Map<String, Object> giamGiaKem = new HashMap<String, Object>();
										giamGiaKem.put("id", j.getBienThe().getSanPham().getId());
										giamGiaKem.put("ten", j.getBienThe().getSanPham().getTen());
										giamGiaKem.put("anhDaiDien", j.getBienThe().getSanPham().getAnhBia());
										giamGiaKem.put("soLuongMuaDuocGiam", j.getSoLuongTu());
										return giamGiaKem;
									}).collect(Collectors.toList()));

					return bienThe;
				}).collect(Collectors.toList()));
		AtomicInteger tongSoSao = new AtomicInteger(0);
		AtomicInteger tongSo5Sao = new AtomicInteger(0);
		AtomicInteger tongSo4Sao = new AtomicInteger(0);
		AtomicInteger tongSo3Sao = new AtomicInteger(0);
		AtomicInteger tongSo2Sao = new AtomicInteger(0);
		AtomicInteger tongSo1Sao = new AtomicInteger(0);
		List<PhanHoi> phanHois = sanPham.getPhanHois();
		AtomicBoolean daPhanHoi = new AtomicBoolean(false);
		phanHois.forEach(d -> {
			if (d.getKhachHang().getId().equals((long) jwtService.getIdUser())) {
				daPhanHoi.set(true);
			} else {
			}
			tongSoSao.addAndGet(d.getSoSao());
			if (d.getSoSao() == 5) {
				tongSo5Sao.addAndGet(1);
			}
			if (d.getSoSao() == 4) {
				tongSo4Sao.addAndGet(1);
			}
			if (d.getSoSao() == 3) {
				tongSo3Sao.addAndGet(1);
			}
			if (d.getSoSao() == 2) {
				tongSo2Sao.addAndGet(1);
			}
			if (d.getSoSao() == 1) {
				tongSo1Sao.addAndGet(1);
			}
		});
		re.put("thongTinPhanHoi", phanHois.stream().map(d -> {

			Map<String, Object> phanHoi = new HashMap<String, Object>();
			phanHoi.put("soSao", d.getSoSao());
			phanHoi.put("tenKhachHang", d.getKhachHang().getTen());
			phanHoi.put("ngayGioiPhanHoi", d.getThoiGianPhanHoi());
			phanHoi.put("noiDungPhanHoi", d.getNoiDungPhanHoi());

			return phanHoi;
		}));
		int sl = phanHois.size() != 0 ? phanHois.size() : 1;
		re.put("soSaoTrungBinh", (float) tongSoSao.get() / sl);
		re.put("so5Sao", tongSo5Sao.get());
		re.put("so4Sao", tongSo4Sao.get());
		re.put("so3Sao", tongSo3Sao.get());
		re.put("so2Sao", tongSo2Sao.get());
		re.put("so1Sao", tongSo1Sao.get());
		List<Object[]> list = phanHoiService.getGeneralPhanHoi(sanPham.getId());
		Object[] phanHoi = list.isEmpty() ? new Object[] { 0, 0 } : list.get(0);
		re.put("sanPhamLienQuan", getSanPhamLienQuan(id));
		re.put("soSao", phanHoi[0] != null ? phanHoi[0] : 0);
		re.put("soLuotDanhGia", phanHoi[1] != null ? phanHoi[1] : 0);
		if (chiTietHoaDonService.kiemTraKhachHangDaMuaThanhConChua(sanPham, jwtService.getIdUser()) == true
				&& daPhanHoi.get() == false) {
			re.put("duocPhanHoi", true);
		}

		else {
			re.put("duocPhanHoi", false);
		}
		if (jwtService.getIdUser() == 0) {
			re.put("duocPhanHoi", false);
		}
		return re;

	}

	public List<Map<String, Object>> getByMaVachAndTen(String ten, boolean l) {
		List<BienThe> b = sanPhamRepository.getBienTheByMaVach(ten);
		if (l == true) {
			b = b.stream().filter(d -> d.getSoLuongKho() > 0).collect(Collectors.toList());
		}
		return b.stream().map(d -> {
			Map<String, Object> r = new HashMap<String, Object>();
			r.put("id", d.getId());
			r.put("maVach", d.getMaVach());
			r.put("ten", d.getSanPham().getTen() + "-" + d.getTen());
			r.put("soLuong", 0);
			r.put("donGia", 0);
			r.put("tenSanPham", d.getSanPham().getTen());
			return r;
		}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getByMaVachAndTenForNhapHang(String ten, boolean l) {
		List<BienTheDongGoiChan> b = sanPhamRepository.getBienTheByMaVachForPhieuNhap(ten);
		System.out.println("số lượng");
		if (l == true) {
			b = b.stream().filter(d -> d.getBienThe().getSoLuongKho() > 0).collect(Collectors.toList());
		}
		return b.stream().map(d -> {
			
			Map<String, Object> r = new HashMap<String, Object>();
			// thử lấy giá nhập lần trước coi.
			QuyCachDongGoiLanNhap q= sanPhamRepository.findLastestNhapHang(d.getDongGoiChan().getId(),
					d.getBienThe().getId(), d.getId().getSOLUONG());
			
			r.put("id", d.getBienThe().getId());
			r.put("maVach", d.getMaVach());
			r.put("ten",d.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định":d.getBienThe().getTen());
			r.put("soLuong", 0);
			r.put("donGia", 0);
			if(q!=null) {
				r.put("donGia",q.getDonGia());
			}
			r.put("tenSanPham", d.getBienThe().getSanPham().getTen());
			r.put("quyCachDongGoi", d.getDongGoiChan().getTenQuyCach());
			r.put("soLuongTronQuyCach", d.getId().getSOLUONG());
			r.put("duongDanIcon", d.getDongGoiChan().getDuongDan());
			if (d.getDongGoiChan().getId() == 4) {
				r.put("macDinh", true);
			} else {
				r.put("macDinh", false);
			}
			return r;
		}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getByMaVachAndTenForCreateHoaDon(String ten, boolean l) {
		List<BienThe> b = sanPhamRepository.getBienTheByMaVach(ten);
		if (l == true) {
			b = b.stream().filter(d -> d.getSoLuongKho() > 0).collect(Collectors.toList());
		}
		List<Long> a = new ArrayList<Long>();
		a.add((long) 3);
		a.add((long) 2);
		return b.stream().map(d -> {
			Map<String, Object> r = new HashMap<String, Object>();
			List<ChiTietHoaDon> chiTietHoaDons = bienTheSerVice.get(d.getId(), a);
			long tongSoLuong = chiTietHoaDons.stream().mapToLong(g -> g.getSoLuong()).sum();
			r.put("id", d.getId());
			r.put("maVach", d.getMaVach());
			r.put("ten", d.getSanPham().getTen() + " - " +( d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc đinh":d.getTen()));
			r.put("soLuong", 0);
			r.put("donGia", 0);
			r.put("tenSanPham", d.getSanPham().getTen() + " - " +( d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc đinh":d.getTen()) );
			if ((tongSoLuong + d.getSoLuongKho()) > 0) {
				return r;
			} else {
				return null;
			}

		}).filter((h) -> Objects.nonNull(h)).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getSoLuongGioiHanMua(List<Integer> id) {
		List<Long> a = new ArrayList<Long>();
		NguoiDung nguoiDung = nguoiDungService.getById(jwtService.getIdUser());
		a.add((long) 3);
		List<Map<String, Object>> danhSach = id.stream().map(idBienThe -> {
			BienThe b = bienTheSerVice.getById(idBienThe);
			List<ChiTietHoaDon> chiTietHoaDons = bienTheSerVice.get(idBienThe, a);
			long tongSoLuong = chiTietHoaDons.stream().mapToLong(g -> g.getSoLuong()).sum();
			Map<String, Object> r = new HashMap<String, Object>();
			r.put("idBienThe", b.getId());
			if (nguoiDung instanceof KhachHang) {
				r.put("soLuongGioiHan", b.getSoLuongKho());
			} else {
				r.put("soLuongDaDat", tongSoLuong);
				r.put("tongSoLuong", tongSoLuong + b.getSoLuongKho());
				List<Map<String, Object>> danhSachLoaiBo = chiTietHoaDons.stream().map(m -> {
					Map<String, Object> con = new HashMap<String, Object>();
					con.put("idHoaDon", m.getId().getHdId());
					con.put("idBienThe", m.getId().getBtId());
					con.put("tongSoTien", m.getHoaDon().getTongTien());
					con.put("tongSoLuongTrongDon", m.getSoLuong());
					con.put("ngayLap", m.getHoaDon().getNgayLap());
					con.put("tyLeGiaTri", m.getTongTien() / m.getHoaDon().getTongTien());
					return con;
				}).collect(Collectors.toList());
				r.put("danhSachCoTheXoa", danhSachLoaiBo);
			}
			return r;
		}).collect(Collectors.toList());
		return danhSach;
	}

	public List<Map<String, Object>> getByMaVachAndTenForPhieuKiem(String ten, boolean l) {
		List<BienThe> b = sanPhamRepository.getBienTheByMaVachForPhieuKiem(ten);
		if (l == true) {
			b = b.stream().filter(d -> d.getSoLuongKho() > 0).collect(Collectors.toList());
		}
		List<Long> a = new ArrayList<Long>();
		a.add((long) 3);
		a.add((long) 2);
		return b.stream().map(d -> {
			Map<String, Object> r = new HashMap<String, Object>();
			List<ChiTietHoaDon> chiTietHoaDons = bienTheSerVice.get(d.getId(), a);
			long tongSoLuong = chiTietHoaDons.stream().mapToLong(g -> g.getSoLuong()).sum();
			r.put("id", d.getId());
			r.put("sanPham_Id", d.getSanPham().getId());
			r.put("maVach", d.getMaVach());
			r.put("ten", d.getSanPham().getTen() + "-" + (d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc đinh" :d.getTen()));
			r.put("soLuong", 0);
			r.put("donGia", 0);
			r.put("soLuongGioiHan", d.getSoLuongKho() + tongSoLuong);
			r.put("soLuongDaDat", tongSoLuong);
			r.put("tenSanPham", d.getSanPham().getTen());
			if ((tongSoLuong + d.getSoLuongKho()) > 0) {
				return r;
			} else {
				return null;
			}
		}).filter((h) -> Objects.nonNull(h)).collect(Collectors.toList());
	}
	public List<PhieuKiemDTO> getByMaVachAndTenForPhieuKiemV2(String ten, boolean l,long id) {
	    List<BienThe> b = sanPhamRepository.getBienTheByMaVachForPhieuKiem(ten);
	    if (l == true) {
	        b = b.stream().filter(d -> d.getSoLuongKho() > 0).collect(Collectors.toList());
	    }
	    PhieuKiemRepository phieuKiemRepository= ServiceLocator.getBean(PhieuKiemRepository.class);
	    List<Integer> dsId=phieuKiemRepository.getBienTheDangTrongChoKhauHao(id);
	    List<Long> a = new ArrayList<Long>();
	    a.add((long) 3);
	    a.add((long) 2);
	    a.add((long) 13);
	    return b.stream().filter(u->!dsId.contains(u.getId())).map(d -> {
	        // lấy danh sách phiếu hao hụt của nó cái đã 
	        List<SanPhamKiem> sp = sanPhamRepository.getSanPhamKiem(d.getId());
	        
	        PhieuKiemDTO phieuKiemDTO = new PhieuKiemDTO();
	        if (sp != null && sp.size() != 0) {
	            phieuKiemDTO.setPhieuKiemDaCo(sp.get(0).getPhieuKiemHang().getId());
	        }
	        phieuKiemDTO.setGhiChu("");
	        phieuKiemDTO.setIdSanPham(d.getId());
	        phieuKiemDTO.setSoLuong(0);
	        phieuKiemDTO.setSoLuongThucTe(d.getSoLuongKho());
	        phieuKiemDTO.setSanPham(d.getSanPham().getTen());
	        phieuKiemDTO.setTenBienThe(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY") ? "Mặc định" : d.getTen());
	        phieuKiemDTO.setSanPham_Cha(d.getSanPham().getId());
	        phieuKiemDTO.setMaVach(d.getMaVach());
	        phieuKiemDTO.setDanhSachHoaDonBiHuy(new ArrayList<Long>());
	        List<ChiTietHoaDon> c = bienTheSerVice.get(d.getId(), a);
	        
	        int tonSoLuongDaDat = c.stream().mapToInt(m -> m.getSoLuong()).sum();
	        phieuKiemDTO.setSoLuongDaDat(tonSoLuongDaDat);
	        List<Map<String, Object>> danhSachLoaiBo = c.stream().map(m -> {
	            Map<String, Object> con = new HashMap<String, Object>();
	            con.put("idHoaDon", m.getId().getHdId());
	            con.put("idBienThe", m.getId().getBtId());
	            // Round up tongSoTien to the nearest 1000
	            float tongSoTien = m.getHoaDon().getTongTien();
	            long roundedTongSoTien = (long) (Math.ceil(tongSoTien / 1000.0) * 1000);
	            con.put("tongSoTien", roundedTongSoTien);
	            con.put("tongSoLuongTrongDon", m.getSoLuong());
	            con.put("ngayLap", m.getHoaDon().getNgayLap());
	            con.put("idTrangThai", m.getHoaDon().getTrangThai().getId());
	            con.put("tenTrangThai", m.getHoaDon().getTrangThai().getTen());
	            return con;
	        }).collect(Collectors.toList());
	        phieuKiemDTO.setResult(danhSachLoaiBo);
	        return phieuKiemDTO;
	    }).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getByMaVachAndTenCorect(String ten) {
		List<BienThe> b = sanPhamRepository.getBienTheByMaVachCorrect(ten);

		return b.stream().map(d -> {
			Map<String, Object> r = new HashMap<String, Object>();
			r.put("id", d.getId());
			r.put("maVach", d.getMaVach());
			r.put("ten", d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"mặc định": d.getTen());
			r.put("soLuong", 0);
			r.put("donGia", 0);
			r.put("tenSanPham", d.getSanPham().getTen());
			return r;
		}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getByMaVachAndTenForNhapHangCorect(String ten) {
		List<BienTheDongGoiChan> b = sanPhamRepository.getByMaVachForPhieuNhapCorrect(ten);

		return b.stream().map(d -> {
			Map<String, Object> r = new HashMap<String, Object>();
			r.put("id", d.getBienThe().getId());
			r.put("maVach", d.getMaVach());
			r.put("ten", d.getBienThe().getSanPham().getTen() + "-" + ((d.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY"))?"Mặc định":d.getBienThe().getTen()));
			r.put("soLuong", 0);
			r.put("donGia", 0);
			r.put("tenSanPham", d.getBienThe().getSanPham().getTen());
			r.put("quyCachDongGoi", d.getDongGoiChan().getTenQuyCach());
			r.put("soLuongTronQuyCach", d.getId().getSOLUONG());
			r.put("duongDanIcon", d.getDongGoiChan().getDuongDan());
			if (d.getDongGoiChan().getId() == 4) {
				r.put("macDinh", true);
			} else {
				r.put("macDinh", false);
			}
			return r;
		}).collect(Collectors.toList());
	}

	@Transactional
	public void Status(long idSanPham, int status) {
		com.example.e_commerce.model.SanPham s = getSanPhamById(idSanPham);
		if (status == 0) {
			s.setConDung(false);

		} else {
			s.setConDung(true);
		}
		sanPhamRepository.save(s);
	}

	
	public bienTheLoiNhuan getGiaOfChiTietHoaDonV2(int idbienThe, LocalDateTime bd, LocalDateTime kt,
	        List<EChiTietHoaDon> cc, List<Integer> trangThaiHoaDon) {
	    BienThe bienThe = bienTheSerVice.getById(idbienThe);
	    int tongSoLuongBanTrongKhoang = 0;
	    float tongGiaTriBan = 0f;
	    List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonService.getChiTietHoaDonOfBienThe(bienThe.getId(), bd, kt,
	            trangThaiHoaDon);
	    for (ChiTietHoaDon ct : dsChiTiet) {
	        tongSoLuongBanTrongKhoang += ct.getSoLuong();
	        tongGiaTriBan += ct.getTongTien();
	    }

	    // Tính số lượng hao hụt trong khoảng thời gian
	    List<SanPhamKiem> danhSachHaoHut = sanPhamKiemService.getSanPhamKiemOfBienThe(bd, kt, bienThe.getId());
	    long soLuongHaoHut = danhSachHaoHut.stream().mapToLong(d -> d.getSoLuong()).sum();

	    // Tổng số lượng cần phân bổ (bán + hao hụt)
	    long tongSoLuongPhanBo = tongSoLuongBanTrongKhoang + soLuongHaoHut;

	    // Lấy số lượng bán/hao hụt từ trước khoảng thời gian
	    Long soLuongBanTuTruocObj = chiTietHoaDonService.getTongSoLuotBan(bienThe.getId(),
	            LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), trangThaiHoaDon);
	    List<SanPhamKiem> danhSachHaoHutTruoc = sanPhamKiemService.getSanPhamKiemOfBienThe(
	            LocalDateTime.of(2000, 1, 1, 0, 0), bd.minusSeconds(1), bienThe.getId());
	    long soLuongHaoHutTruoc = danhSachHaoHutTruoc.stream().mapToLong(d -> d.getSoLuong()).sum();
	    long soLuongBanTuTruoc = (soLuongBanTuTruocObj != null ? soLuongBanTuTruocObj : 0L) + soLuongHaoHutTruoc;

	    // Lấy danh sách phiếu nhập đến thời điểm kt
	    List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapService.getAllChiTietPhieuNhapBienThe(bienThe.getId(), kt);

	    // Tính giá vốn cho bán (tinhChoHoaDon = true)
	    List<Map<String, Object>> danhSachGiaVonBan = getNSanPhamBanCuoiFIFO(dsNhap, soLuongBanTuTruoc,
	            true, dsChiTiet, danhSachHaoHut);

	    bienTheLoiNhuan b = new bienTheLoiNhuan();
	    b.setId(idbienThe);
	    b.setTen(bienThe.getTen());

	    List<lanBan> danhSachLanBan = dsChiTiet.stream().map(d -> {
	        lanBan l = new lanBan();
	        List<ParentCartLast> parentCartLasts = hoaDonOnlineService.mapToHoaDonConver(d.getHoaDon().getId());
	        List<CartItemLast> h = parentCartLasts.stream().filter(k -> k.getIdBienThe() == d.getBienThe().getId())
	                .flatMap(k -> k.getCartItemLasts().stream()).collect(Collectors.toList());

	        l.setCartItemLasts(h);
	        l.setNgayBan(d.getHoaDon().getNgayLap());
	        l.setE(d.getId());
	        l.setDanhSachPhanNho(new ArrayList<>());

	        long soLuongConLai = d.getSoLuong();
	        int i = 0;

	        while (soLuongConLai > 0 && i < danhSachGiaVonBan.size()) {
	            long soLuongTrongKho = (Long) danhSachGiaVonBan.get(i).get("soLuong");
	            float giaVon = (Float) danhSachGiaVonBan.get(i).get("gia");

	            if (soLuongTrongKho > 0) {
	                long soLuongSuDung = Math.min(soLuongConLai, soLuongTrongKho);
	                itemPrice ii = new itemPrice();
	                ii.setSoLuong(soLuongSuDung);
	                ii.setGia(giaVon);
	                l.getDanhSachPhanNho().add(ii);
	                soLuongConLai -= soLuongSuDung;
	                danhSachGiaVonBan.get(i).put("soLuong", soLuongTrongKho - soLuongSuDung);
	            }
	            i++;
	        }
	        if (!l.getDanhSachPhanNho().isEmpty()) {
	            float tongGiaVon = (float) l.getDanhSachPhanNho().stream()
	                    .mapToDouble(ii -> ii.getSoLuong() * ii.getGia()).sum();
	            l.setTonGiaBan(d.getTongTien());
	            l.setDonGiaBan(d.getDonGia());
	        } else {
	            l.setTonGiaBan(0f);
	        }

	        // Sửa logic lọc cc
	        if (cc == null || cc.isEmpty()) {
	            return l; // Không trả về lanBan nếu cc rỗng
	        } else {
	            if (cc.contains(d.getId())) {
	                return l;
	            } else {
	                return null;
	            }
	        }
	    }).filter(Objects::nonNull).collect(Collectors.toList());
	    b.setLanBan(danhSachLanBan);
	    return b;
	}
	public boolean KiemTraConDuKhong(BienThe b ) {
		List<SanPhamKiem> sanPhamKiems=sanPhamRepository.getSanPhamKiem(b.getId());
		long soluong=sanPhamKiems.stream().mapToLong(m->m.getSoLuong()).sum();
		if(soluong-b.getSoLuongKho()<=0) {
			return false;
		}
		else {
			return true;
		}
	}
	public int soLuongHaoHienGio (BienThe b ) {
		List<SanPhamKiem> sanPhamKiems=sanPhamRepository.getSanPhamKiem(b.getId());
		return sanPhamKiems.stream().mapToInt(d->d.getSoLuong()).sum();
	}
	
	
	
}

class bienTheLoiNhuan {
	String ten;
	int id;
	List<lanBan> lanBan = new ArrayList<lanBan>();

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<lanBan> getLanBan() {
		return lanBan;
	}

	public void setLanBan(List<lanBan> lanBan) {
		this.lanBan = lanBan;
	}

}

class lanBan {
	float tonGiaBan;
	float donGiaBan;
	List<CartItemLast> cartItemLasts;
	DonGiaBan donGiaBan2;
	
	

	public DonGiaBan getDonGiaBan2() {
		return donGiaBan2;
	}

	public void setDonGiaBan2(DonGiaBan donGiaBan2) {
		this.donGiaBan2 = donGiaBan2;
	}

	public float getDonGiaBan() {
		return donGiaBan;
	}

	public void setDonGiaBan(float donGiaBan) {
		this.donGiaBan = donGiaBan;
	}

	public List<CartItemLast> getCartItemLasts() {
		return cartItemLasts;
	}

	public void setCartItemLasts(List<CartItemLast> cartItemLasts) {
		this.cartItemLasts = cartItemLasts;
	}

	LocalDateTime ngayBan;
	List<itemPrice> danhSachPhanNho;
	EChiTietHoaDon e;

	public EChiTietHoaDon getE() {
		return e;
	}

	public void setE(EChiTietHoaDon e) {
		this.e = e;
	}

	public float getTonGiaBan() {
		return tonGiaBan;
	}

	public void setTonGiaBan(float tonGiaBan) {
		this.tonGiaBan = tonGiaBan;
	}

	public LocalDateTime getNgayBan() {
		return ngayBan;
	}

	public void setNgayBan(LocalDateTime ngayBan) {
		this.ngayBan = ngayBan;
	}

	public List<itemPrice> getDanhSachPhanNho() {
		return danhSachPhanNho;
	}

	public void setDanhSachPhanNho(List<itemPrice> danhSachPhanNho) {
		this.danhSachPhanNho = danhSachPhanNho;
	}
	

}


class itemPrice {
	long soLuong;
	float gia;
	long idLanNhap;
	LocalDateTime ngayNhap;

	public long getIdLanNhap() {
		return idLanNhap;
	}

	public void setIdLanNhap(long idLanNhap) {
		this.idLanNhap = idLanNhap;
	}

	public LocalDateTime getNgayNhap() {
		return ngayNhap;
	}

	public void setNgayNhap(LocalDateTime ngayNhap) {
		this.ngayNhap = ngayNhap;
	}

	public long getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(long soLuong) {
		this.soLuong = soLuong;
	}

	public float getGia() {
		return gia;
	}

	public void setGia(float gia) {
		this.gia = gia;
	}

}
