package com.example.e_commerce.service;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MomoService {
	@Value("${momo.partner-code}")
	private String partnerCode;

	@Value("${momo.access-key}")
	private String accessKey;

	@Value("${momo.secret-key}")
	private String secretKey;

	@Value("${momo.endpoint}")
	private String endpoint;
	
	@Value("${momo.endpoint-query}")
	private String endpointQuery;
	

	@Value("${momo.endpoint-refund}")
	private String endpointRefund;

	@Value("${endpoint_server}")
	private String endpointServer;

	private final RestTemplate restTemplate = new RestTemplate();

	public String sign(String secretKey, String raw) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return new String(org.springframework.security.crypto.codec.Hex
				.encode(mac.doFinal(raw.getBytes(StandardCharsets.UTF_8))));
	}

	public Map<String, String> createOrderPayment(long amount, long id) throws Exception {
		String orderId = id + "";
		String requestId = orderId;
		System.out.println("số tiền thanh toán: "+amount);
		String orderInfo = "Thanh án";
		String redirectUrl = "https://webhook.site/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
		String ipnUrl = endpointServer;
		String requestType = "captureWallet";
		String extraData = "";
		String rawData = String.format(
				"accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s"
						+ "&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
				accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId,
				requestType);
		String signature = sign(secretKey, rawData);
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("partnerCode", partnerCode);
		requestBody.put("accessKey", accessKey);
		requestBody.put("requestId", requestId);
		requestBody.put("amount", String.valueOf(amount));
		requestBody.put("orderId", orderId);
		requestBody.put("orderInfo", orderInfo);
		requestBody.put("redirectUrl", redirectUrl);
		requestBody.put("ipnUrl", ipnUrl);
		requestBody.put("requestType", requestType);
		requestBody.put("extraData", extraData);
		requestBody.put("signature", signature);
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

		Map<String, Object> responseBody = response.getBody();
		if (responseBody == null || !"0".equals(String.valueOf(responseBody.get("resultCode")))) {
			throw new IllegalStateException("Tạo đơn thanh toán MoMo thất bại: " + responseBody);
		}
		
		OrderClassSchedule callBackIpnService=ServiceLocator.getBean(OrderClassSchedule.class);
		callBackIpnService.callBackIpn(id, 30);

		return Map.of("orderId", orderId, "payUrl", (String) responseBody.get("payUrl"), "qrCodeUrl",
				(String) responseBody.get("qrCodeUrl"));
	}

//	public Map<String, String> refundOrder(String orderIdGoc, long amount, String transId, String description)
//			throws Exception {
//
//		// 1. Tạo orderId và requestId mới để tránh bị lỗi duplicated
//		String timestamp = String.valueOf(System.currentTimeMillis());
//		String refundOrderId = orderIdGoc + "-refund-" + timestamp;
//		String requestId = "REFUND-" + refundOrderId;
//
//		// 2. Mô tả hoàn tiền
//		String noiDungHoanTien = (description != null && !description.isEmpty()) ? description
//				: "Hoàn tiền đơn hàng #" + orderIdGoc;
//
//		// 3. Tạo rawData
//		String rawData = String.format(
//				"accessKey=%s&amount=%d&description=%s&orderId=%s&partnerCode=%s&requestId=%s&transId=%s", accessKey,
//				amount, noiDungHoanTien, refundOrderId, partnerCode, requestId, transId);
//
//		// 4. Tạo chữ ký
//		String signature = sign(secretKey, rawData);
//
//		// 5. Tạo body yêu cầu hoàn tiền
//		Map<String, Object> requestBody = new HashMap<>();
//		requestBody.put("partnerCode", partnerCode);
//		requestBody.put("orderId", refundOrderId); // ✅ phải là mã mới
//		requestBody.put("requestId", requestId); // ✅ duy nhất
//		requestBody.put("amount", amount);
//		requestBody.put("transId", transId); // ✅ giao dịch gốc
//		requestBody.put("description", noiDungHoanTien);
//		requestBody.put("signature", signature);
//
//		// 6. Gửi yêu cầu HTTP
//		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//		System.out.println("Refund request body: " + requestBody); // Log để kiểm tra
//
//		ResponseEntity<Map> response = restTemplate.postForEntity(endpointRefund, entity, Map.class);
//		
//		Map<String, Object> responseBody = response.getBody();
//		
//		// 7. Kiểm tra phản hồi
//		if (responseBody == null || !"0".equals(String.valueOf(responseBody.get("resultCode")))) {
//			throw new IllegalStateException("Hoàn tiền MoMo thất bại: " + responseBody);
//		}
//		
//		// 8. Trả kết quả
//		return Map.of("orderId", refundOrderId, "requestId", requestId, "transId",
//				String.valueOf(responseBody.get("transId")), "resultCode",
//				String.valueOf(responseBody.get("resultCode")));
//	}
	public Map<String, String> refundOrder(String orderIdGoc, long amount, String transId, String description) {
	    // 1. Tạo orderId và requestId mới để tránh bị lỗi duplicated
	    String timestamp = String.valueOf(System.currentTimeMillis());
	    String refundOrderId = orderIdGoc + "-refund-" + timestamp;
	    String requestId = "REFUND-" + refundOrderId;
	    System.out.println("số tiền cần trả là: "+amount);
	    // 2. Mô tả hoàn tiền
	    String noiDungHoanTien = (description != null && !description.isEmpty()) 
	        ? description
	        : "Hoàn tiền đơn hàng #" + orderIdGoc;

	    // 3. Tạo rawData
	    String rawData = String.format(
	        "accessKey=%s&amount=%d&description=%s&orderId=%s&partnerCode=%s&requestId=%s&transId=%s",
	        accessKey, amount, noiDungHoanTien, refundOrderId, partnerCode, requestId, transId
	    );

	    // 4. Tạo chữ ký
	    String signature;
	    try {
	        signature = sign(secretKey, rawData);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new IllegalStateException("Không thể tạo chữ ký cho yêu cầu hoàn tiền: " + e.getMessage(), e);
	    }

	    // 5. Tạo body yêu cầu hoàn tiền
	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("partnerCode", partnerCode);
	    requestBody.put("orderId", refundOrderId);
	    requestBody.put("requestId", requestId);
	    requestBody.put("amount", amount);
	    requestBody.put("transId", transId);
	    requestBody.put("description", noiDungHoanTien);
	    requestBody.put("signature", signature);
	    System.out.println("ĐI QUA CÁC BƯỚC NÀY ");
	    // 6. Gửi yêu cầu HTTP
	    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	    ResponseEntity<Map> response=null;
	    
	    try {
	    	response= restTemplate.postForEntity(endpointRefund, entity, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    System.out.println(response);
	    Map<String, Object> responseBody = response.getBody();
	    
	    // 7. Kiểm tra phản hồi
	    System.out.println("hồi kết");
	    System.out.println(responseBody);
	    System.out.println(String.valueOf(responseBody.get("resultCode")));
	    if (responseBody == null || !"0".equals(String.valueOf(responseBody.get("resultCode")))) {
	        
	    	throw new IllegalStateException("Hoàn tiền MoMo thất bại: " + responseBody);
	    }
	    
	    // 8. Trả kết quả
	    return Map.of(
	        "orderId", refundOrderId,
	        "requestId", requestId,
	        "transId", String.valueOf(responseBody.get("transId")),
	        "resultCode", String.valueOf(responseBody.get("resultCode"))
	    );
	}
	public Map<String, String> queryTransactionStatus(String orderId) throws Exception {
	    // 1. Tạo requestId duy nhất
	    String timestamp = String.valueOf(System.currentTimeMillis());
	    String requestId = "QUERY-" + orderId + "-" + timestamp;

	    String rawData = String.format(
	            "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
	            accessKey, orderId, partnerCode, requestId
	    );

	    String signature;
	    try {
	        signature = sign(secretKey, rawData);
	    } catch (Exception e) {
	        throw new IllegalStateException("Không thể tạo chữ ký cho yêu cầu kiểm tra trạng thái: " + e.getMessage(), e);
	    }
	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("partnerCode", partnerCode);
	    requestBody.put("requestId", requestId);
	    requestBody.put("orderId", orderId);
	    requestBody.put("signature", signature);

	    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	    ResponseEntity<Map> response;
	    try {
	        response = restTemplate.postForEntity(endpointQuery, entity, Map.class);
	    } catch (Exception e) {
	        throw new IllegalStateException("Gửi yêu cầu kiểm tra trạng thái MoMo thất bại: " + e.getMessage(), e);
	    }

	    Map<String, Object> responseBody = response.getBody();
	    if (responseBody == null) {
	        throw new IllegalStateException("Kiểm tra trạng thái MoMo thất bại: Response body is null");
	    }

	    System.out.println("Query transaction response: " + responseBody);

	    Map<String, String> result = new HashMap<>();
	    result.put("orderId", String.valueOf(responseBody.get("orderId")));
	    result.put("requestId", String.valueOf(responseBody.get("requestId")));
	    result.put("transId", String.valueOf(responseBody.get("transId")));
	    result.put("resultCode", String.valueOf(responseBody.get("resultCode")));
	    result.put("message", String.valueOf(responseBody.get("message")));
	    result.put("amount", String.valueOf(responseBody.get("amount")));
	    result.put("orderInfo", String.valueOf(responseBody.get("orderInfo")));
	    result.put("orderType", String.valueOf(responseBody.get("orderType")));
	    result.put("transType", String.valueOf(responseBody.get("transType")));
	    result.put("payType", String.valueOf(responseBody.get("payType")));
	    result.put("responseTime", String.valueOf(responseBody.get("responseTime")));

	    return result;
	}

}
