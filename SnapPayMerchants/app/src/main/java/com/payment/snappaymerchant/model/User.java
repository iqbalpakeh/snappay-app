package com.payment.snappaymerchant.model;

public class User {

    private String mEmail;

    private String mToken;

    public User(String email, String token) {
        this.mEmail = email;
        this.mToken = token;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

}
