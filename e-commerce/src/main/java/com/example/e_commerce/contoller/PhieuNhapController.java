package com.example.e_commerce.contoller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.DTO.response.PhieuNhapDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.BienTheSerVice;
import com.example.e_commerce.service.NguoiDungService;
import com.example.e_commerce.service.PhieuNhapService;

@RequestMapping(Routes.API+"/phieunhap")
@RestController
public class PhieuNhapController {
	@Autowired
	private PhieuNhapService phieuNhapService;
	
	
	@GetMapping("getphieunhap/{id}")
	public ResponseEntity<APIResponse> getPhieuNhapByDVCC(
			@RequestParam("nbd") LocalDate nbd, 
			@RequestParam("nkt") LocalDate nkt,
			@PathVariable("id") Long id,
			@RequestParam("trang") int trang
			){
		if(nkt.isBefore(nkt)||trang<0) {
			throw new GeneralException("Thông tin yêu cầu không hợp lệ",null);
		}
		return new ResponseEntity<APIResponse>(new APIResponse(null,phieuNhapService.getPhieuNhapByTimeAndID(nbd.atStartOfDay(), nkt.atStartOfDay(),id,trang)),HttpStatus.OK);
	}
	@GetMapping("getphieunhapbyten")
	public ResponseEntity<APIResponse> getPhieuNhapByDVCC(
			@RequestParam("nbd") LocalDate nbd, 
			@RequestParam("nkt") LocalDate nkt,
			@RequestParam(value = "dvcc",defaultValue = "") String tenDonViCungCap,
			@RequestParam(value ="sp",defaultValue = "") String tenSanPham,
			@RequestParam("trang") int trang
			){
		if(nkt.isBefore(nkt)||trang<0) {
			throw new GeneralException("Thông tin yêu cầu không hợp lệ",null);
		}
		return new ResponseEntity<APIResponse>(new APIResponse(null,phieuNhapService.getPhieuNhapByTimeAndTen(nbd.atStartOfDay(), nkt.atStartOfDay(), tenDonViCungCap, tenSanPham, trang)),HttpStatus.OK);
	}
	@GetMapping("getChitiet/{id}")
	public ResponseEntity<APIResponse> getChiTiet(@PathVariable("id") Long id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, phieuNhapService.getChiTiet(id)),HttpStatus.OK);
	}
	
	@PostMapping("save")
	public ResponseEntity<APIResponse> save(@RequestBody PhieuNhapDTO phieuNhapDTO){
		System.out.println("nhận được thông tin");
		phieuNhapService.savePhieuNhap(phieuNhapDTO);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	@PostMapping("savev2")
	public ResponseEntity<APIResponse> saveV2(@RequestBody PhieuNhapDTO phieuNhapDTO){
		phieuNhapService.savePhieuNhapV2(phieuNhapDTO);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	
	@GetMapping("get-update")
	public ResponseEntity<APIResponse> getUpdate(@RequestParam("id") long id ) {
		return new ResponseEntity<APIResponse>(new APIResponse("", phieuNhapService.getUpdate(id)),HttpStatus.OK);
	}
	@PutMapping("update/{id}")
	public ResponseEntity<APIResponse> getUpdate(@PathVariable("id") long id, @RequestBody PhieuNhapDTO phieuNhapDTO) {
	    return new ResponseEntity<>(new APIResponse("", phieuNhapService.Update(phieuNhapDTO, id)), HttpStatus.OK);
	}
	@PutMapping("duyet/{id}")
	public ResponseEntity<APIResponse> duyet(@PathVariable("id") long id) {
		 phieuNhapService.duyet(id);
	    return new ResponseEntity<>(new APIResponse("",null), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<APIResponse> huyDietPhieuNhap(@PathVariable("id") long id) {
		 phieuNhapService.deletePhieuNhap(id);
	    return new ResponseEntity<>(new APIResponse("",null), HttpStatus.OK);
	}

}
