package com.isuzumahub.diagnostic.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private String email;

    public LoginResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }
}