package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private String status;

    @SerializedName("accountType")
    private String accountType;

    @SerializedName("vendorSessionToken")
    private String token;

    @SerializedName("vendorId")
    private int vendor_id;

    public LoginResponse(String token, int vendor_id) {
        this.token = token;
        this.vendor_id = vendor_id;
    }



    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", accountType='" + accountType + '\'' +
                ", token='" + token + '\'' +
                ", vendor_id=" + vendor_id +
                '}';
    }

    public String getAccountType() {
        return accountType;
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public int getVendor_id() {
        return vendor_id;
    }
}
