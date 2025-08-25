package com.example.e_commerce.DTO.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZaloPayResponseDTO {
    private String sub_return_message;
    private String return_message;
    private String zp_trans_token;
    private String qr_code;
    private Integer return_code; // Đổi sang Integer để hỗ trợ null
    private Integer sub_return_code; // Đổi sang Integer để hỗ trợ null
    private String order_url;
    private String order_token;
    private String cashier_order_url;

    public String getSub_return_message() {
        return sub_return_message;
    }

    public void setSub_return_message(String sub_return_message) {
        this.sub_return_message = sub_return_message;
    }

    public String getReturn_message() {
        return return_message;
    }

    public void setReturn_message(String return_message) {
        this.return_message = return_message;
    }

    public String getZp_trans_token() {
        return zp_trans_token;
    }

    public void setZp_trans_token(String zp_trans_token) {
        this.zp_trans_token = zp_trans_token;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public Integer getReturn_code() {
        return return_code;
    }

    public void setReturn_code(Integer return_code) {
        this.return_code = return_code;
    }

    public Integer getSub_return_code() {
        return sub_return_code;
    }

    public void setSub_return_code(Integer sub_return_code) {
        this.sub_return_code = sub_return_code;
    }

    public String getOrder_url() {
        return order_url;
    }

    public void setOrder_url(String order_url) {
        this.order_url = order_url;
    }

    public String getOrder_token() {
        return order_token;
    }

    public void setOrder_token(String order_token) {
        this.order_token = order_token;
    }

    public String getCashier_order_url() {
        return cashier_order_url;
    }

    public void setCashier_order_url(String cashier_order_url) {
        this.cashier_order_url = cashier_order_url;
    }
}