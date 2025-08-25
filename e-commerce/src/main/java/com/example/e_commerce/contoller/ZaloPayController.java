package com.example.e_commerce.contoller;

import com.example.e_commerce.DTO.request.GHNOrder;
import com.example.e_commerce.DTO.response.ZaloPayOrder;
import com.example.e_commerce.service.GHNService;
import com.example.e_commerce.service.ZaloPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZaloPayController {
    @Autowired
    private ZaloPayService zaloPayService;
    
    @Autowired
    private GHNService ghnServiceL;

    @PostMapping("/zalopay/create-order")
    public ResponseEntity<ZaloPayOrder> createOrder(
            @RequestParam String appUser,
            @RequestParam long amount,
            @RequestParam String description,
            @RequestParam String internalOrderId) throws Exception {
        ZaloPayOrder order = zaloPayService.createOrder(appUser, amount, description, internalOrderId);
        return ResponseEntity.ok(order);
    }
//    @GetMapping("/zalopay/query-order")
//    public ResponseEntity<Integer> queryOrder(@RequestParam String appTransId) throws Exception {
//        Integer returnCode = zaloPayService.queryOrder(appTransId);
//        return ResponseEntity.ok(returnCode);
//    }
    @GetMapping("/zalopay/refund")
    public Object refund(
            @RequestParam String zpTransId,
            @RequestParam long amount,
            @RequestParam String description,
            @RequestParam String mRefundId) throws Exception {
       
        return zaloPayService.refund(zpTransId, amount, description, mRefundId,null);
    }
    
    
    
}