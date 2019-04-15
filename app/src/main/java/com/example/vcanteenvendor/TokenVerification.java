package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class TokenVerification {

    @SerializedName("expired")
    private boolean isExpired;

    public boolean isExpired() {
        return isExpired;
    }
}
