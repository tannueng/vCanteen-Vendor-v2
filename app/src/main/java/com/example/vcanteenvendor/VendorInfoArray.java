package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VendorInfoArray {

    @SerializedName("vendorInfo")
    public List<Vendor> vendorInfo;

    @SerializedName("vendorPaymentMethod")
    private List<ServiceProvider> vendorPaymentMethod;

    public Boolean findServiceProviderFromList(List<ServiceProvider> vendorPaymentMethod, String account) {

        if (vendorPaymentMethod == null) return  false;

        for (ServiceProvider serviceProvider : vendorPaymentMethod) {
            if ( serviceProvider.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }



    public List<ServiceProvider> getVendorPaymentMethod() {
        return vendorPaymentMethod;
    }

    public void setVendorPaymentMethod(List<ServiceProvider> vendorPaymentMethod) {
        this.vendorPaymentMethod = vendorPaymentMethod;
    }

    @Override
    public String toString() {
        return "VendorInfoArray{" +
                "vendorInfo=" + vendorInfo +
                '}';
    }

    public List<Vendor> getVendorInfo() {
        return vendorInfo;
    }

    public void setVendorInfo(List<Vendor> vendorInfo) {
        this.vendorInfo = vendorInfo;
    }


    public VendorInfoArray(List<Vendor> vendorInfo, List<ServiceProvider> vendorPaymentMethod) {
        this.vendorInfo = vendorInfo;
        this.vendorPaymentMethod = vendorPaymentMethod;
    }
}
