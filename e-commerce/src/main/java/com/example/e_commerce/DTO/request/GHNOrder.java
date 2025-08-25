package com.example.e_commerce.DTO.request;

import java.util.List;

import com.example.e_commerce.DTO.response.GHNLog;

public class GHNOrder {
    private int payment_type_id;
    private String note;
    private String required_note;
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;
    private String from_ward_code; // Thêm trường from_ward_code
    private int from_district_id; // Trường bắt buộc đã đề xuất trước
    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_code;
    private int to_district_id;
    private int cod_amount;
    private String content;
    private int weight;
    private int length;
    private int width;
    private int height;
    private int service_id;
    private int service_type_id;
    private int insurance_value; 
    private int shop_id; 
    private String status;
    private float total_fee;
    private String client_order_code; 
    private String coupon; 
    private List<GHNItem> items;
    private List<GHNLog> log;
    public int getPayment_type_id() {
        return payment_type_id;
    }

    public String getStatus() {
		return status;
	}

	public List<GHNLog> getLog() {
		return log;
	}

	public void setLog(List<GHNLog> log) {
		this.log = log;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(float total_fee) {
		this.total_fee = total_fee;
	}

	public void setPayment_type_id(int payment_type_id) {
        this.payment_type_id = payment_type_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRequired_note() {
        return required_note;
    }

    public void setRequired_note(String required_note) {
        this.required_note = required_note;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_phone() {
        return from_phone;
    }

    public void setFrom_phone(String from_phone) {
        this.from_phone = from_phone;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getFrom_ward_name() {
        return from_ward_name;
    }

    public void setFrom_ward_name(String from_ward_name) {
        this.from_ward_name = from_ward_name;
    }

    public String getFrom_district_name() {
        return from_district_name;
    }

    public void setFrom_district_name(String from_district_name) {
        this.from_district_name = from_district_name;
    }

    public String getFrom_province_name() {
        return from_province_name;
    }

    public void setFrom_province_name(String from_province_name) {
        this.from_province_name = from_province_name;
    }

    public String getFrom_ward_code() {
        return from_ward_code;
    }

    public void setFrom_ward_code(String from_ward_code) {
        this.from_ward_code = from_ward_code;
    }

    public int getFrom_district_id() {
        return from_district_id;
    }

    public void setFrom_district_id(int from_district_id) {
        this.from_district_id = from_district_id;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTo_phone() {
        return to_phone;
    }

    public void setTo_phone(String to_phone) {
        this.to_phone = to_phone;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getTo_ward_code() {
        return to_ward_code;
    }

    public void setTo_ward_code(String to_ward_code) {
        this.to_ward_code = to_ward_code;
    }

    public int getTo_district_id() {
        return to_district_id;
    }

    public void setTo_district_id(int to_district_id) {
        this.to_district_id = to_district_id;
    }

    public int getCod_amount() {
        return cod_amount;
    }

    public void setCod_amount(int cod_amount) {
        this.cod_amount = cod_amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(int service_type_id) {
        this.service_type_id = service_type_id;
    }

    public int getInsurance_value() {
        return insurance_value;
    }

    public void setInsurance_value(int insurance_value) {
        this.insurance_value = insurance_value;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getClient_order_code() {
        return client_order_code;
    }

    public void setClient_order_code(String client_order_code) {
        this.client_order_code = client_order_code;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public List<GHNItem> getItems() {
        return items;
    }

    public void setItems(List<GHNItem> items) {
        this.items = items;
    }
}