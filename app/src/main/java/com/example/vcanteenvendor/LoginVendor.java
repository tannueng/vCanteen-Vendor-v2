package com.example.vcanteenvendor;

public class LoginVendor {
    private String email;
    private String password;
    private String firebaseToken;
    private String account_type;

    public LoginVendor(String email, String password, String firebaseToken, String account_type) {
        this.email = email;
        this.password = password;
        this.firebaseToken = firebaseToken;
        this.account_type = account_type;
    }

    @Override
    public String toString() {
        return "LoginVendor{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                ", account_type='" + account_type + '\'' +
                '}';
    }
}
