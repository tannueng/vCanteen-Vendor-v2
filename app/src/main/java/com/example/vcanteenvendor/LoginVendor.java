package com.example.vcanteenvendor;

public class LoginVendor {
    private String email;
    private String password;
    private String firebaseToken;

    private String account_type;

    public LoginVendor(String email, String password, String firebaseToken) {
        this.email = email;
        this.password = password;
        this.firebaseToken = firebaseToken;
    }

    public LoginVendor(String email, String firebaseToken) {
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String toString() {
        return "LoginVendor{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public LoginVendor(String email, String password, String firebaseToken, String account_type) {
        this.email = email;
        this.password = password;
        this.firebaseToken = firebaseToken;
        this.account_type = account_type;
    }

}
