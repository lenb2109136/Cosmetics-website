package com.example.e_commerce.contoller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.request.NhaCungCapDTO;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.DonViCungCap;
import com.example.e_commerce.service.DonGiaBanHangService;
import com.example.e_commerce.service.DonViCungCapService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(Routes.API+ "/donvicungcap")
@Validated
public class DonViCungCapController {
	@Autowired
	private DonViCungCapService donViCungCapService;
	
	@PostMapping
	public ResponseEntity<APIResponse> save(@RequestBody @Valid NhaCungCapDTO nhaCungCapDTO){
		donViCungCapService.save(nhaCungCapDTO);
		
		return new ResponseEntity<APIResponse>(HttpStatus.OK);
	}
	
	@GetMapping("/getManager")
	public ResponseEntity<APIResponse> getManagerDonViCungCap(
	    @NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
	    @RequestParam(value = "bd") LocalDate bd,

	    @NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
	    @RequestParam(value = "kt") LocalDate kt,

	    @Min(value = 0, message = "Thông tin trang chưa hợp lệ") 
	    @RequestParam(value = "trang") int trang,

	    @RequestParam(value = "ten", required = false, defaultValue = "") String ten
	) {
	    if (bd.isAfter(kt)) {
	        throw new GeneralException("Thông tin thời gian không phù hợp", null);
	    }
	    return new ResponseEntity<APIResponse>(new APIResponse("", donViCungCapService.getManagerSupplier(ten, trang, bd, kt)),HttpStatus.OK);
	}
	
	@GetMapping("getproduct/{id}")
	public ResponseEntity<APIResponse> laySanPhamDonViCungCap(
			@NotNull(message = "Vui lòng cung cấp thời điểm bắt đầu") 
			@RequestParam(value = "bd") LocalDate nbd,
			@RequestParam(value = "trang") int trang,
			@NotNull(message = "Vui lòng cung cấp thời điểm kết thúc") 
			@RequestParam(value = "kt") LocalDate nkt,
			@Min(value = 1,message = "Thông tin yêu cầu không hợp lệ")@PathVariable("id") Long id
			){
		if(nkt.isBefore(nbd)) {
			throw new GeneralException("Thông tin ngày giờ không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<APIResponse>(new APIResponse("", donViCungCapService.getSanPhamCungCap(nbd.atStartOfDay(),nkt.atStartOfDay(),id,trang)),HttpStatus.OK);
		}
	}
	@GetMapping("/getall")
	public ResponseEntity<APIResponse> getAll(){
		return new ResponseEntity<APIResponse>(new APIResponse("", donViCungCapService.donViCungCaps()),HttpStatus.OK);
	}
	
	@GetMapping("/getupdate/{id}")
	public ResponseEntity<APIResponse> getUpdate(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("", donViCungCapService.getInfo(id)),HttpStatus.OK);
	}
}
