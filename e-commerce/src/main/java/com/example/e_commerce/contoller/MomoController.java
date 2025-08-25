package com.example.e_commerce.contoller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.service.HoaDonSerVice;
import com.example.e_commerce.service.HoaDonTaiQUayService;
import com.example.e_commerce.service.MomoService;

@RestController
@RequestMapping(Routes.API+ "/momo")
public class MomoController {
	@Autowired
	private MomoService momoService;
	
	@Autowired
	private HoaDonSerVice hoaDonSerVice;
	
	@Autowired
	private HoaDonTaiQUayService hoaDonTaiQUayService;
	
	@GetMapping("/hoanhang")
	public Map<String, String> get() throws Exception {
		return momoService.refundOrder("76"+"SKINLY", 200000, "4533699960", "Khách yêu cầu trả hàng");

	}
//	@PostMapping("/listen-result-payment")
//	public ResponseEntity<Void> langNgheThanhToan(@RequestBody MomoIpnRequest resultPayment) throws Exception {
//	    System.out.println("có gọi");
//	    int code = resultPayment.getResultCode();
//	    String orderId = resultPayment.getOrderId();
//	    long transId = resultPayment.getTransId();
//
//	    long orderIdLong = Long.parseLong(orderId);
////	    hoaDonTaiQUayService.listenMomo(orderIdLong, transId, code);
//	    hoaDonSerVice.listenMomo(orderIdLong, transId, code);
//	    return ResponseEntity.noContent().build();
//	}
	
	
	
	
	
}
