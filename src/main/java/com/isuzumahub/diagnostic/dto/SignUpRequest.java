package com.isuzumahub.diagnostic.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String fullName;
    private String email;
    private String mobileNumber;
    private String password;
    private String confirmPassword;
}