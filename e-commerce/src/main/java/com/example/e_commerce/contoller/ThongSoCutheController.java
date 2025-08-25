package com.example.e_commerce.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.ThongSoCuTheService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
@RequestMapping(Routes.API + "/thongsocuthe")
public class ThongSoCutheController {
	@Autowired
	private ThongSoCuTheService thongSoCuTheService;
	
	@DeleteMapping("/removethongsocuthe/{id}")
	public ResponseEntity<APIResponse> removeThongSoCuThe(
		@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
		@Min(value = 0, message = "Thông số trên không tồn tại") @PathVariable("id") Long id) {
		thongSoCuTheService.delete(id);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
}
