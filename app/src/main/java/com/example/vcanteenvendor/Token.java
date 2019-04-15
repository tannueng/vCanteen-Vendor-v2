package com.example.vcanteenvendor;

public class Token {

    private String token;

    public Token(String email, String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                '}';
    }
}

