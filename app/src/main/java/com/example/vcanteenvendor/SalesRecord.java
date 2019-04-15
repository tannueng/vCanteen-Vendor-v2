package com.example.vcanteenvendor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SalesRecord {

    @SerializedName("order")
    @Expose
    private ArrayList<SalesRecordOrder> salesRecordOrder;

    @SerializedName("bestSeller")
    @Expose
    private BestSeller bestSeller;

    @SerializedName("totalDailySales")
    @Expose
    private TotalDailySales totalDailySales;

    public SalesRecord(ArrayList<SalesRecordOrder> salesRecordOrder) {
        this.salesRecordOrder = salesRecordOrder;
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "salesRecordOrders=" + salesRecordOrder +
                ", bestSeller=" + bestSeller +
                ", totalDailySales=" + totalDailySales +
                '}';
    }

    public ArrayList<SalesRecordOrder> getSalesRecordOrders() {
        return salesRecordOrder;
    }

    public void setSalesRecordOrders(ArrayList<SalesRecordOrder> salesRecordOrders) {
        this.salesRecordOrder = salesRecordOrders;
    }

    public BestSeller getBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(BestSeller bestSeller) {
        this.bestSeller = bestSeller;
    }

    public TotalDailySales getTotalDailySales() {
        return totalDailySales;
    }

    public void setTotalDailySales(TotalDailySales totalDailySales) {
        this.totalDailySales = totalDailySales;
    }
}
