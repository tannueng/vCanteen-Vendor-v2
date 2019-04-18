package com.example.vcanteenvendor.POJO;

import com.google.gson.annotations.SerializedName;

public class BugReport {
    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("vendorId")
    public int vendorId;

    @SerializedName("message")
    public String message;


}
