package com.example.e_commerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudinary.api.exceptions.BadRequest;
import com.example.e_commerce.DTO.response.PhieuNhapDTO;
import com.example.e_commerce.DTO.response.SanPhamPhieuNhap;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.mapper.PhieuNhapMapper;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.model.ChiTietPhieuNhap;
import com.example.e_commerce.model.DonViCungCap;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.PhieuNhap;
import com.example.e_commerce.model.QuyCachDongGoiLanNhap;
import com.example.e_commerce.repository.PhieuNhapRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PhieuNhapService {
	@Autowired
	private ChiTietPhieuNhapService chiTietPhieuNhapService;
	
	@Autowired
	private DonViCungCapService donViCungCapService;
	
	@Autowired
	private BienTheSerVice bienTheSerVice;
	
	@Autowired
	private BienTheDongGoiChanService bienTheDongGoiChanService;
	
	@Autowired
	private QuyCachDongGoiLanNhapService quyCachDongGoiLanNhapService;
	
	@Autowired
	NguoiDungService nguoiDungService;
	
	@Autowired
	private JwtService jwtService;
	@Autowired
	private PhieuNhapRepository phieuNhapRepository;
	public Page<Map<String, Object>> getPhieuNhapByTimeAndID(LocalDateTime nbd, LocalDateTime nkt, long id, int trang) {
	    Pageable pageable = PageRequest.of(trang, 5);
	    Page<PhieuNhap> pp = phieuNhapRepository.getPhieuNhap(nbd, nkt, id, pageable);
	    long sl=pp.getContent().stream().flatMap(d->d.getChiTietPhieuNhap().stream()).map(l->l.getBienThe().getSanPham().getId())
	    .distinct().count();
	    Page<Map<String, Object>> result = pp.map(data -> {
	    	
	        Map<String, Object> d = new HashMap<>();
	        d.put("soluong", sl);
	        d.put("id", data.getId());
	        d.put("ngayNhap", data.getNgayNhapHang());
	        d.put("tongTien", data.getTongTien());
	        d.put("thueVAT", data.getThueVAT());
	        d.put("nhanVien", data.getNguoiDung().getTen());
	        return d;
	    });
	    

	    return result;
	}
	public Page<Map<String, Object>> getPhieuNhapByTimeAndTen(LocalDateTime nbd, LocalDateTime nkt, 
			String tenDonViCungCap, String  tenSanPham, int trang) {
		nkt = nkt.toLocalDate()
	            .plusDays(1)
	            .atStartOfDay();
	    Pageable pageable = PageRequest.of(trang, 5);
	    Page<PhieuNhap> pp = phieuNhapRepository.getPhieuNhapByTen(nbd, nkt, tenDonViCungCap,tenSanPham, pageable);
	    Page<Map<String, Object>> result = pp.map(data -> {
	        Map<String, Object> d = new HashMap<>();
	        d.put("id", data.getId());
	        d.put("ngayNhap", data.getNgayNhapHang());
	        d.put("tongTien", data.getTongTien());
	        d.put("thueVAT", data.getThueVAT());
	        d.put("nhanVien", data.getNguoiDung().getTen());
	        d.put("daDuyet", data.isDuyet());
	        return d;
	    });

	    return result;
	}
	public PhieuNhapDTO getChiTiet(Long phieunhap) {
		PhieuNhap p= phieuNhapRepository.findById(phieunhap).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy phiếu nhập"));
		return PhieuNhapMapper.MappToPhieuNhapDTO(p);
	}
	@Transactional
	public void savePhieuNhap(PhieuNhapDTO d) {
		PhieuNhapMapper.mapperPhieuNhapDTOToPhieuNhap(d);
		PhieuNhap p= new PhieuNhap();
		p.setNgayNhapHang(LocalDateTime.now());
		NhanVien nhanVien=(NhanVien) nguoiDungService.getById(jwtService.getIdUser());
		p.setNguoiDung(nhanVien);
		p.setThueVAT(d.getThueVAT());
		DonViCungCap donViCungCap = donViCungCapService.getById(d.getIdDonViCungCap());
		p.setDonViCungCap(donViCungCap);
		phieuNhapRepository.save(p);
		List<ChiTietPhieuNhap>cc=
		d.getSanPham().stream().map((dd)->{
			ChiTietPhieuNhap c= new ChiTietPhieuNhap();
			BienThe b= bienTheSerVice.getById((int)dd.getIdSanPham());
			c.setBienThe(b);
			c.setSoLuong(dd.getSoLuong());
			c.setDonGia(dd.getDonGia());
			c.setHanSuDung(LocalDate.now());
			c.setPhieuNhap(p);
			return c;
		}).collect(Collectors.toList());
		p.setChiTietPhieuNhap(cc);
		BienTheSerVice bientheSerVice= ServiceLocator.getBean(BienTheSerVice.class);
		
		System.out.println("số lượng con: "+p.getChiTietPhieuNhap().size());
		AtomicReference<Float> tongTien=new AtomicReference<Float>(0f);
		p.getChiTietPhieuNhap().stream().forEach(dd->{
			BienThe bienThe=dd.getBienThe();
			bienThe.setSoLuongKho(bienThe.getSoLuongKho()+dd.getSoLuong());
			bientheSerVice.save(bienThe);
			chiTietPhieuNhapService.Save(dd);
			tongTien.updateAndGet(v->v+dd.getDonGia()*dd.getSoLuong());
		});
		System.out.println("id đơn vị cung cấp: khoản "+p.getDonViCungCap().getId());
		p.setTongTien(tongTien.get());
		
		phieuNhapRepository.save(p);
	}
	
	@Transactional
	public void savePhieuNhapV2(PhieuNhapDTO d) {
//		PhieuNhapMapper.mapperPhieuNhapDTOToPhieuNhap(d);
		if(d.getSanPham().size()<=0) {
			throw new GeneralException("Vui lòng cung cấp nhiều hơn một phân loại",HttpStatus.BAD_REQUEST);
			
		}
		PhieuNhap p= new PhieuNhap();
		p.setNgayNhapHang(LocalDateTime.now());
		NhanVien nhanVien=(NhanVien) nguoiDungService.getById(jwtService.getIdUser());
		p.setNguoiDung(nhanVien);
		p.setThueVAT(d.getThueVAT());
		DonViCungCap donViCungCap = donViCungCapService.getById(d.getIdDonViCungCap());
		p.setDonViCungCap(donViCungCap);
		phieuNhapRepository.save(p);
		AtomicReference<Float> tongTien=new AtomicReference<Float>(0f);
		// lấy danh sách biến thể:))
		List<Long> danhSachBienThe= d.getSanPham().stream().map(m->m.getIdSanPham()).distinct().collect(Collectors.toList());
		danhSachBienThe.forEach(p2->{
			AtomicReference<Float> tongTien2=new AtomicReference<Float>(0f);
			AtomicInteger tongSoLuong=new AtomicInteger();
			BienThe b = bienTheSerVice.getById(p2.intValue());
			d.getSanPham().stream().filter(o->o.getIdSanPham()==p2).forEach(j->{
				BienTheDongGoiChan bienTheDongGoiChan= bienTheDongGoiChanService.getBienTheDongGoiByMaVach(j.getMaVach());
				tongTien2.updateAndGet(i->i+(j.getSoLuong()*j.getDonGia()));
				if(j.getSoLuong()<=0) {
					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp số lượng",HttpStatus.BAD_REQUEST);
				}
				if(j.getDonGia()<=0) {
					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp giá",HttpStatus.BAD_REQUEST);
				}
				tongSoLuong.addAndGet(j.getSoLuong()*bienTheDongGoiChan.getId().getSOLUONG());
				QuyCachDongGoiLanNhap quyCachDongGoiLanNhap= new QuyCachDongGoiLanNhap();
				quyCachDongGoiLanNhap.setBienThe(b);
				quyCachDongGoiLanNhap.setDongGoiChan(bienTheDongGoiChan.getDongGoiChan());
				quyCachDongGoiLanNhap.setPhieuNhap(p);
				quyCachDongGoiLanNhap.setDonGia(j.getDonGia());
				quyCachDongGoiLanNhap.setSoLuong(j.getSoLuong());
				quyCachDongGoiLanNhap.getId().setBTDGC_SOLUONG(bienTheDongGoiChan.getId().getSOLUONG());
				quyCachDongGoiLanNhapService.save(quyCachDongGoiLanNhap);
			});
			ChiTietPhieuNhap c= new ChiTietPhieuNhap();
			c.setBienThe(b);
			c.setPhieuNhap(p);
			c.setDonGia((tongTien2.get())/tongSoLuong.get());
			c.setSoLuong(tongSoLuong.get());
			c.setHanSuDung(LocalDate.now());
			chiTietPhieuNhapService.Save(c);
			// KHÔNG TĂNG SỐ LƯỢNG TRƯỚC
			
			// b.setSoLuongKho(b.getSoLuongKho()+tongSoLuong.get());
			tongTien.updateAndGet(v->v+tongTien2.get());
			
		});
		p.setDuyet(false);
		p.setTongTien(tongTien.get());
		phieuNhapRepository.save(p);
	}
	
	
	@Transactional
	public long Update(PhieuNhapDTO d, long id) {
		PhieuNhap p1= phieuNhapRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy phiếu nhập",HttpStatus.BAD_REQUEST));
		phieuNhapRepository.delete(p1);
//		PhieuNhapMapper.mapperPhieuNhapDTOToPhieuNhap(d);
		PhieuNhap p= new PhieuNhap();
		p.setNgayNhapHang(p1.getNgayNhapHang());
		NhanVien nhanVien=(NhanVien) nguoiDungService.getById(jwtService.getIdUser());
		p.setNguoiDung(nhanVien);
		p.setThueVAT(d.getThueVAT());
		DonViCungCap donViCungCap = donViCungCapService.getById(d.getIdDonViCungCap());
		p.setDonViCungCap(donViCungCap);
		phieuNhapRepository.save(p);
		AtomicReference<Float> tongTien=new AtomicReference<Float>(0f);
		// lấy danh sách biến thể:))
		List<Long> danhSachBienThe= d.getSanPham().stream().map(m->m.getIdSanPham()).distinct().collect(Collectors.toList());
		danhSachBienThe.forEach(p2->{
			AtomicReference<Float> tongTien2=new AtomicReference<Float>(0f);
			AtomicInteger tongSoLuong=new AtomicInteger();
			BienThe b = bienTheSerVice.getById(p2.intValue());
			d.getSanPham().stream().filter(o->o.getIdSanPham()==p2).forEach(j->{
				BienTheDongGoiChan bienTheDongGoiChan= bienTheDongGoiChanService.getBienTheDongGoiByMaVach(j.getMaVach());
				tongTien2.updateAndGet(i->i+(j.getSoLuong()*j.getDonGia()));
				if(j.getSoLuong()<=0) {
					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp số lượng",HttpStatus.BAD_REQUEST);
				}
				if(j.getDonGia()<=0) {
					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp giá",HttpStatus.BAD_REQUEST);
				}
				tongSoLuong.addAndGet(j.getSoLuong()*bienTheDongGoiChan.getId().getSOLUONG());
				QuyCachDongGoiLanNhap quyCachDongGoiLanNhap= new QuyCachDongGoiLanNhap();
				quyCachDongGoiLanNhap.setBienThe(b);
				quyCachDongGoiLanNhap.setDongGoiChan(bienTheDongGoiChan.getDongGoiChan());
				quyCachDongGoiLanNhap.setPhieuNhap(p);
				quyCachDongGoiLanNhap.setDonGia(j.getDonGia());
				quyCachDongGoiLanNhap.setSoLuong(j.getSoLuong());
				quyCachDongGoiLanNhap.getId().setBTDGC_SOLUONG(bienTheDongGoiChan.getId().getSOLUONG());
				quyCachDongGoiLanNhapService.save(quyCachDongGoiLanNhap);
			});
			ChiTietPhieuNhap c= new ChiTietPhieuNhap();
			c.setBienThe(b);
			c.setPhieuNhap(p);
			c.setDonGia((tongTien2.get())/tongSoLuong.get());
			c.setSoLuong(tongSoLuong.get());
			c.setHanSuDung(LocalDate.now());
			chiTietPhieuNhapService.Save(c);
			// KHÔNG TĂNG SỐ LƯỢNG TRƯỚC
			
			// b.setSoLuongKho(b.getSoLuongKho()+tongSoLuong.get());
			tongTien.updateAndGet(v->v+tongTien2.get());
			
		});
		p.setDuyet(false);
		p.setTongTien(tongTien.get());
		phieuNhapRepository.save(p);
		return p.getId();
	}
	
	@Transactional
	public void deletePhieuNhap(long id) {
		PhieuNhap p1= phieuNhapRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy phiếu nhập",HttpStatus.BAD_REQUEST));
		phieuNhapRepository.delete(p1);
	}
	
//	public void DuyetHoaDon(long id) {
//		PhieuNhap p= phieuNhapRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy phiếu nhập",HttpStatus.BAD_REQUEST));
//		List<BienThe> bienThes=p.getQuyCachDongGoiLanNhap().stream().map(d->{
//			return d.getBienThe();
//		}).collect(Collectors.toList());
//		
//		bienThes.forEach(p2->{
//			AtomicReference<Float> tongTien2=new AtomicReference<Float>(0f);
//			AtomicInteger tongSoLuong=new AtomicInteger();
//			p.getQuyCachDongGoiLanNhap().stream().filter(p3->p2.getId()==p3.getBienThe().getId()).forEach(j->{
//				BienTheDongGoiChan bienTheDongGoiChan= bienTheDongGoiChanService.getBienTheDongGoiByMaVach(j.getMaVach());
//				tongTien2.updateAndGet(i->i+(j.getSoLuong()*j.getDonGia()));
//				if(j.getSoLuong()<=0) {
//					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp số lượng",HttpStatus.BAD_REQUEST);
//				}
//				if(j.getDonGia()<=0) {
//					throw new GeneralException("Biến thể có mã vạch: "+j.getMaVach()+" chưa cung cấp giá",HttpStatus.BAD_REQUEST);
//				}
//				tongSoLuong.addAndGet(j.getSoLuong()*bienTheDongGoiChan.getId().getSOLUONG());
//				QuyCachDongGoiLanNhap quyCachDongGoiLanNhap= new QuyCachDongGoiLanNhap();
//				quyCachDongGoiLanNhap.setBienThe(b);
//				quyCachDongGoiLanNhap.setDongGoiChan(bienTheDongGoiChan.getDongGoiChan());
//				quyCachDongGoiLanNhap.setPhieuNhap(p);
//				quyCachDongGoiLanNhap.setDonGia(j.getDonGia());
//				quyCachDongGoiLanNhap.setSoLuong(j.getSoLuong());
//				quyCachDongGoiLanNhap.getId().setBTDGC_SOLUONG(bienTheDongGoiChan.getId().getSOLUONG());
//				quyCachDongGoiLanNhapService.save(quyCachDongGoiLanNhap);
//			});
////			ChiTietPhieuNhap c= new ChiTietPhieuNhap();
////			c.setBienThe(b);
////			c.setPhieuNhap(p);
////			c.setDonGia((tongTien2.get())/tongSoLuong.get());
////			c.setSoLuong(tongSoLuong.get());
////			c.setHanSuDung(LocalDate.now());
////			chiTietPhieuNhapService.Save(c);
//			// KHÔNG TĂNG SỐ LƯỢNG TRƯỚC
//			
//			// b.setSoLuongKho(b.getSoLuongKho()+tongSoLuong.get());
//			tongTien.updateAndGet(v->v+tongTien2.get());
//			
//		});
//		p.setDuyet(true);
//	}
//	{
//	    "tenSanPham": "Gel Tẩy Tế Bào Chết Naruko Chiết Xuất Tràm Tr",
//	    "duongDanIcon": "https://cdn-icons-png.flaticon.com/128/3703/3703259.png",
//	    "soLuongTronQuyCach": 1,
//	    "id": 52,
//	    "maVach": "255221",
//	    "quyCachDongGoi": "LẺ",
//	    "ten": "Type da nhạy cảm ( 30g )",
//	    "macDinh": true,
//	    "soLuong": 1,
//	    "donGia": 0
//	}
//	
	public Map<String, Object> getUpdate(long id) {
		PhieuNhap p= phieuNhapRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy phiếu nhập",HttpStatus.BAD_REQUEST));
		PhieuNhapDTO d= new PhieuNhapDTO();
		d.setThueVAT(p.getThueVAT());
		List<SanPhamPhieuNhap> pi= p.getQuyCachDongGoiLanNhap().stream().map(m->{
			System.out.println(m.getId().getBTDGC_SOLUONG());
			System.out.println(m.getBienThe().getId());
			System.out.println(m.getId().getDGC_ID());
			BienTheDongGoiChan maVach= phieuNhapRepository.getMaVachQuyCach(m.getId().getBTDGC_SOLUONG(),m.getId().getDGC_ID(),m.getBienThe().getId());
			System.out.println(maVach);
			SanPhamPhieuNhap s= new SanPhamPhieuNhap();
			s.setTenSanPham(m.getBienThe().getSanPham().getTen());
			s.setDuongDanIcon(m.getDongGoiChan().getDuongDan());
			s.setSoLuongTronQuyCach(maVach.getId().getSOLUONG());
			s.setId(m.getBienThe().getId());
			s.setMaVach(maVach.getMaVach());
			s.setQuyCachDongGoi(m.getDongGoiChan().getTenQuyCach());
			s.setTen(m.getBienThe().getTen());
			s.setMacDinh(true);
			s.setSoLuong(m.getSoLuong());
			s.setDonGia(m.getDonGia());
			return s;
		}).collect(Collectors.toList());
		d.setSanPham(pi);
		d.setIdDonViCungCap(p.getDonViCungCap().getId());
		Map<String,Object> m= new HashMap<String, Object>();
		m.put("data", d);
		m.put("canUpdate",!p.isDuyet());
		m.put("tenConTy", p.getDonViCungCap().getTen());
		m.put("tongTien", p.getTongTien());
		m.put("tongTienPhaiTra", p.getTongTien()-p.getThueVAT());
		return m;
	}
	
	public void duyet(long id) {
		PhieuNhap p= phieuNhapRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy phiếu nhập",HttpStatus.BAD_REQUEST));
		p.getChiTietPhieuNhap().forEach(d->{
			BienThe b=d.getBienThe();
			b.setSoLuongKho(d.getSoLuong()+b.getSoLuongKho());
			bienTheSerVice.save(b);
		});
		p.setDuyet(true);
		phieuNhapRepository.save(p);
	}
}
