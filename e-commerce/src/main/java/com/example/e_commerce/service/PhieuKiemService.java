package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.e_commerce.DTO.request.PhieuKiemDTO;
import com.example.e_commerce.DTO.request.SanPham;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.ErrorData;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.ChiTietPhieuNhap;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.HoaDonHaoHut;
import com.example.e_commerce.model.HoaDonOnline;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.PhieuKiemHang;
import com.example.e_commerce.model.SanPhamKiem;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.repository.HoaDonHaoHutRepository;
import com.example.e_commerce.repository.PhieuKiemRepository;

@Service
public class PhieuKiemService {
	@Autowired
	private PhieuKiemRepository phieuKiemRepository;
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private ZaloPayService zaloPayService;
	
	@Autowired
	private GHNService ghnService;
	
	@Autowired
	private NguoiDungService nguoiDungService;
	
	@Autowired
	private SanPhamKiemService sanPhamKiemService;
	
	@Autowired
	private BienTheSerVice bienTheSerVice;
	
	@Autowired
	private HoaDonSerVice hoaDonSerVice;
	
	@Autowired
	private HoaDonHaoHutRepository hoaDonHaoHutRepository;
	
	
	@Autowired
	private HoaDonOnlineService hoaDonOnlineService;
	
	@Autowired
	private TrangThaiService trangThaiService;
	
	@Autowired
	private TrangThaiHoaDonService trangThaiHoaDonService;
	
	@Transactional
	public void save(List<PhieuKiemDTO> sanPhamKie,boolean traLuon) {
		PhieuKiemHang p= new PhieuKiemHang();
		p.setNhanVien((NhanVien)nguoiDungService.getById(jwtService.getIdUser()));
		p.setDaXacNhan(false);
		p.setNgayYeuCau(LocalDateTime.now());
		phieuKiemRepository.save(p);
		
		// xóa hóa đơn trước 
//		List<Long> danhSachCanXoa= sanPhamKie.get(0).getDanhSachHoaDonBiHuy();
//		if(danhSachCanXoa!=null) {
//			danhSachCanXoa.forEach(d->{
//				System.out.println("có đi vô đây xóa");
//				hoaDonOnlineService.removeHoaDonByNhanVien(d, traLuon);
//			});
//		}
		
		// kiểm tra pần vượt quá số lượng
		List<Long> a= new ArrayList<Long>();
		a.add((long)3); a.add((long)2);
		List<Map<String, Object>> danhSachThua= new ArrayList<Map<String,Object>>();
		sanPhamKie.forEach(d->{
			BienThe bienThe= bienTheSerVice.getById(d.getIdSanPham());
			long soLuongXoa= d.getDanhSachHoaDonBiHuy().stream().map(m->hoaDonSerVice.getById(m))
					.flatMap(m->m.getChiTietHoaDons().stream())
					.filter(m->m.getBienThe().getId()==d.getIdSanPham())
					.map(m->m.getSoLuong()).count();
			List<ChiTietHoaDon> c= bienTheSerVice.get(bienThe.getId(), a)
					.stream().filter(m->m.getHoaDon() instanceof HoaDonOnline).collect(Collectors.toList());;
			if((d.getSoLuong()-soLuongXoa) >bienThe.getSoLuongKho()) {
				Map<String, Object> duThua=new HashMap<String, Object>();
				duThua.put("id", d.getIdSanPham());
				List<Map<String, Object>> danhSachLoaiBo=c.stream().map(m->{
					Map<String, Object> con = new HashMap<String, Object>();
					con.put("idHoaDon",m.getId().getHdId());
					con.put("idBienThe",m.getId().getBtId());
					con.put("tongSoTien",m.getHoaDon().getTongTien());
					con.put("tongSoLuongTrongDon", m.getSoLuong());
					con.put("ngayLap",m.getHoaDon().getNgayLap());
					con.put("tyLeGiaTri",m.getTongTien()/m.getHoaDon().getTongTien());
					return con;
				}).collect(Collectors.toList());
				duThua.put("danhSachCoTheXoa", danhSachLoaiBo);
				danhSachThua.add(duThua);
			}
		});
		if(danhSachThua.size()!=0) {
			throw new ErrorData("Vui Lòng điều chỉnh Thông tin", HttpStatus.INTERNAL_SERVER_ERROR,danhSachThua);
			
		}
		if(sanPhamKie==null ||sanPhamKie.size()==0) {
			throw new GeneralException("Bạn phải cung cấp nhiều hơn một sản phẩm hao hụt ",HttpStatus.BAD_REQUEST);
			
		}
		sanPhamKie.forEach(d->{
			if(d.getSoLuong()<=0) {
				throw new GeneralException("Các sản phẩm trong phiếu hao phải lớn hơn 0",HttpStatus.BAD_REQUEST);
			}
			SanPhamKiem s= new SanPhamKiem();
			BienThe bienThe= bienTheSerVice.getById(d.getIdSanPham());
			s.setGhiChu(d.getGhiChu());
			s.setBienThe(bienThe);
			s.setPhieuKiemHang(p);
			s.setSoLuong(d.getSoLuong());
			d.getDanhSachHoaDonBiHuy().forEach(m->{
				HoaDonOnline hoaDonOnline=(HoaDonOnline)hoaDonSerVice.getById(m);
				if(hoaDonOnline.getTrangThai().getId()==13) {
		    		try {
			    		ghnService.cancelOrder(hoaDonOnline.getGhnCode());
					} catch (Exception e) {
						throw new GeneralException("Có lỗixảy ra trong quá trình cập nhập dữ liệu vui lòng kiểm tra lại", HttpStatus.BAD_REQUEST);
					}
		    		hoaDonOnline.setDaLenDonGHN(false);
		    		TrangThai trangThai = trangThaiService.getById(2);
		    		trangThaiHoaDonService.Save(hoaDonOnline, trangThai, "Hóa đơn được liệt vào danh sách bị hủy");
		    		hoaDonOnline.setTrangThai(trangThai);
		    		hoaDonSerVice.Update(hoaDonOnline);
		    	}
				HoaDonHaoHut hoaDonHaoHut= new HoaDonHaoHut();
				
				hoaDonHaoHut.setBienThe(bienTheSerVice.getById(d.getIdSanPham()));
				hoaDonHaoHut.setHoaDonOnline((HoaDonOnline)hoaDonSerVice.getById(m));
				hoaDonHaoHut.setPhieuKiemHang(p);
				hoaDonHaoHutRepository.save(hoaDonHaoHut);
			});		
			sanPhamKiemService.save(s);
		});
	}
	
	public Page<Map<String, Object>> getPhieuKiemForUppdate(int trang,String ten, LocalDateTime bd, LocalDateTime kt, int active, String maVach) {
	    Pageable p = PageRequest.of(trang, 5);
	    System.out.println( (kt.withHour(0).withMinute(0).withSecond(0).withNano(0)).toString());
	    Page<PhieuKiemHang> phieukiemPage = phieuKiemRepository.findByNguoiDungTenAndNgayLapBetween(p, ten,maVach, bd, (kt.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)),active);

	    return phieukiemPage.map(d -> {
	        Map<String, Object> m = new HashMap<>();
	        m.put("id", d.getId());
	        m.put("soLuongHangHoaHut", d.getSanPhamKiem().size());
	        m.put("soLuongHangHaoHut", d.getSanPhamKiem().stream().mapToLong(du -> du.getSoLuong()).sum());
	        m.put("nhanVienLap", d.getNhanVien().getTen());
	        m.put("daXacNhan", d.isDaXacNhan());
	        m.put("ngayLap", d.getNgayYeuCau());
	        return m;
	    });
	}
	
	public Map<String, Object> getPhieuKiem(long idPhieuKiem) {
		PhieuKiemHang phieuKiemHang= phieuKiemRepository.findById(idPhieuKiem).orElseThrow(()->new GeneralException("Vui lòng cung cấp thông tin phiếu kiểm",HttpStatus.BAD_REQUEST));
		List<Long> a= new ArrayList<Long>();
		a.add((long)3); a.add((long)2); a.add((long)13);
		Map<String, Object> p= new HashMap<String, Object>();
		p.put("id", phieuKiemHang.getId());
		p.put("ngayLapPhieu", phieuKiemHang.getNgayYeuCau());
		p.put("nhanVienLap", phieuKiemHang.getNhanVien().getTen());
		p.put("daXacNhan", phieuKiemHang.isDaXacNhan());
		List<SanPhamKiem> danhSachChiTiet= phieuKiemHang.getSanPhamKiem();
		
		p.put("danhSachChiTiet", danhSachChiTiet.stream().map(d->{
			PhieuKiemDTO phieuKiemDTO= new PhieuKiemDTO();
			phieuKiemDTO.setGhiChu(d.getGhiChu());
			phieuKiemDTO.setIdSanPham(d.getBienThe().getId());
			phieuKiemDTO.setSoLuong(d.getSoLuong());
			phieuKiemDTO.setSoLuongThucTe(d.getBienThe().getSoLuongKho());
			phieuKiemDTO.setSanPham(d.getBienThe().getSanPham().getTen());
			phieuKiemDTO.setTenBienThe(d.getBienThe().getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định" : d.getBienThe().getTen());
			phieuKiemDTO.setSanPham_Cha(d.getBienThe().getSanPham().getId());
			phieuKiemDTO.setMaVach(d.getBienThe().getMaVach());
			phieuKiemDTO.setDanhSachHoaDonBiHuy(
					d.getHoaDonHaoHuts().stream()
					.map(m->{
//						if(m.getHoaDonOnline().getTrangThai().getId()==4
//							|| m.getHoaDonOnline().getTrangThai().getId()==9) {
//							hoaDonHaoHutRepository.delete(m);
//							return (long)0;
//						}
						return m.getHoaDonOnline().getId();
					}).filter(m->m!=0).collect(Collectors.toList()));
			List<ChiTietHoaDon> c= bienTheSerVice.get(d.getBienThe().getId(), a)
					.stream().filter(m->m.getHoaDon() instanceof HoaDonOnline).collect(Collectors.toList());
			int tonSoLuongDaDat=c.stream().mapToInt(m->m.getSoLuong()).sum();
			phieuKiemDTO.setSoLuongDaDat(tonSoLuongDaDat);
				List<Map<String, Object>> danhSachLoaiBo=c.stream().map(m->{
					Map<String, Object> con = new HashMap<String, Object>();
					con.put("idHoaDon",m.getId().getHdId());
					con.put("idBienThe",m.getId().getBtId());
					con.put("tongSoTien",m.getHoaDon().getTongTien());
					con.put("tongSoLuongTrongDon", m.getSoLuong());
					con.put("ngayLap",m.getHoaDon().getNgayLap());
					con.put("tyLeGiaTri",m.getTongTien()/m.getHoaDon().getTongTien());
					return con;
				}).collect(Collectors.toList());
				phieuKiemDTO.setResult(danhSachLoaiBo);
				return phieuKiemDTO;
		}));
		return p;
	}
	
	public Map<String, Object> getThongTinHaoHut(int trang, String ten, LocalDateTime bd, LocalDateTime kt, int active, String maVach) {
	    Pageable page = PageRequest.of(trang, 5);
	    SanPhamService sanPhamService=ServiceLocator.getBean(SanPhamService.class);
	    Page<BienThe> danhSachChiTiet1 = phieuKiemRepository.findDistinctBienTheByDieuKien(page, ten, maVach, bd, kt.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0), active);
	    Set<BienThe> danhSachBienTheHaoHut= new HashSet<BienThe>();
	    Map<String, Object> r= new HashMap<String, Object>();
	    
	    List<Map<String, Object>> danhSachChiTiet = new ArrayList<>();

        danhSachChiTiet1.forEach(d -> {
            Map<String, Object> m = new HashMap<>();
            m.put("tenBienThe", d.getSanPham().getTen()+" - "+(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định": d.getTen()));
            m.put("anhBienThe", d.getAnhBia());
            Map<String, Object> k = sanPhamService.tinhGiaVonHaoHut(d.getId(), bd, kt,active);
            if (k != null) {
                m.putAll(k);
                Float giaTriHao = (Float) k.get("tongGiaTriHao");
            }
            danhSachChiTiet.add(m);
        });
        r.put("tongGiaTriHaoHut", danhSachChiTiet);
        r.put("tongTrang", danhSachChiTiet1.getTotalPages());
        r.put("tongSoPhanTu", danhSachChiTiet1.getTotalElements());
        return r;
	}

	
	@Transactional
	public long update(long id , List<PhieuKiemDTO> ds) {
		PhieuKiemHang phieuKiemHang= phieuKiemRepository.findById(id).orElseThrow(()->new GeneralException("Vui lòng cung cấp thông tin phiếu kiểm",HttpStatus.BAD_REQUEST));
		PhieuKiemHang p2= new PhieuKiemHang();
		p2.setNgayYeuCau(phieuKiemHang.getNgayYeuCau());
		p2.setNhanVien(phieuKiemHang.getNhanVien());
		p2.setDaXacNhan(false);
		phieuKiemRepository.save(p2);
		phieuKiemRepository.delete(phieuKiemHang);
		IntStream.range(0, ds.size()).forEach(i -> {
		    PhieuKiemDTO d = ds.get(i);
		    int index = i + 1; 

		    BienThe b = bienTheSerVice.getById(d.getIdSanPham());

		    if ((b.getSoLuongKho() >= d.getSoLuong()) && d.getDanhSachHoaDonBiHuy().size() > 0) {
		        throw new GeneralException("Phân loại thứ: " + index + " không cần xóa thêm hóa đơn, vui lòng điều chỉnh lại", HttpStatus.BAD_REQUEST);
		    }

		    AtomicInteger soLuongDuocXoa = new AtomicInteger();
		    List<HoaDon> hd = d.getDanhSachHoaDonBiHuy().stream().map(m -> {
		        HoaDon h = hoaDonSerVice.getById(m);
		        int so = h.getChiTietHoaDons().stream()
		                .filter(k -> k.getBienThe().getId() == d.getIdSanPham())
		                .mapToInt(l -> l.getSoLuong())
		                .sum();
		        soLuongDuocXoa.addAndGet(so);
		        return h;
		    }).collect(Collectors.toList());

		    if (b.getSoLuongKho() < (d.getSoLuong() - soLuongDuocXoa.get())) {
		        throw new GeneralException("Biến thể thứ: " + index + " vượt quá số lượng được xóa vui lòng điều chỉnh xóa các hóa đơn", HttpStatus.BAD_REQUEST);
		    }

		    hd.forEach(m -> {
		    	
		    	if(m.getTrangThai().getId()==13) {
		    		try {
			    		ghnService.cancelOrder(((HoaDonOnline)m).getGhnCode());
					} catch (Exception e) {
						throw new GeneralException("Có lỗixảy ra trong quá trình cập nhập dữ liệu vui lòng kiểm tra lại", HttpStatus.BAD_REQUEST);
					}
		    		((HoaDonOnline)m).setDaLenDonGHN(false);
		    		TrangThai trangThai = trangThaiService.getById(2);
		    		trangThaiHoaDonService.Save(m, trangThai, "Hóa đơn được liệt vào danh sách bị hủy");
		    		m.setTrangThai(trangThai);
		    		hoaDonSerVice.Update(m);
		    	}
		        HoaDonHaoHut hoaDonHaoHut = new HoaDonHaoHut();
		        hoaDonHaoHut.setBienThe(b);
		        hoaDonHaoHut.setHoaDonOnline((HoaDonOnline) m);
		        hoaDonHaoHut.setPhieuKiemHang(p2);
		        hoaDonHaoHutRepository.save(hoaDonHaoHut);
		    });

		    SanPhamKiem sp = new SanPhamKiem();
		    sp.setBienThe(b);
		    sp.setGhiChu(d.getGhiChu());
		    sp.setPhieuKiemHang(p2);
		    sp.setSoLuong(d.getSoLuong());
		    sanPhamKiemService.save(sp);
		});

		return p2.getId();
	}
	
	@Transactional
	public void DuyetPhieuKiem(long idPhieuKiem) {
		PhieuKiemHang phieuKiemHang= phieuKiemRepository.findById(idPhieuKiem).orElseThrow(()->new GeneralException("Vui lòng cung cấp thông tin phiếu kiểm",HttpStatus.BAD_REQUEST));
		TrangThai t= trangThaiService.getById(9);
		// tính hao hụt của sản phẩm.
		phieuKiemHang.getSanPhamKiem().forEach(d->{
			BienThe b=d.getBienThe();
			
			// lấy số lượng mà nó sẽ trừ:
			int soLuong = d.getHoaDonHaoHuts().stream()
					.flatMap(m -> m.getHoaDonOnline().getChiTietHoaDons().stream())
					.filter(h->h.getBienThe().getId()==b.getId()).mapToInt(m->m.getSoLuong())
					.sum();
			
			if((d.getSoLuong()-soLuong)>b.getSoLuongKho()) {
				throw new GeneralException("Phân loại: "+b.getSanPham().getTen()+" - "+(b.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định": b.getTen())
						+ " vuợt quá số lượng được xóa vui lòng điều chỉnh lại", HttpStatus.BAD_REQUEST);
			}
			b.setSoLuongKho(b.getSoLuongKho()-d.getSoLuong());
			bienTheSerVice.save(b);
		});
		phieuKiemHang.getSanPhamKiem().stream().flatMap(m->m.getHoaDonHaoHuts().stream()).
		forEach(d->{
			hoaDonSerVice.HuyDon3(d.getHoaDonOnline().getId());
			HoaDonOnline g= d.getHoaDonOnline();
			long tienGoc = (long) d.getHoaDonOnline().getTongTien();
			long soTienRefound = ((tienGoc + 999) / 1000) * 1000; 
			if(d.getHoaDonOnline().isDaThanhTona()==true && d.getHoaDonOnline().isDaHoanHang()==false) {
				try {
					zaloPayService.refund(
							d.getHoaDonOnline().getHD_zpTrans_Id()+"",
							soTienRefound,
							"Hoàn tiền hóa đơn SKINLY",
							d.getHoaDonOnline().getRefund(),
							null);
					g.setDaHoanHang(true);
					hoaDonSerVice.Update(g);
				} catch (Exception e) {
					g.setDaHoanHang(false);
					hoaDonSerVice.Update(g);
				}
			}
		});
		phieuKiemHang.setDaXacNhan(true);
		
	}
	
	public Map<String, Object> getHoaHutHangHoa(String ten, LocalDateTime bd, LocalDateTime kt, int active, String maVach) {
		SanPhamService sanPhamService=ServiceLocator.getBean(SanPhamService.class);
		 Pageable p = PageRequest.of(0, 500);
		 Page<PhieuKiemHang> phieukiemPage = phieuKiemRepository.findByNguoiDungTenAndNgayLapBetween(p,ten, maVach, bd, kt.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0),active);
		 // Lấy ra danh sách chi tiết sản phẩm hao hụt
		 System.out.println("số lượng phiếu kiểm hàng: "+phieukiemPage.getContent().size());
		 Set<BienThe> danhSachBienTheHaoHut= new HashSet<BienThe>();
		 Map<String, Object> r= new HashMap<String, Object>();
		 AtomicInteger tongSoLuongHaoHut= new AtomicInteger(0);
		 AtomicInteger tongSoHoaDonPhaiHuy= new AtomicInteger(0);
		 AtomicInteger donHuyNhungChuaDuyet= new AtomicInteger(0);
		 AtomicInteger donHuyDaDuyet= new AtomicInteger(0);
		 AtomicLong tongSoHoaDonDuKienHuy= new AtomicLong(0);
		 AtomicLong tongSoHoaDonHuy= new AtomicLong(0);
		 phieukiemPage.getContent().stream().flatMap(d->d.getSanPhamKiem().stream())
		 .forEach(d->{
			 System.out.println("đi vào đây cộng nè ");
			 danhSachBienTheHaoHut.add(d.getBienThe());
			 tongSoLuongHaoHut.addAndGet(d.getSoLuong());
			 System.out.println(d.getSoLuong());
		 });
		 phieukiemPage.getContent().forEach(d->{
			 if(d.isDaXacNhan()==true) {
				 donHuyDaDuyet.addAndGet(1);
				 System.out.println("ĐƠN ĐÃ XÁC NHẬN");
				 long tongSoHuy= d.getSanPhamKiem().stream().flatMap(m->m.getHoaDonHaoHuts().stream())
						 .distinct().count();
				 tongSoHoaDonHuy.addAndGet(tongSoHuy);
			 }
			 else {
				 donHuyNhungChuaDuyet.addAndGet(1);
				 System.out.println("Phiếu dự kiến id: "+d.getId());
				 long tongSoHuy= d.getSanPhamKiem().stream().flatMap(m->m.getHoaDonHaoHuts().stream())
						 .distinct().count();
				 System.out.println("Dự kiến hủy: "+tongSoHuy);
				 tongSoHoaDonDuKienHuy.addAndGet(tongSoHuy);
			 }
		 });
		 SanPhamService sp= ServiceLocator.getBean(SanPhamService.class);
		 r.put("tongSoLuongHaoHut", tongSoLuongHaoHut.get());
		 r.put("tongSoLanHaoHut", phieukiemPage.getContent().size());
		 r.put("tongSoLuongChoDuyet", donHuyNhungChuaDuyet.get());
		 r.put("tonSoLuongDonDaDuyet", donHuyNhungChuaDuyet.get());
		 r.put("tongSanPhamHaoHut",danhSachBienTheHaoHut.size());
		 r.put("tongSoHoaDonDuKienHuy",tongSoHoaDonDuKienHuy.get());
		 r.put("tongSoHoaDonHuy",tongSoHoaDonHuy.get());
		 List<Map<String, Object>> danhSachChiTiet = new ArrayList<>();
	        AtomicReference<Float> tongGiaTriHaoHut = new AtomicReference<>(0f); // Khởi tạo với 0f

	        danhSachBienTheHaoHut.forEach(d -> {
	            Map<String, Object> m = new HashMap<>();
	            m.put("tenBienThe", d.getSanPham().getTen()+" - "+(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")?"Mặc định": d.getTen()));
	            m.put("anhBienThe", d.getAnhBia());
	            Map<String, Object> k = sanPhamService.tinhGiaVonHaoHut(d.getId(), bd, kt,active);
	            if (k != null) {
	                m.putAll(k);
	                Float giaTriHao = (Float) k.get("tongGiaTriHao");
	                if (giaTriHao != null) {
	                    tongGiaTriHaoHut.updateAndGet(current -> current + giaTriHao);
	                }
	            }
	            danhSachChiTiet.add(m);
	        });
	        r.put("tongGiaTriHaoHut", tongGiaTriHaoHut.get());
		 r.put("tongSoTienHaoHut", danhSachChiTiet);
		 return r;
	}
	
	@Transactional
	public void deleteKhauHao(long id) {
		PhieuKiemHang phieuKiemHang= phieuKiemRepository.findById(id).orElseThrow(()->new GeneralException("Vui lòng cung cấp thông tin phiếu kiểm",HttpStatus.BAD_REQUEST));
		
		phieuKiemRepository.delete(phieuKiemHang);
	}
	
}
