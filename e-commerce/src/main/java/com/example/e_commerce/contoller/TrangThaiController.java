package com.example.e_commerce.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.TrangThaiService;

@RestController
@RequestMapping(Routes.API+ "/trangthai")
public class TrangThaiController {
	
	@Autowired
	private TrangThaiService trangThaiService;
	
	@GetMapping
	public ResponseEntity<APIResponse> getAll() {
		return new ResponseEntity<APIResponse>(new APIResponse("", trangThaiService.getAllTrangThai()),HttpStatus.OK);
	}
}
