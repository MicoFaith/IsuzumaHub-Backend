package com.isuzumahub.diagnostic.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Hash for admin123: " + encoder.encode("admin123"));
        System.out.println("Hash for employee123: " + encoder.encode("employee123"));
    }
}