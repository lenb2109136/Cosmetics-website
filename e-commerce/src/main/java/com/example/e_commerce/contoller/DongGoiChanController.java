package com.example.e_commerce.contoller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.DongGoiChan;
import com.example.e_commerce.repository.DongGoiChanRepository;
import com.example.e_commerce.service.QuyCachNhapHangService;

@RestController
@RequestMapping(Routes.API + "/donggoichan")
public class DongGoiChanController {
	@Autowired
	private QuyCachNhapHangService quyCachNhapHangService;
	
	@Autowired
	private DongGoiChanRepository dongGoiChanRepository;

	@GetMapping("getall")
	public ResponseEntity<APIResponse> getAll() {
	    return new ResponseEntity<>(
	        new APIResponse(
	            "",
	            quyCachNhapHangService.getAllDongGoi().stream().map(d -> {
	                Map<String, Object> m = new HashMap<>();
	                m.put("id", d.getId());
	                m.put("tenQuyCach", d.getTenQuyCach());
	                m.put("duongDan", d.getDuongDan());
	                m.put("canUpdate", dongGoiChanRepository.countNhap(d.getId()) <= 0);

	                return m;
	            }).collect(Collectors.toList())
	        ),
	        HttpStatus.OK
	    );
	}


	@PostMapping("save")
	public ResponseEntity<APIResponse> save(@RequestParam("ten") String ten,
			@RequestParam(value = "anhMoTa", required = false) MultipartFile anh) {
		quyCachNhapHangService.save(ten, anh);
		return new ResponseEntity<APIResponse>(new APIResponse("", null), HttpStatus.OK);

	}
	@PutMapping()
	public ResponseEntity<APIResponse> update(@RequestParam("id") int id,@RequestParam("ten") String ten,
			@RequestParam(value = "anhMoTa", required = false) MultipartFile anh) {
		quyCachNhapHangService.update(id,ten, anh);
		return new ResponseEntity<APIResponse>(new APIResponse("", null), HttpStatus.OK);

	}
}
