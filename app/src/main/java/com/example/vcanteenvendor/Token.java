package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("token")
    private String token;

    @SerializedName("email")
    private String email;

    public Token(String email, String token) {
        this.email = email;
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                '}';
    }


}

