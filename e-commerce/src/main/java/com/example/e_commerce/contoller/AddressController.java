package com.example.e_commerce.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.service.AddressService;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/address")
public class AddressController {
	@Autowired
	private AddressService addressService;
	@GetMapping("/tinh")
	public ResponseEntity<APIResponse> getTinh(){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getTinh()),HttpStatus.OK);
	}
	@GetMapping("/huyen/{id}")
	public ResponseEntity<APIResponse> getHuyen(@PathVariable("id") @Min(value = 1,message = "Thông tin địa chỉ không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getHuyen(id)),HttpStatus.OK);
	}
	@GetMapping("/xa/{id}")
	public ResponseEntity<APIResponse> getXa(@PathVariable("id") @Min(value = 1,message = "Thông tin địa chỉ không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getXa(id)),HttpStatus.OK);
	}
	@GetMapping("/tinh-GHN")
	public ResponseEntity<APIResponse> getTinhGHN(){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getTinhGHN()),HttpStatus.OK);
	}
	@GetMapping("/huyen-GHN/{id}")
	public ResponseEntity<APIResponse> getHuyenGHN(@PathVariable("id") @Min(value = 1,message = "Thông tin địa chỉ không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getHuyenGHN(id)),HttpStatus.OK);
	}
	@GetMapping("/xa-GHN/{id}")
	public ResponseEntity<APIResponse> getXaGHN(@PathVariable("id") @Min(value = 1,message = "Thông tin địa chỉ không hợp lệ") int id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, addressService.getXaGHN(id)),HttpStatus.OK);
	}
}
