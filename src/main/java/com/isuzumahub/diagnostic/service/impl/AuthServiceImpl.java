package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.dto.AuthRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserDto registerUser(SignupRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.ROLE_PATIENT);
        user.setEnabled(false);

        user = userRepository.save(user);

        String token = generateEmailVerificationToken(user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);

        return modelMapper.toUserDto(user);
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        UserDto userDto = registerUser(request);
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, userDto.getUsername(), userDto.getRole().name(), "Registration successful");
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), "Login successful");
    }

    @Override
    public AuthResponse verifyToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new BadRequestException("Invalid or expired token");
        }
        
        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), "Token is valid");
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
        
        user.setEnabled(true);
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