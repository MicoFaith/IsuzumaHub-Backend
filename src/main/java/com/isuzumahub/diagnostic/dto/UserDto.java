package com.isuzumahub.diagnostic.dto;

import com.isuzumahub.diagnostic.model.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String address;
    private UserRole role;
    private boolean enabled;
} 