package com.project.demo.logic.entity.auth;

public class LoginCredentials {
    private String username;
    private String password;

    // Constructor vacío
    public LoginCredentials() {
    }

    // Constructor con parámetros
    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}