package com.example.vcanteenvendor;

public class ChangePass {

    private String passwordNew;
    private String email;

    public ChangePass(String passwordNew, String email) {
        this.passwordNew = passwordNew;
        this.email = email;
    }

    @Override
    public String toString() {
        return "ChangePass{" +
                "passwordNew='" + passwordNew + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
