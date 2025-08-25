package com.example.e_commerce.DTO.response;

import java.time.LocalDateTime;

public class ZaloPayOrder {
	private String appTransId;
    private String zpTransToken;
    private String orderToken;
    private int returnCode;
    private int subReturnCode;
    private long appTime;
    private long amount;
    private String description;
    private String appUser;
    private String internalOrderId;
    private LocalDateTime responseTime;
    private String transactionStatus;
    private String orderUrl;

    public ZaloPayOrder() {}

    public ZaloPayOrder(String appTransId, String zpTransToken, String orderToken, int returnCode, 
                       int subReturnCode, long appTime, long amount, String description, 
                       String appUser, String internalOrderId, LocalDateTime responseTime, 
                       String transactionStatus, String orderUrl) {
        this.appTransId = appTransId;
        this.zpTransToken = zpTransToken;
        this.orderToken = orderToken;
        this.returnCode = returnCode;
        this.subReturnCode = subReturnCode;
        this.appTime = appTime;
        this.amount = amount;
        this.description = description;
        this.appUser = appUser;
        this.internalOrderId = internalOrderId;
        this.responseTime = responseTime;
        this.transactionStatus = transactionStatus;
        this.orderUrl = orderUrl;
    }

    public String getAppTransId() { return appTransId; }
    public void setAppTransId(String appTransId) { this.appTransId = appTransId; }
    public String getZpTransToken() { return zpTransToken; }
    public void setZpTransToken(String zpTransToken) { this.zpTransToken = zpTransToken; }
    public String getOrderToken() { return orderToken; }
    public void setOrderToken(String orderToken) { this.orderToken = orderToken; }
    public int getReturnCode() { return returnCode; }
    public void setReturnCode(int returnCode) { this.returnCode = returnCode; }
    public int getSubReturnCode() { return subReturnCode; }
    public void setSubReturnCode(int subReturnCode) { this.subReturnCode = subReturnCode; }
    public long getAppTime() { return appTime; }
    public void setAppTime(long appTime) { this.appTime = appTime; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAppUser() { return appUser; }
    public void setAppUser(String appUser) { this.appUser = appUser; }
    public String getInternalOrderId() { return internalOrderId; }
    public void setInternalOrderId(String internalOrderId) { this.internalOrderId = internalOrderId; }
    public LocalDateTime getResponseTime() { return responseTime; }
    public void setResponseTime(LocalDateTime responseTime) { this.responseTime = responseTime; }
    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    public String getOrderUrl() { return orderUrl; }
    public void setOrderUrl(String orderUrl) { this.orderUrl = orderUrl; }
}
