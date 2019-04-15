package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class ServiceProvider {

    @SerializedName("account")
    private String account;

    public ServiceProvider(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "account='" + account + '\'' +
                '}';
    }
}
