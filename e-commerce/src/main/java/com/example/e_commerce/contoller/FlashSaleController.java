package com.example.e_commerce.contoller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Api;
import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.BienTheFlashSaleService;
import com.example.e_commerce.service.FlashSaleService;
import com.example.e_commerce.service.ServiceLocator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(Routes.API+"/flashsale")
@Validated
public class FlashSaleController {
	@Autowired
	private FlashSaleService flashSaleService;
	@PostMapping
	private ResponseEntity<APIResponse> save(@RequestBody @Valid FlashSaleDTO flash) {
		flash.validate();
		FlashSaleService c=ServiceLocator.getBean(FlashSaleService.class);
		c.saveFromDTO(flash);
		return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
	}
	@GetMapping("/getbientheofflash/{id}")
	public ResponseEntity<APIResponse> getbientheofflash(@PathVariable("id")@NotNull(message = "Thông tin flash sale cung cấp không hợp lệ")
	@Min(value = 1,message = "Thông tin flash sale cung cấp không hợp lệ") Long id) {
		List<Integer> idBienThe=flashSaleService.getById(id).getBienTheFlashSale().stream().map(data-> data.getBienThe().getId()).collect(Collectors.toList());
		return new ResponseEntity<APIResponse>(new APIResponse(null,idBienThe),HttpStatus.OK);
	}
	@GetMapping("/getflashbyid/{id}")
	public ResponseEntity<APIResponse> getFlashSalebyId(@PathVariable("id")@NotNull(message = "Thông tin flash sale cung cấp không hợp lệ")
	@Min(value = 1,message = "Thông tin flash sale cung cấp không hợp lệ") Long id) {
		return new ResponseEntity<APIResponse>(new APIResponse(null,flashSaleService.getFlashSaleById(id)),HttpStatus.OK);
	}
	@PostMapping("/update/{id}")
	public void update(@RequestBody FlashSaleDTO f,@PathVariable("id") long id) {
		f.ValidateForUpdate(id);
		flashSaleService.UpDate(f, id);
	}
	
	@GetMapping("/getbystatus/{status}")
	public ResponseEntity<APIResponse> getByStatus(
			@PathVariable @NotNull(message = "Vui lòng cung cấp trạng thái Flashsale" )@Min(value = 0, message = "Thông tin trạng thái cung cấp không hợp lệ") Integer status,
			@RequestParam @NotNull(message = "Vui lòng cung cấp thời gian bắt đầu lọc") LocalDateTime bd,
			@RequestParam @NotNull(message = "Vui lòng cung cấp thời gian kết thúc lọc") LocalDateTime kt,
			@RequestParam @NotNull(message = "Vui lòng cung cấp trang cần lọc") @Min(value = 0, message = "Thông tin trang cung cấp không hợp lệ") Integer trang
			) {
		if(!bd.isBefore(kt)) {
			throw new GeneralException("Thời gian kết thúc phải sau thười gian bắt đầu",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<APIResponse>(new APIResponse(null,flashSaleService.getByStatus(trang, bd, kt, status)),HttpStatus.OK);
	}
	@GetMapping("active/{id}")
	public ResponseEntity<APIResponse> setActiveFlashSale(
			@PathVariable("id")@NotNull(message = "Thông tin flash sale cung cấp không hợp lệ")
			@Min(value = 1,message = "Thông tin flash sale cung cấp không hợp lệ") Long id,
			@RequestParam("active") @NotNull(message = "Vui lòng cung cấp hoạt động của flashsale") Boolean active){
		flashSaleService.setActiveFlashSale(id, active);
		return new ResponseEntity<APIResponse>(new APIResponse(null, null),HttpStatus.OK);
	}
	@GetMapping("/thongke/{id}")
	public ResponseEntity<APIResponse> setActiveFlashSale(
			@PathVariable("id")@NotNull(message = "Thông tin flash sale cung cấp không hợp lệ")
			@Min(value = 1,message = "Thông tin flash sale cung cấp không hợp lệ") Long id){
		Map<String,Object> map= new HashMap<String, Object>();
		map=flashSaleService.getThongTinThongKe(id);
		return new ResponseEntity<APIResponse>(new APIResponse(null, map),HttpStatus.OK);
	}
}
