package com.example.e_commerce.service;

import com.example.e_commerce.DTO.response.ZaloPayOrder;
import com.example.e_commerce.DTO.response.ZaloPayResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ZaloPayService {
	@Value("${app_id_ZALOPAY}")
	private String appID;

	@Value("${key1_ZALOPAY}")
	private String key1_ZALOPAY;

	@Value("${key2_ZALOPAY}")
	private String key2_ZALOPAY;

	@Value("${endpoint_ZALOPAY_CREATE}")
	private String endpoint_ZALOPAY_CREATE;

	@Value("${QUERY_ZALOPAY}")
	private String endpoint_QUERY_ZALOPAY;

	@Value("${endpoint_ZALOPAY_REFUND}")
	private String endpoint_ZALOPAY_REFUND;
	
	@Value("${QUERY_ZALOPAY_REFUND}")
	private String QUERY_ZALOPAY_REFUND;
	

	private static final String HMAC_ALGORITHM = "HmacSHA256";

	public ZaloPayOrder createOrder(String appUser, long amount, String description, String internalOrderId)
			throws Exception {
		String appTransId = generateAppTransId();
		long appTime = System.currentTimeMillis();

		Map<String, String> params = new HashMap<>();
		params.put("app_id", appID);
		params.put("app_user", appUser);
		params.put("app_trans_id", appTransId);
		params.put("app_time", String.valueOf(appTime));
		params.put("amount", String.valueOf(amount));
		params.put("item", "[]");
		params.put("description", description);
		params.put("embed_data", "{}");
		params.put("bank_code", "zalopayapp");

		String dataToSign = params.get("app_id") + "|" + params.get("app_trans_id") + "|" + params.get("app_user") + "|"
				+ params.get("amount") + "|" + params.get("app_time") + "|" + params.get("embed_data") + "|"
				+ params.get("item");
		String mac = generateHmac(dataToSign, key1_ZALOPAY);
		params.put("mac", mac);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			formData.add(entry.getKey(), entry.getValue());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

		RestTemplate restTemplate = new RestTemplate();
		String cleanEndpoint = endpoint_ZALOPAY_CREATE.trim().replaceAll("[,\\s]+$", "");
		String responseBody = restTemplate.postForObject(cleanEndpoint, request, String.class);

		ObjectMapper mapper = new ObjectMapper();
		ZaloPayResponseDTO responseDTO = mapper.readValue(responseBody, ZaloPayResponseDTO.class);

		return new ZaloPayOrder(appTransId,
				responseDTO.getZp_trans_token() != null ? responseDTO.getZp_trans_token() : "",
				responseDTO.getOrder_token() != null ? responseDTO.getOrder_token() : "",
				responseDTO.getReturn_code() != null ? responseDTO.getReturn_code() : 0,
				responseDTO.getSub_return_code() != null ? responseDTO.getSub_return_code() : 0, appTime, amount,
				description, appUser, internalOrderId, LocalDateTime.now(),
				responseDTO.getReturn_code() != null && responseDTO.getReturn_code().equals(1) ? "PENDING" : "FAILED",
				responseDTO.getOrder_url() != null ? responseDTO.getOrder_url() : "");
	}

	private String generateAppTransId() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String datePart = dateFormat.format(new Date());
		String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
		String appTransId = datePart + "_" + randomPart + "_skinly";
		if (appTransId.length() > 40) {
			appTransId = appTransId.substring(0, 40);
		}
		return appTransId;
	}

	private String generateHmac(String data, String key) throws Exception {
		Mac mac = Mac.getInstance(HMAC_ALGORITHM);
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
		mac.init(secretKey);
		byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		StringBuilder hexString = new StringBuilder();
		for (byte b : hmacBytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public Map<String, Object> queryOrder(String appTransId) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("app_id", appID);
		params.put("app_trans_id", appTransId);

		String dataToSign = params.get("app_id") + "|" + params.get("app_trans_id") + "|" + key1_ZALOPAY;
		String mac = generateHmac(dataToSign, key1_ZALOPAY);
		params.put("mac", mac);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			formData.add(entry.getKey(), entry.getValue());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

		RestTemplate restTemplate = new RestTemplate();
		String cleanEndpoint = endpoint_QUERY_ZALOPAY.trim().replaceAll("[,\\s]+$", "");
		String responseBody = restTemplate.postForObject(cleanEndpoint, request, String.class);

		if (responseBody == null) {
			throw new Exception("Empty response from ZaloPay API");
		}

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);

		return responseMap;
	}

	public Map<String, Object> refund(String zpTransId, long amount, String description, String mRefundId,
            Long refundFeeAmount) throws Exception {
        long timestamp;
        int maxRetries = 7;
        int retryCount = 0;

        while (true) {
            // If this is the first attempt, call the refund API
            if (retryCount == 0) {
                timestamp = System.currentTimeMillis();

                Map<String, String> params = new HashMap<>();
                params.put("app_id", String.valueOf(appID));
                params.put("zp_trans_id", zpTransId);
                params.put("amount", String.valueOf(amount));
                params.put("description", description);
                params.put("m_refund_id", mRefundId);
                params.put("timestamp", String.valueOf(timestamp));

                if (refundFeeAmount != null) {
                    params.put("refund_fee_amount", String.valueOf(refundFeeAmount));
                }

                String dataToSign;
                if (refundFeeAmount == null) {
                    dataToSign = appID + "|" + zpTransId + "|" + amount + "|" + description + "|" + timestamp;
                } else {
                    dataToSign = appID + "|" + zpTransId + "|" + amount + "|" + refundFeeAmount + "|" + description + "|" + timestamp;
                }

                String mac = generateHmac(dataToSign, key1_ZALOPAY);
                params.put("mac", mac);

                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formData.add(entry.getKey(), entry.getValue());
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

                RestTemplate restTemplate = new RestTemplate();
                String cleanEndpoint = endpoint_ZALOPAY_REFUND.trim().replaceAll("[,\\s]+$", "");

                String responseBody = restTemplate.postForObject(cleanEndpoint, request, String.class);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);

                System.out.println("Refund API response: " + responseMap);

                int returnCode = (int) responseMap.get("return_code");

                if (returnCode == 1) {
                    return responseMap; // REFUND_SUCCESS
                } else if (returnCode == 2) {
                    throw new Exception("Refund failed with code 2: " + responseMap); // REFUND_FAIL
                } else if (returnCode == 3) {
                    retryCount++;
                    System.out.println("Processing refund... attempt " + (retryCount + 1) + "/" + maxRetries);
                    Thread.sleep(1000);
                } else {
                    throw new Exception("Refund failed with unexpected code: " + responseMap);
                }
            } else {
                // For subsequent retries, call queryRefund instead
                Map<String, Object> responseMap = queryRefund(mRefundId);
                System.out.println("Query Refund API response: " + responseMap);

                int returnCode = (int) responseMap.get("return_code");

                if (returnCode == 1) {
                    return responseMap; // REFUND_SUCCESS
                } else if (returnCode == 2) {
                    throw new Exception("Refund failed with code 2: " + responseMap); // REFUND_FAIL
                } else if (returnCode == 3) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw new Exception("Refund still processing after " + maxRetries + " retries: " + responseMap);
                    }
                    System.out.println("Retrying query refund... attempt " + (retryCount + 1) + "/" + maxRetries);
                    Thread.sleep(1000);
                } else {
                    throw new Exception("Query refund failed with unexpected code: " + responseMap);
                }
            }
        }
    }
	
	public Map<String, Object> queryRefund(String mRefundId) throws Exception {
        long timestamp = System.currentTimeMillis();

        Map<String, String> params = new HashMap<>();
        params.put("app_id", appID);
        params.put("m_refund_id", mRefundId);
        params.put("timestamp", String.valueOf(timestamp));

        String dataToSign = appID + "|" + mRefundId + "|" + timestamp;
        String mac = generateHmac(dataToSign, key1_ZALOPAY);
        params.put("mac", mac);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formData.add(entry.getKey(), entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        RestTemplate restTemplate = new RestTemplate();
        String endpoint = QUERY_ZALOPAY_REFUND.trim();
        String responseBody = restTemplate.postForObject(endpoint, request, String.class);

        if (responseBody == null) {
            throw new Exception("Empty response from ZaloPay API");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);

        return responseMap;
    }

}