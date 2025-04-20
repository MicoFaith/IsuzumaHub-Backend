package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.AuthRequest;
import com.isuzumahub.diagnostic.dto.AuthResponse;
import com.isuzumahub.diagnostic.dto.SignupRequest;
import com.isuzumahub.diagnostic.model.enums.UserRole;
import com.isuzumahub.diagnostic.service.AuthService;
import com.isuzumahub.diagnostic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Email already exists"));
        }
        
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Username already exists"));
        }

        // Set default role as PATIENT if not specified
        if (request.getRole() == null) {
            request.setRole(UserRole.ROLE_PATIENT);
        }
        
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/employee")
    public ResponseEntity<AuthResponse> signupEmployee(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Email already exists"));
        }
        
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Username already exists"));
        }
        
        request.setRole(UserRole.ROLE_LAB_TECHNICIAN);
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<AuthResponse> signupAdmin(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Email already exists"));
        }
        
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Username already exists"));
        }
        
        request.setRole(UserRole.ROLE_ADMIN);
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthResponse> verifyToken(@RequestHeader("Authorization") String token) {
        AuthResponse response = authService.verifyToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestParam String email) {
        if (!userService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Email not found"));
        }
        
        authService.sendPasswordResetEmail(email);
        return ResponseEntity.ok(new AuthResponse(null, null, null, "Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        boolean success = authService.resetPassword(token, newPassword);
        if (success) {
            return ResponseEntity.ok(new AuthResponse(null, null, null, "Password reset successfully"));
        }
        return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Invalid or expired token"));
    }
} 