package com.example.vcanteenvendor;

import com.google.firebase.database.Exclude;

public class User {

    public String account_type;
    public String email;
    public String firebaseToken;


    public User(){

    }

    public User(String account_type, String email, String firebaseToken) {
        this.account_type = account_type;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    @Exclude
    public String getUserEmail() {
        return email;
    }

    @Exclude
    public void setUserEmail(String email) {
        this.email = email;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
