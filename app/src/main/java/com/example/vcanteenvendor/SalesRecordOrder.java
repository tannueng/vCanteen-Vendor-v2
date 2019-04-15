package com.example.vcanteenvendor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesRecordOrder {


    public SalesRecordOrder(int orderIdSales, String orderNameSales, String orderNameExtraSales, int orderPriceSales) {
        this.orderIdSales = orderIdSales;
        this.orderNameSales = orderNameSales;
        this.orderNameExtraSales = orderNameExtraSales;
        this.orderPriceSales = orderPriceSales;
    }

    public int getOrderIdSales() {
        return orderIdSales;
    }

    public void setOrderIdSales(int orderIdSales) {
        this.orderIdSales = orderIdSales;
    }

    public String getOrderNameSales() {
        return orderNameSales;
    }

    public void setOrderNameSales(String orderNameSales) {
        this.orderNameSales = orderNameSales;
    }

    public String getOrderNameExtraSales() {
        return orderNameExtraSales;
    }

    public void setOrderNameExtraSales(String orderNameExtraSales) {
        this.orderNameExtraSales = orderNameExtraSales;
    }

    public int getOrderPriceSales() {
        return orderPriceSales;
    }

    public void setOrderPriceSales(int orderPriceSales) {
        this.orderPriceSales = orderPriceSales;
    }

    @SerializedName("orderId")
    @Expose
    public int orderIdSales;

    @SerializedName("orderName")
    @Expose
    public String orderNameSales;

    @SerializedName("orderNameExtra")
    @Expose
    public String orderNameExtraSales;

    @SerializedName("orderPrice")
    @Expose
    public int orderPriceSales;

    @Override
    public String toString() {
        return "SalesRecordOrder{" +
                "orderIdSales=" + orderIdSales +
                ", orderNameSales='" + orderNameSales + '\'' +
                ", orderNameExtraSales='" + orderNameExtraSales + '\'' +
                ", orderPrice=" + orderPriceSales +
                '}';
    }



}
