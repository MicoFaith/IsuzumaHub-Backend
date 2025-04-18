package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.dto.AuthResponse;
import com.isuzumahub.diagnostic.dto.SignupRequest;
import com.isuzumahub.diagnostic.dto.UserDto;
import com.isuzumahub.diagnostic.exception.BadRequestException;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.UserRole;
import com.isuzumahub.diagnostic.repository.UserRepository;
import com.isuzumahub.diagnostic.security.JwtTokenProvider;
import com.isuzumahub.diagnostic.service.AuthService;
import com.isuzumahub.diagnostic.service.EmailService;
import com.isuzumahub.diagnostic.service.UserService;
import com.isuzumahub.diagnostic.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto registerUser(SignupRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(UserRole.PATIENT);
        user.setEmailVerified(false);

        user = userRepository.save(user);

        String token = generateEmailVerificationToken(user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);

        return modelMapper.toUserDto(user);
    }

    @Override
    public String generateEmailVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);

        return true;
    }
} 