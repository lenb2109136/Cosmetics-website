package com.example.e_commerce.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.e_commerce.DTO.response.AdressDTO;
import com.example.e_commerce.constants.Routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class AddressService {

    @Value("${GHN_Token}")
    private String ghnToken;

    private final RestTemplate restTemplate = new RestTemplate();


    public List<AdressDTO> getTinh() {
        ResponseEntity<List<AdressDTO>> response = restTemplate.exchange(
                Routes.tinh,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public List<AdressDTO> getHuyen(int id) {
        String url = Routes.huyen + id + "?depth=2";
        ResponseEntity<AdressDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        AdressDTO result = response.getBody();
        return result != null ? result.getDistricts() : Collections.emptyList();
    }

    public List<AdressDTO> getXa(int id) {
        String url = Routes.xa + id + "?depth=2";
        ResponseEntity<AdressDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        AdressDTO result = response.getBody();
        return result != null ? result.getWards() : Collections.emptyList();
    }


    public List<AdressDTO> getTinhGHN() {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province";
        HttpEntity<String> entity = new HttpEntity<>(createGHNHeaders());

        ResponseEntity<GHNResponse<List<ProvinceData>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody() != null ?
                response.getBody().getData().stream()
                        .map(data -> {
                            AdressDTO dto = new AdressDTO();
                            dto.setName(data.getProvinceName());
                            dto.setCode(data.getProvinceID());
                            dto.setWards(null);
                            dto.setDistricts(Collections.emptyList());
                            return dto;
                        })
                        .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public List<AdressDTO> getHuyenGHN(int provinceId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district";
        Map<String, Integer> body = new HashMap<>();
        body.put("province_id", provinceId);

        HttpHeaders headers = createGHNHeaders(); // Hàm này bạn đã định nghĩa ở nơi khác
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<GHNResponse<List<DistrictData>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST, // ✅ Phải là POST mới đúng
                    entity,
                    new ParameterizedTypeReference<GHNResponse<List<DistrictData>>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().stream()
                        .map(data -> {
                            AdressDTO dto = new AdressDTO();
                            dto.setName(data.getDistrictName());
                            dto.setCode(data.getDistrictID());
                            dto.setWards(null);
                            dto.setDistricts(Collections.emptyList());
                            return dto;
                        })
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace(); // Gợi ý: log ra để debug lỗi rõ hơn
            return Collections.emptyList();
        }
    }

    public List<AdressDTO> getXaGHN(int districtId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward";
        
        Map<String, Integer> body = new HashMap<>();
        body.put("district_id", districtId);

        HttpHeaders headers = createGHNHeaders(); // Hàm tự tạo header chứa Token
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<GHNResponse<List<WardData>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST, // ✅ Đã đổi từ GET sang POST
                    entity,
                    new ParameterizedTypeReference<GHNResponse<List<WardData>>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getData() != null) {

                return response.getBody().getData().stream()
                        .map(data -> {
                            AdressDTO dto = new AdressDTO();
                            dto.setName(data.getWardName());
                            dto.setCode(Integer.parseInt(data.getWardCode())); // wardCode là String -> ép kiểu
                            dto.setWards(null);
                            dto.setDistricts(Collections.emptyList());
                            return dto;
                        })
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace(); // Khuyến khích log lỗi để dễ debug
            return Collections.emptyList();
        }
    }


    private HttpHeaders createGHNHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    private static class GHNResponse<T> {
        private int code;
        private String message;
        private T data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    private static class ProvinceData {
        @JsonProperty("ProvinceID")
        private int provinceID;

        @JsonProperty("ProvinceName")
        private String provinceName;

        public int getProvinceID() {
            return provinceID;
        }

        public String getProvinceName() {
            return provinceName;
        }
    }

    private static class DistrictData {
        @JsonProperty("DistrictID")
        private int districtID;

        @JsonProperty("DistrictName")
        private String districtName;

        public int getDistrictID() {
            return districtID;
        }

        public String getDistrictName() {
            return districtName;
        }
    }

    private static class WardData {
        @JsonProperty("WardCode")
        private String wardCode;

        @JsonProperty("WardName")
        private String wardName;

        public String getWardCode() {
            return wardCode;
        }

        public String getWardName() {
            return wardName;
        }
    }
    public static String getTextBeforeDot(String input) {
        int dotIndex = input.indexOf('.');
        if (dotIndex != -1) {
            return input.substring(0, dotIndex).trim();
        }
        return input.trim();
    }

    public static int getFirstNumberAfterDot(String input) {
        String[] parts = input.split("\\.");
        if (parts.length > 1) {
            String[] numbers = parts[1].trim().split("\\s+");
            if (numbers.length > 0) {
                return Integer.parseInt(numbers[0]);
            }
        }
        throw new IllegalArgumentException("Không tìm thấy số đầu tiên sau dấu chấm");
    }

    public static int getSecondNumberAfterDot(String input) {
        String[] parts = input.split("\\.");
        if (parts.length > 1) {
            String[] numbers = parts[1].trim().split("\\s+");
            if (numbers.length > 1) {
                return Integer.parseInt(numbers[1]);
            }
        }
        throw new IllegalArgumentException("Không tìm thấy số thứ hai sau dấu chấm");
    }

    public static int getThirdNumberAfterDot(String input) {
    	System.out.println("chuỗi là"+input);
        String[] parts = input.split("\\.");
        if (parts.length > 1) {
            String[] numbers = parts[1].trim().split("\\s+");
            if (numbers.length > 2) {
                return Integer.parseInt(numbers[2]);
            }
        }
        throw new IllegalArgumentException("Không tìm thấy số thứ ba sau dấu chấm");
    }

}
