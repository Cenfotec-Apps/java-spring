package com.project.demo.logic.entity.user;

public class LoginResponse {
    private String token;

    private User authUser;

    private long expiresIn;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }
}
