package com.cinelog.CineLog.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthResponse {
   private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
