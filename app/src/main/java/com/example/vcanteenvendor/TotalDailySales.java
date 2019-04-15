package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class TotalDailySales {
    @SerializedName("sum")
    private int sum;

    public TotalDailySales(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
