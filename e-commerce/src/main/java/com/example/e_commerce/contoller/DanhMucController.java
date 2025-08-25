package com.example.e_commerce.contoller;

import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.request.DanhMucCreate;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.DanhMucSerVice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController()
@RequestMapping(Routes.API+ "/danhmuc")
@Validated
public class DanhMucController {
	@Autowired
	private DanhMucSerVice danhMucSerVice;
	@GetMapping
	public ResponseEntity<APIResponse> getDanhMuc(){
		return new ResponseEntity<APIResponse>(new APIResponse("ok", danhMucSerVice.findAll()),HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<APIResponse> saveDanhMuc(@Valid @RequestBody DanhMucCreate danhMucCreate) {
		danhMucSerVice.save(danhMucCreate);
		return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<APIResponse> updateDanhMuc(@Valid @RequestBody DanhMucCreate danhMucCreate) {
		danhMucSerVice.update(danhMucCreate);
		return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
	}
	
	@GetMapping("/getmanager/{trang}")
	public ResponseEntity<APIResponse> getManager(@PathVariable("trang") @Min(value = 0,message = "Vị trí trang không được nhỏ hơn 0") int trang,
			@RequestParam(value = "thamso",required = false, defaultValue = "") String thamso){
		return new ResponseEntity<APIResponse>(new APIResponse(null, danhMucSerVice.getDanhMucManager(trang, thamso)),HttpStatus.OK);
	}
	
	@GetMapping("/getsanphambase/{id}")
	public ResponseEntity<APIResponse> getManager(@PathVariable("id") @Min(value = 0,message = "Thông tin danh mục không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, danhMucSerVice.getAllSanPhamInDanhMuc(id)),HttpStatus.OK);
	}
	
	@GetMapping("/getthonsoofdanhmuc/{id}")
	public ResponseEntity<APIResponse> getThongSoCuTheOfThongSo(
	@NotNull(message = "Vui lòng cung cấp thông tin danh mục") 
    @Min(value = 0, message = "Không tìm thấy danh mục cần kiểm") 
    @PathVariable("id") Integer id){
		return new ResponseEntity<APIResponse>(new APIResponse("", danhMucSerVice.getThongSoCuTheOfThongSo(id)),HttpStatus.OK);
	}
	
	@GetMapping("/getupdate/{id}")
	public ResponseEntity<APIResponse> getupdate(@PathVariable("id") @Min(value = 0,message = "Thông tin danh mục không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, danhMucSerVice.getUpdate(id)),HttpStatus.OK);
	}
	
	
	
	@GetMapping("/test/{id}")
	public ResponseEntity<APIResponse> test(@PathVariable("id")  int id){
		if(id==0) {
			return new ResponseEntity<APIResponse>(new APIResponse(null, false),HttpStatus.OK);
		}
		return new ResponseEntity<APIResponse>(new APIResponse(null, danhMucSerVice.test(id)),HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<APIResponse> delete(@RequestParam("id")  int id, 
			@RequestParam(value = "idt", required = false, defaultValue = "0") int idt){
		if(id==0) {
			return new ResponseEntity<APIResponse>(new APIResponse(null, false),HttpStatus.OK);
		}
		danhMucSerVice.Huy(id, idt);
		return new ResponseEntity<APIResponse>(new APIResponse(null, null),HttpStatus.OK);
	}
	
	
}
