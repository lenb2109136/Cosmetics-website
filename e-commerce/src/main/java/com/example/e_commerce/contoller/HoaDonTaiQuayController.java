package com.example.e_commerce.contoller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.request.DTOCreateHoaDonTaiQuay;
import com.example.e_commerce.DTO.request.FilterOrderEmployee;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.HoaDonTaiQUayService;

@RestController
@RequestMapping(Routes.API+"/hoadontaiquay")
public class HoaDonTaiQuayController {
	
	@Autowired
	private HoaDonTaiQUayService hoaDonTaiQUayService;
	
	@PostMapping("create")
	public ResponseEntity<APIResponse> createOrderTaiQuay(@RequestBody DTOCreateHoaDonTaiQuay donTaiQuay){
		return  new ResponseEntity<APIResponse>(new APIResponse("", hoaDonTaiQUayService.DatHangOnline(donTaiQuay)),HttpStatus.OK);
	}
	
	@PutMapping("xacnhanthanhtoan/{id}")
	public ResponseEntity<APIResponse> xacNhanThanhToan(@PathVariable("id") long id) {
		hoaDonTaiQUayService.xacNhanDaThanhToan(id);
		return  new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	@GetMapping("chuyendoihinhthuc/{id}")
	public ResponseEntity<APIResponse> ChuyenDoiHinhThuc(@PathVariable("id") long id) {
		
		return  new ResponseEntity<APIResponse>(new APIResponse("", hoaDonTaiQUayService.chuyenDoiHinhThuc(id)),HttpStatus.OK);
	}
	@GetMapping("gettrangthai/{id}")
	public ResponseEntity<APIResponse> getTrangThai(@PathVariable("id") long id,
			@RequestParam("lan") int lan
			) {
		
		return  new ResponseEntity<APIResponse>(new APIResponse("", hoaDonTaiQUayService.getTrangThaiHoaDon(id, lan)),HttpStatus.OK);
	}
	@GetMapping("gethinhthuc/{id}")
	public ResponseEntity<APIResponse> getHinhThucThanhToan(@PathVariable("id") long id) {
		
		return  new ResponseEntity<APIResponse>(new APIResponse("", hoaDonTaiQUayService.getHinhThucThanhToan(id)),HttpStatus.OK);
	}
	
	@PutMapping("huydon/{id}")
	public ResponseEntity<APIResponse> huyDon(@PathVariable("id") long id) {
		hoaDonTaiQUayService.HuyDon(id);
		return  new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	@PutMapping("hoanDon/{id}")
	public ResponseEntity<APIResponse> hoanDon(
				@PathVariable("id") long id,
				@RequestParam(value = "lan", required = false, defaultValue = "0") int lan,
				@RequestParam(value = "re", required = false, defaultValue = "false") boolean re
			) {
		hoaDonTaiQUayService.hoanDonCheckTaiQuay(id, lan,re);
		return  new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	
	@GetMapping("/getbill/{id}")
    public ResponseEntity<byte[]> getBillPdf(@PathVariable Long id) {
        try {
            byte[] pdfContent = hoaDonTaiQUayService.renderBill(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill_thanh_toan_" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            // Trả về file PDF
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);

        } catch (Exception e) {
        	e.printStackTrace();
            throw new GeneralException("Xuất bill thất bại",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/getUpdate/{id}")
	public ResponseEntity<APIResponse> getUpDate(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonTaiQUayService.viewHoaDonAlReady(id)),HttpStatus.OK);
	}
    @PostMapping("/getprevpayment/{id}")
	public ResponseEntity<APIResponse> getPrevPayment(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonTaiQUayService.PrePaymentHoaDon(id)),HttpStatus.OK);
	}
    @PostMapping("/getbystatus")
    public ResponseEntity<APIResponse> getByStatus(
    		@RequestBody FilterOrderEmployee f
    		){
    	return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonTaiQUayService.filterHoaDonTaiQuay
    			(f.getMaHoaDon(), f.getKhachHang(), f.getTrangThai(), f.getNgayLap(),
    					f.getSort(), f.getTrang())),HttpStatus.OK);
    }
}
