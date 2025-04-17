package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.dto.UserDto;
import com.isuzumahub.diagnostic.exception.BadRequestException;
import com.isuzumahub.diagnostic.exception.ResourceNotFoundException;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.UserRole;
import com.isuzumahub.diagnostic.repository.UserRepository;
import com.isuzumahub.diagnostic.security.UserPrincipal;
import com.isuzumahub.diagnostic.service.UserService;
import com.isuzumahub.diagnostic.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        User user = modelMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        
        // Default role is PATIENT if not specified
        if (user.getRole() == null) {
            user.setRole(UserRole.PATIENT);
        }
        
        User savedUser = userRepository.save(user);
        return modelMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return modelMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(modelMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(modelMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check if email is being changed and if it's already in use
        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        
        User updatedUser = modelMapper.toUser(userDto);
        updatedUser.setId(existingUser.getId());
        updatedUser.setPassword(existingUser.getPassword()); // Don't update password here
        
        User savedUser = userRepository.save(updatedUser);
        return modelMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        return modelMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("User", "reset password token", token));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        
        User savedUser = userRepository.save(user);
        return modelMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email verification token", token));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        
        User savedUser = userRepository.save(user);
        return modelMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        
        userRepository.save(user);
        return token;
    }

    @Override
    @Transactional
    public String generateEmailVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        
        userRepository.save(user);
        return token;
    }

    @Override
    public User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
} 