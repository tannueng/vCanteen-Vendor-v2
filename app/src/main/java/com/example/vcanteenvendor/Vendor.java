package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Vendor {

    @SerializedName("vendorId")
    private int vendorId;

    @SerializedName("adminPermission")
    private String adminPermission;

    @SerializedName("vendorNo")
    private int vendorNo;

    @SerializedName("vendorName")
    private String vendorName;

    @SerializedName("vendorStatus")
    private String vendorStatus;

    @SerializedName("vendorImage")
    private String vendorImage;

    @SerializedName("vendorPassword")
    private String vendorPassword;

    @SerializedName("accountType")
    private String vendorAccountType;

    @SerializedName("vendorEmail")
    private String vendorEmail;


    public Vendor(int vendorId, String adminPermission, int vendorNo, String vendorName, String vendorStatus, String vendorImage, String vendorPassword, String vendorAccountType, String vendorEmail) {
        this.vendorId = vendorId;
        this.adminPermission = adminPermission;
        this.vendorNo = vendorNo;
        this.vendorName = vendorName;
        this.vendorStatus = vendorStatus;
        this.vendorImage = vendorImage;
        this.vendorPassword = vendorPassword;
        this.vendorAccountType = vendorAccountType;
        this.vendorEmail = vendorEmail;
    }

    public Vendor() {
    }



    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getAdminPermission() {
        return adminPermission;
    }

    public void setAdminPermission(String adminPermission) {
        this.adminPermission = adminPermission;
    }

    public int getVendorNo() {
        return vendorNo;
    }

    public void setVendorNo(int vendorNo) {
        this.vendorNo = vendorNo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorStatus() {
        return vendorStatus;
    }

    public void setVendorStatus(String vendorStatus) {
        this.vendorStatus = vendorStatus;
    }

    public String getVendorImage() {
        return vendorImage;
    }

    public void setVendorImage(String vendorImage) {
        this.vendorImage = vendorImage;
    }

    public String getVendorPassword() {
        return vendorPassword;
    }

    public void setVendorPassword(String vendorPassword) {
        this.vendorPassword = vendorPassword;
    }

    public String getVendorAccountType() {
        return vendorAccountType;
    }

    public void setVendorAccountType(String vendorAccountType) {
        this.vendorAccountType = vendorAccountType;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }
}
