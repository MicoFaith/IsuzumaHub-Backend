package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.AuthRequest;
import com.isuzumahub.diagnostic.dto.AuthResponse;
import com.isuzumahub.diagnostic.dto.SignupRequest;
import com.isuzumahub.diagnostic.dto.UserDto;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.UserRole;
import com.isuzumahub.diagnostic.security.JwtTokenProvider;
import com.isuzumahub.diagnostic.service.AuthService;
import com.isuzumahub.diagnostic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDto userDto = userService.getUserByEmail(request.getEmail());
        return ResponseEntity.ok(new AuthResponse(jwt, userDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupRequest request) {
        // Check if email is already in use
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Create new user
        UserDto userDto = authService.registerUser(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/signup/employee")
    public ResponseEntity<UserDto> signupEmployee(@Valid @RequestBody SignupRequest request) {
        // Check if email is already in use
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Set role to LAB_EMPLOYEE
        request.setRole(UserRole.LAB_EMPLOYEE);
        
        // Create new employee
        UserDto userDto = authService.registerUser(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<UserDto> signupAdmin(@Valid @RequestBody SignupRequest request) {
        // Check if email is already in use
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Set role to ADMIN
        request.setRole(UserRole.ADMIN);
        
        // Create new admin
        UserDto userDto = authService.registerUser(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if (authService.verifyEmail(token)) {
            return ResponseEntity.ok("Email verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        if (authService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Password reset successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }
} 