package com.example.vcanteenvendor;

public class VendorSingleton {

    private static VendorSingleton instance;

    public VendorSingleton() {

    }

    public static VendorSingleton getInstance(){
        if(instance == null) instance = new VendorSingleton();
        return  instance;
    }

    String vendorName, email, vendorImage;

    public String getVendorImage() {
        return vendorImage;
    }

    public void setVendorImage(String vendorImage) {
        this.vendorImage = vendorImage;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
