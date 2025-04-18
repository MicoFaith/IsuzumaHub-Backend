package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.dto.SignupRequest;
import com.isuzumahub.diagnostic.dto.UserDto;

public interface AuthService {
    UserDto registerUser(SignupRequest request);
    boolean verifyEmail(String token);
    void sendPasswordResetEmail(String email);
    boolean resetPassword(String token, String newPassword);
} 