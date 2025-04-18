package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.dto.UserDto;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.UserRole;

import java.util.List;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    List<UserDto> getUsersByRole(UserRole role);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto changePassword(Long id, String oldPassword, String newPassword);
    UserDto resetPassword(String token, String newPassword);
    UserDto verifyEmail(String token);
    String generatePasswordResetToken(String email);
    String generateEmailVerificationToken(String email);
    User getCurrentUser();
    UserDto createUser(UserDto userDto);
    boolean existsByEmail(String email);
    UserDto convertToDto(User user);
} 