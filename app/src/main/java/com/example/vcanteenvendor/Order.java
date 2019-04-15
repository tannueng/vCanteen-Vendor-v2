package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("orderId")
    private int orderId;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("pickupSlot")
    private int pickupSlot;

    @SerializedName("orderPrice")
    private int orderPrice;

    @SerializedName("orderCustomerId")
    private int orderCustomerId;

    @SerializedName("timestamp")
    private String createdAt;

    @SerializedName("vendorId")
    private int vendorId;

    @SerializedName("orderName")
    private String orderName;

    @SerializedName("orderNameExtra")
    private String orderNameExtra;

    @SerializedName("transactionId")
    private int transactionId;

    public Order(int orderId, String orderStatus, String createdAt, String orderName, String orderNameExtra) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.orderName = orderName;
        this.orderNameExtra = orderNameExtra;
    }

    public Order(int orderId, String orderStatus, int pickupSlot, int orderPrice, int orderCustomerId, String createdAt, int vendorId, String orderName, String orderNameExtra, int transactionId) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.pickupSlot = pickupSlot;
        this.orderPrice = orderPrice;
        this.orderCustomerId = orderCustomerId;
        this.createdAt = createdAt;
        this.vendorId = vendorId;
        this.orderName = orderName;
        this.orderNameExtra = orderNameExtra;
        this.transactionId = transactionId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPickupSlot() {
        return pickupSlot;
    }

    public void setPickupSlot(int pickupSlot) {
        this.pickupSlot = pickupSlot;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderCustomerId() {
        return orderCustomerId;
    }

    public void setOrderCustomerId(int orderCustomerId) {
        this.orderCustomerId = orderCustomerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNameExtra() {
        return orderNameExtra;
    }

    public void setOrderNameExtra(String orderNameExtra) {
        this.orderNameExtra = orderNameExtra;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
