package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.dto.LoginResponse;
import com.isuzumahub.diagnostic.dto.SignUpRequest;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        try {
            User user = authService.signUp(request);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to create user: " + e.getMessage()));
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<?> handleAuthFallback() {
        System.out.println("[DEBUG] Fallback /auth endpoint reached - Spring Security did not intercept the request");
        return ResponseEntity.status(400).body(new ErrorResponse("Bad request - use form login"));
    }

    @GetMapping("/auth/success")
    public ResponseEntity<?> loginSuccess() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(new LoginResponse("Login successful", email));
    }

    @GetMapping("/auth/error")
    public ResponseEntity<?> loginError() {
        return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("User not found"));
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", user.getFullName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRoles().stream().findFirst().orElse("USER"));
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(new ErrorResponse("Password reset functionality not yet implemented"));
    }
}