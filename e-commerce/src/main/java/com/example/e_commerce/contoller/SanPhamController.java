package com.example.e_commerce.contoller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.DTO.request.FilterViewProduct;
import com.example.e_commerce.DTO.request.SanPham;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.ExportExcelService;
import com.example.e_commerce.service.SanPhamService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(Routes.API+ "/sanpham")
public class SanPhamController {
	@Autowired
	private SanPhamService sanPhamService;
	@PostMapping
	public ResponseEntity<APIResponse> save(
			@RequestPart("sanpham") String sanpham,
			@RequestPart("anhphu") List<MultipartFile> anhPhu,
			@RequestPart("anhbia") MultipartFile anhChinh,
			@RequestPart(value = "anhbienthe", required = false) List<MultipartFile> anhBienThe) throws Exception {
		ObjectMapper op= new ObjectMapper();
	
		try {
	        SanPham sp = op.readValue(sanpham, SanPham.class);
	        sp.check();
	        sanPhamService.save(sp,anhPhu,anhChinh,anhBienThe);
	        return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
	    } catch (Exception e) {
	        throw e;
	    }
	}
	@PostMapping("/update/{id}")
	public ResponseEntity<APIResponse> update(
			@RequestPart("sanpham") String sanpham,
			@RequestPart(value="anhphanloainew",required = false) List<MultipartFile> anhPhanLoaiNew,
			@RequestPart(value="anhGioiThieuNew",required = false) List<MultipartFile> anhGioiThieuNew,
			@RequestPart(value="anhbianew",required = false) MultipartFile anhChinh,
			@PathVariable(value = "id", required = false) long id,
			@RequestPart(value = "anhgioithieuxoa", required = false) List<Long> anhGioiThieuXoa
			) throws Exception {
		ObjectMapper op= new ObjectMapper();
	
		try {
	        SanPham sp = op.readValue(sanpham, SanPham.class);
	        sp.checkForUpdate(anhPhanLoaiNew);
	        sanPhamService.Update(sp,anhPhanLoaiNew,anhGioiThieuNew,anhChinh,id);
	        return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
	    } catch (Exception e) {
	        throw e;
	    }
	}
	@GetMapping("/getcreateflashsale")
	public ResponseEntity<APIResponse> getCreateForKhuyenMai(
			@RequestParam(value = "id", required = false,defaultValue = "0") int idDanhMuc,
			@RequestParam(value = "trang", required = false,defaultValue = "0") int trang,
			@RequestParam(value = "ten", required = false, defaultValue = "") String ten,
			@RequestParam(value = "task", required = false, defaultValue = "flash") String task
			){
		return new ResponseEntity<APIResponse>(new APIResponse(null, sanPhamService.getSanPhamChoKhuyenMai(idDanhMuc,ten,trang,task)),HttpStatus.OK);
	}
	
	@GetMapping("/getmanager")
	public ResponseEntity<APIResponse> getManager(
			@RequestParam(value = "id", required = false,defaultValue = "0") int idDanhMuc,
			@RequestParam(value = "trang", required = false,defaultValue = "0") int trang,
			@RequestParam(value = "ten", required = false, defaultValue = "") String ten,
			@RequestParam(value = "consudung", required = false, defaultValue = "true") Boolean conSuDung,
			@RequestParam(value = "hetHang", required = false, defaultValue = "0") int hetHang
			){
		return new ResponseEntity<APIResponse>(new APIResponse(null, sanPhamService.getManagerProduct(conSuDung, ten, hetHang, idDanhMuc, trang)),HttpStatus.OK);
	}
	@GetMapping("/thongkedoanhthucoban")
	public ResponseEntity<APIResponse> getManager(
			@RequestParam(value = "id", required = false,defaultValue = "0") long id,
			@RequestParam(value = "bd", required = false) LocalDateTime bd,
			@RequestParam(value = "kt", required = false) LocalDateTime kt
			){
		if(bd==null||kt==null) {
			throw new GeneralException("Vui lòng cung cấp thời gian thống kê",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<APIResponse>(
				new APIResponse(null, 
				sanPhamService.getThongKeSanPham(id, bd, kt))
				,HttpStatus.OK);
	}
	@GetMapping("/chitietdoanhthu")
	public ResponseEntity<APIResponse> getChiTietDoanhThu(
			@RequestParam(value = "id", required = false,defaultValue = "0") long id,
			@RequestParam(value = "bd", required = false) LocalDateTime bd,
			@RequestParam(value = "kt", required = false) LocalDateTime kt,
			@RequestParam(value = "kb", required = false,defaultValue = "0") int kieuBan,
			@RequestParam(value = "plc", required = false,defaultValue = "0") int phanLoaiChon
			){
		if(bd==null||kt==null) {
			throw new GeneralException("Vui lòng cung cấp thời gian thống kê",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<APIResponse>(
				new APIResponse(null, 
				sanPhamService.getChiTietDoanhThu(id, bd, kt,kieuBan,phanLoaiChon))
				,HttpStatus.OK);
	}
	
	@GetMapping("/luluongtruycap")
	public ResponseEntity<APIResponse> getChiLuuLuongTruyCap(
			@RequestParam(value = "id", required = false,defaultValue = "0") long id,
			@RequestParam(value = "bd", required = false) LocalDateTime bd,
			@RequestParam(value = "kt", required = false) LocalDateTime kt,
			@RequestParam(value = "timedurate", required = false,defaultValue = "0") int timeDuration,
			@RequestParam(value = "kb", required = false,defaultValue = "0") int kieuBan
			){
		if(bd==null||kt==null) {
			throw new GeneralException("Vui lòng cung cấp thời gian thống kê",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<APIResponse>(
				new APIResponse(null, 
				sanPhamService.getLuuLuongTruyCap(id, bd, kt,timeDuration,kieuBan))
				,HttpStatus.OK);
	}
	
	@GetMapping("getupdate/{id}")
	public ResponseEntity<APIResponse> getForUpdate(@PathVariable(value = "id", required = false) long id) {
		return new ResponseEntity<APIResponse>(
				new APIResponse(null, 
				sanPhamService.getSanPhamForUpdate(id))
				,HttpStatus.OK);
	}
	@GetMapping("/getproduct")
	public ResponseEntity<APIResponse> laySanPhamDonViCungCap(
			@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dvcc") String dvcc,
			@RequestParam(value = "sp") String sp
			
			){
		if(nkt.isBefore(nbd)) {
			throw new GeneralException("Thông tin ngày giờ không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getSanPhamCungCap(nbd.atStartOfDay(),nkt.atStartOfDay(),dvcc,sp,trang,false)),HttpStatus.OK);
		}
	}
	
	@PutMapping("/status/{id}")
    public ResponseEntity<APIResponse> status(@PathVariable(value="id") long id,  @RequestParam(value = "st") int st){
		sanPhamService.Status(id, st);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	@GetMapping("/export/lannhapkho")
    public ResponseEntity<InputStreamResource> exportToExcel(
    		@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dvcc") String dvcc,
			@RequestParam(value = "sp") String sp) throws IOException {
        

        ByteArrayInputStream in = ExportExcelService.exportToExcel(sanPhamService.getSanPhamCungCap(nbd.atStartOfDay(),nkt.atStartOfDay(),dvcc,sp,trang,true).getContent(),nbd,nkt);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=san_pham_phieu_nhap.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
	@GetMapping("/export/xuatTonKho")
    public ResponseEntity<InputStreamResource> exportToExcel(
    		@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dm") int dm,
			@RequestParam(value = "sp") String sp,
			@RequestParam(value = "status", required = false, defaultValue = "1 2 3 6 12") String status
			) throws IOException {
        

        ByteArrayInputStream in = ExportExcelService.exportToExcel(sanPhamService.getXuatTonKho(sp, dm, trang,nbd.atStartOfDay(),nkt.atStartOfDay(),true,status),nbd.atStartOfDay(),nkt.atStartOfDay());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=san_pham_phieu_nhap.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
	@GetMapping("/export/xuatloinhuan")
    public ResponseEntity<InputStreamResource> xuatLoiNhuan(
    		@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dm") int dm,
			@RequestParam(value = "sp") String sp,
			@RequestParam(value = "status", required = false, defaultValue = "1 2 3 6 12") String status
			
    		) throws IOException {
        

        ByteArrayInputStream in = ExportExcelService.exportToExcelLoiNhuan(sanPhamService.getXuatBanAndLoiNhuanBase(nbd.atStartOfDay(),nkt.atStartOfDay(), trang, sp,true,status),nbd.atStartOfDay(),nkt.atStartOfDay());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=san_pham_phieu_nhap.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
	
	@GetMapping("/xuatTonKho")
	public ResponseEntity<APIResponse> laySanPhamDonViCungCap(
			@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dm") int dm,
			@RequestParam(value = "sp") String sp,
			@RequestParam(value = "status", required = false, defaultValue = "1 2 3 6 12") String status
			
			){
		if(nkt.isBefore(nbd)) {
			throw new GeneralException("Thông tin ngày giờ không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getXuatTonKho(sp, dm, trang,nbd.atStartOfDay(),nkt.atStartOfDay(),false,status)),HttpStatus.OK);
		}
	}
	
	@GetMapping("/xuatloinhuan")
	public ResponseEntity<APIResponse> getXuatBanAndLoiNhuanBase(
			@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "dm") int dm,
			@RequestParam(value = "sp") String sp,
			@RequestParam(value = "status", required = false, defaultValue = "1 2 3 6 12") String status
			
			
			){
		if(nkt.isBefore(nbd)) {
			throw new GeneralException("Thông tin ngày giờ không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getXuatBanAndLoiNhuanBase(nbd.atStartOfDay(),nkt.atStartOfDay(), trang,sp,false,status)),HttpStatus.OK);
		}
	}
	
	@GetMapping("getloinhuansanpham/{id}")
	public ResponseEntity<APIResponse> getXuatBanAndLoiNhuanBase(
			@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@RequestParam(value = "sp") long idSP,
			@RequestParam(value = "status", required = false, defaultValue = "1 2 3 6 12") String status
			){
		if(nkt.isBefore(nbd)) {
			throw new GeneralException("Thông tin ngày giờ không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getLoiNhuanOfSanPham(idSP,nbd.atStartOfDay(), nkt.atStartOfDay(),status)),HttpStatus.OK);
		}
	}
	
	
	@PostMapping("/getviewproduct/{trang}")
	public ResponseEntity<APIResponse> getViewProduct(@RequestBody FilterViewProduct filterViewProduct, @PathVariable int trang){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getViewProduct(filterViewProduct, trang)),HttpStatus.OK);
	}
	
	@GetMapping("/getdetail/{id}")
	public ResponseEntity<APIResponse> getDetail(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getDetailProduct(id)),HttpStatus.OK);
	}
	
	@GetMapping("/getByMaVach")
	public ResponseEntity<APIResponse> getByMaVachOrten(@RequestParam("ten") String ten,
			@RequestParam(value = "lonhonkhong", required = false, defaultValue = "false") Boolean l){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTen(ten,l)),HttpStatus.OK);
	}
	
	@GetMapping("/getByMaVachForPhieuNhap")
	public ResponseEntity<APIResponse> getByMaVachOrtenForPhieuNhap(@RequestParam("ten") String ten,
			@RequestParam(value = "lonhonkhong", required = false, defaultValue = "false") Boolean l){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenForNhapHang(ten,l)),HttpStatus.OK);
	}
	
	@GetMapping("/getByMaVachForCreateHoaDon")
	public ResponseEntity<APIResponse> getByMaVachOrtenForCreateHoaDon(@RequestParam("ten") String ten,
			@RequestParam(value = "lonhonkhong", required = false, defaultValue = "false") Boolean l){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenForCreateHoaDon(ten,l)),HttpStatus.OK);
	}
	@PostMapping("/getSoLuongDatGioiHan")
	public ResponseEntity<APIResponse> getSoLuongDatGioiHan( @RequestBody List<Integer> ten)
	{
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getSoLuongGioiHanMua(ten)),HttpStatus.OK);
	}
	
	@GetMapping("/getByMaVachForPhieuKiem")
	public ResponseEntity<APIResponse> getByMaVachOrtenForPhieuKiem(@RequestParam("ten") String ten,
			@RequestParam(value = "lonhonkhong", required = false, defaultValue = "false") Boolean l){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenForPhieuKiem(ten,l)),HttpStatus.OK);
	} 
	@GetMapping("/getByMaVachForPhieuKiem/v2")
	public ResponseEntity<APIResponse> getByMaVachOrtenForPhieuKiemV2(
			@RequestParam("ten") String ten,
			@RequestParam(value = "lonhonkhong", 
			required = false, defaultValue = "false") Boolean l,
			@RequestParam(value = "long", defaultValue = "-1") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenForPhieuKiemV2(ten,l,id)),HttpStatus.OK);
	} 
	
	@GetMapping("/getByMaVachCorect")
	public ResponseEntity<APIResponse> getByMaVachOrtenCorect(@RequestParam("ten") String ten){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenCorect(ten)),HttpStatus.OK);
	} 
	@GetMapping("/getByMaVachCorectv2")
	public ResponseEntity<APIResponse> getByMaVachOrtenCorectv2(@RequestParam("ten") String ten){
		return new ResponseEntity<APIResponse>(new APIResponse("", sanPhamService.getByMaVachAndTenForNhapHangCorect(ten)),HttpStatus.OK);
	} 
	
}
