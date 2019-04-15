package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class BestSeller {

    @SerializedName("orderName")
    private String orderName;
    @SerializedName("amount")
    private int amount;

    public BestSeller(String ordername) {
        orderName = ordername;
    }

    public BestSeller(int amount) {
        this.amount = amount;
    }

    public String getOrdername() {
        return orderName;
    }

    public void setOrdername(String ordername) {
        orderName = ordername;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


}
