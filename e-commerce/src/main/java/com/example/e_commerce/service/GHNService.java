package com.example.e_commerce.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cloudinary.api.exceptions.GeneralError;
import com.example.e_commerce.DTO.request.GHNOrder;
import com.example.e_commerce.DTO.response.GHNLog;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.ChiTietHoaDon;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GHNService {
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Value("${GHN_Token}")
    private String apiToken;
	
	
	@Value("${GHN_API_ORDER_DETAIL}")
	private String GHN_API_ORDER_DETAIL;
	
	@Value("${GHN_API_CANCELORDER}")
    private String GHN_API_CANCELORDER;
	
	@Value("${GHN_API_ORDER_CHANGE}")
    private String GHN_API_ORDER_CHANGE;
	
	
	@Value("${GHN_API_PRINTORDER}")
    private String GHN_API_PRINTORDER;
	

	@Value("${GHN_ShopID}")
    private String ShopId;
	
	@Value("${GHN_API_GETSERVICE}")
    private String GHN_API_GETSERVICE;
	
	
	@Value("${GHN_API_TinhPhi}")
    private String GHN_API_TinhPhi;
	
	@Value("${GHN_API_CREATEORDER}")
    private String GHN_API_CREATEORDER;
	
	
	
	private  RestTemplate restTemplate= new RestTemplate();
	
	public HttpHeaders getHeaders() {
		HttpHeaders headers= new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Token", apiToken);
		headers.set("ShopId", ShopId);
		return headers;
	}
	
	public Map<String, Object> tinhPhiGiaoHang(GHNOrder ghnOrder) {
		try {
			HttpEntity<GHNOrder> request = new HttpEntity<>(ghnOrder, getHeaders());
			ResponseEntity<Map<String, Object>> response =
				restTemplate.exchange(
					GHN_API_TinhPhi,
					HttpMethod.POST,
					request,
					new ParameterizedTypeReference<Map<String, Object>>() {});
			return response.getBody();
		} catch (HttpClientErrorException e) {
			try {
				ghnOrder.setService_id(5);
				HttpEntity<GHNOrder> retryRequest = new HttpEntity<>(ghnOrder, getHeaders());
				ResponseEntity<Map<String, Object>> retryResponse =
					restTemplate.exchange(
						GHN_API_TinhPhi,
						HttpMethod.POST,
						retryRequest,
						new ParameterizedTypeReference<Map<String, Object>>() {});
				return retryResponse.getBody();
			} catch (Exception retryException) {
				throw new GeneralException("Lấy thông tin phí ship thất bại sau khi thử lại với service_id = 5", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			throw new GeneralException("Server lỗi vui lòng kiểm tra lại", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Map<String, Object> CreateGHNOrder(GHNOrder ghnOrder) {
		Map<String, Object> result = new HashMap<>();
		try {
			HttpEntity<GHNOrder> request = new HttpEntity<>(ghnOrder, getHeaders());
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
				GHN_API_CREATEORDER,
				HttpMethod.POST,
				request,
				new ParameterizedTypeReference<Map<String, Object>>() {}
			);
			
			if (response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> responseData = response.getBody();
				if (responseData != null && responseData.containsKey("data")) {
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) responseData.get("data");
					
					
					result.put("maDonHang", data.get("order_code"));
					result.put("thoiGianGiaoHang", data.get("expected_delivery_time"));
					result.put("tongTienGiao", data.get("total_fee"));
					result.put("tienBaoHiem", data.get("insurance_fee"));
//					result.put("khoiLuong", data.get("weight"));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Map<String, Integer> getService(int fromDistrict, int toDistrict) throws GeneralException {
		Map<String, Integer> requestBody = new HashMap<>();
		requestBody.put("shop_id", Integer.parseInt(ShopId));
		requestBody.put("from_district", fromDistrict);
		requestBody.put("to_district", toDistrict);

		HttpEntity<Map<String, Integer>> request = new HttpEntity<>(requestBody, getHeaders());

		try {
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
					GHN_API_GETSERVICE,
				HttpMethod.POST,
				request,
				new ParameterizedTypeReference<Map<String, Object>>() {}
			);

			Map<String, Object> responseBody = response.getBody();
			if (responseBody == null || !responseBody.containsKey("data")) {
				throw new GeneralException("Không tìm thấy danh sách dịch vụ từ API GHN", HttpStatus.BAD_REQUEST);
			}

			List<Map<String, Object>> services = (List<Map<String, Object>>) responseBody.get("data");
			if (services == null || services.isEmpty()) {
				throw new GeneralException("Danh sách dịch vụ rỗng", HttpStatus.BAD_REQUEST);
			}

			Map<String, Integer> result = new HashMap<>();
			Map<String, Integer> fallbackService = null;

			for (Map<String, Object> service : services) {
				int serviceTypeId = ((Number) service.get("service_type_id")).intValue();
				int serviceId = ((Number) service.get("service_id")).intValue();

				if (serviceTypeId == 2) {
					result.put("s_id", serviceId);
					result.put("s_t_id", serviceTypeId);
					return result;
				}

				if (fallbackService == null || shouldReplaceFallback(fallbackService.get("s_t_id"), serviceTypeId)) {
					fallbackService = new HashMap<>();
					fallbackService.put("s_id", serviceId);
					fallbackService.put("s_t_id", serviceTypeId);
				}
			}

			if (fallbackService != null) {
				return fallbackService;
			}

			throw new GeneralException("Không tìm thấy dịch vụ phù hợp theo thứ tự ưu tiên", HttpStatus.BAD_REQUEST);
		} catch (HttpClientErrorException e) {
			throw new GeneralException("Lỗi khi gọi API GHN: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			throw new GeneralException("Lỗi server khi lấy dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean shouldReplaceFallback(Integer currentFallbackTypeId, int newTypeId) {
		if (currentFallbackTypeId == null) {
			return true;
		}
		if (currentFallbackTypeId == 1) {
			return false;
		}
		if (currentFallbackTypeId == 3 && newTypeId == 1) {
			return true;
		}
		if (currentFallbackTypeId == 5 && (newTypeId == 1 || newTypeId == 3)) {
			return true;
		}
		return false;
	}
	
	public String getPrintOrder(String orderCode) {
	    try {
	    	System.out.println("ĐI VÀO HÀM");
	        String url = GHN_API_PRINTORDER; 
	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("order_codes", Collections.singletonList(orderCode));
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Token", apiToken);
	        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            request,
	            new ParameterizedTypeReference<Map<String, Object>>() {}
	        );

	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            Map<String, Object> body = response.getBody();
	            if (body.containsKey("data")) {
	                Map<String, Object> data = (Map<String, Object>) body.get("data");
	                
	                return data.get("token").toString();
	            }
	            
	            throw new GeneralException("Không tìm thấy data trong response", HttpStatus.BAD_REQUEST);
	        }
	        throw new GeneralException("Yêu cầu thất bại: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    } catch (HttpClientErrorException e) {
	        e.printStackTrace();
	        throw new GeneralException("In phiếu thông tin thất bại: ", HttpStatus.BAD_REQUEST);
	    }
	}
	
	
	public boolean cancelOrder(String orderCode) {
	    try {
	        String url = GHN_API_CANCELORDER; 
	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("order_codes", Collections.singletonList(orderCode));

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Token", apiToken);
	        headers.set("ShopId", ShopId);

	        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            request,
	            new ParameterizedTypeReference<Map<String, Object>>() {}
	        );

	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            Map<String, Object> body = response.getBody();
	            System.out.println(body);
	            Object data = body.get("data");
	            if (data instanceof List<?> dataList && !dataList.isEmpty()) {
	                Map<?, ?> firstItem = (Map<?, ?>) dataList.get(0);
	                Boolean result = (Boolean) firstItem.get("result");
	                return result != null && result; 
	            }
	        }
	        throw new GeneralException("Hủy đơn hàng thất bại: Dữ liệu trả về không đúng định dạng", HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        throw new GeneralException("Hủy đơn hàng thất bại: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}
	
	public GHNOrder getOrderDetail(String orderCode) {
	    try {
	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("order_code", orderCode);

	        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, getHeaders());

	        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	            GHN_API_ORDER_DETAIL,
	            HttpMethod.POST,
	            request,
	            new ParameterizedTypeReference<Map<String, Object>>() {}
	        );

	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            Map<String, Object> body = response.getBody();
	            if (!body.containsKey("code") || !body.get("code").equals(200)) {
	                throw new GeneralException("Lấy thông tin đơn hàng thất bại: " + body.getOrDefault("message", "Không có thông tin lỗi"), HttpStatus.BAD_REQUEST);
	            }

	            if (!body.containsKey("data")) {
	                throw new GeneralException("Không tìm thấy trường 'data' trong phản hồi", HttpStatus.BAD_REQUEST);
	            }

	            Object data = body.get("data");
	            if (!(data instanceof Map)) {
	                throw new GeneralException("Dữ liệu đơn hàng có định dạng không hợp lệ, mong đợi một đối tượng JSON", HttpStatus.BAD_REQUEST);
	            }

	            Map<String, Object> orderData = (Map<String, Object>) data;
	            GHNOrder order = new GHNOrder();

	            Object status = orderData.get("status");
	            if (status instanceof String) {
	                order.setStatus((String) status);
	            } else {
	                throw new GeneralException("status không hợp lệ", HttpStatus.BAD_REQUEST);
	            }

	            return order;
	        }
	        throw new GeneralException("Yêu cầu thất bại: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    } catch (HttpClientErrorException e) {
	        throw new GeneralException("Lỗi khi gọi API GHN: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new GeneralException("Lỗi server khi lấy thông tin đơn hàng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	 public void ChangeInfo(Map<String, Object> requestBody) {
	        try {
	            String url = GHN_API_ORDER_CHANGE;
	            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, getHeaders());
	            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                request,
	                new ParameterizedTypeReference<Map<String, Object>>() {}
	            );

	            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	                Map<String, Object> body = response.getBody();
	                if (body.containsKey("code") && body.get("code").equals(200)) {
	                    return;
	                }
	                throw new GeneralException("Thay đổi thông tin thất bại, vui lòng thử lại", HttpStatus.BAD_REQUEST);
	            }
	            throw new GeneralException("Thay đổi thông tin thất bại, vui lòng thử lại", HttpStatus.BAD_REQUEST);
	        } catch (HttpClientErrorException e) {
	            throw new GeneralException("Thay đổi thông tin thất bại, vui lòng thử lại: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	        } catch (Exception e) {
	            throw new GeneralException("Lỗi server khi thay đổi thông tin: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
}
