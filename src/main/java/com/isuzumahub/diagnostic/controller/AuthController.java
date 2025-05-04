package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.dto.LoginRequest;
import com.isuzumahub.diagnostic.dto.LoginResponse;
import com.isuzumahub.diagnostic.dto.SignUpRequest;
import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SECRET_KEY = "your-256-bit-secret-key-here-32-chars"; // Must match JwtAuthenticationFilter
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private String generateJwt(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("[DEBUG] Processing login request for email: " + loginRequest.getEmail());
        System.out.println("[DEBUG] Login payload: " + loginRequest);
        try {
            boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            System.out.println("[DEBUG] Authentication result: " + isAuthenticated);
            if (!isAuthenticated) {
                System.out.println("[DEBUG] Authentication failed for email: " + loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid email or password"));
            }

            Object user = authService.findUserByEmail(loginRequest.getEmail());
            System.out.println("[DEBUG] User found: " + (user != null));
            if (user == null) {
                System.out.println("[DEBUG] User not found for email: " + loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("User not found"));
            }

            String role = user instanceof User ? "USER" :
                    user instanceof Admin ? "ADMIN" : "EMPLOYEE";
            String fullName = user instanceof User ? ((User) user).getFullName() :
                    user instanceof Admin ? ((Admin) user).getFullName() :
                            ((Employee) user).getFullName();
            System.out.println("[DEBUG] Role: " + role + ", Full Name: " + fullName);

            String token = generateJwt(loginRequest.getEmail(), role);
            System.out.println("[DEBUG] Generated JWT for email: " + loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponse(loginRequest.getEmail(), role, fullName, token));
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        System.out.println("[DEBUG] Processing admin login request for email: " + loginRequest.getEmail());
        boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }

        Object user = authService.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Admin not found"));
        }

        if (!(user instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("This endpoint is for admins only"));
        }

        String role = "ADMIN";
        String fullName = ((Admin) user).getFullName();
        String token = generateJwt(loginRequest.getEmail(), role);

        return ResponseEntity.ok(new LoginResponse(loginRequest.getEmail(), role, fullName, token));
    }

    @PostMapping("/employee-login")
    public ResponseEntity<?> employeeLogin(@RequestBody LoginRequest loginRequest) {
        System.out.println("[DEBUG] Processing employee login request for email: " + loginRequest.getEmail());
        boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }

        Object user = authService.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Employee not found"));
        }

        if (!(user instanceof Employee)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("This endpoint is for employees only"));
        }

        String role = "EMPLOYEE";
        String fullName = ((Employee) user).getFullName();
        String token = generateJwt(loginRequest.getEmail(), role);

        return ResponseEntity.ok(new LoginResponse(loginRequest.getEmail(), role, fullName, token));
    }

    @GetMapping("/admin/me")
    public ResponseEntity<?> getAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = authentication.getName();
        Object user = authService.findUserByEmail(email);
        if (user == null || !(user instanceof Admin admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Not an admin"));
        }

        return ResponseEntity.ok(new LoginResponse(email, "ADMIN", admin.getFullName()));
    }

    @GetMapping("/employee/me")
    public ResponseEntity<?> getEmployeeProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = authentication.getName();
        Object user = authService.findUserByEmail(email);
        if (user == null || !(user instanceof Employee employee)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Not an employee"));
        }

        return ResponseEntity.ok(new LoginResponse(email, "EMPLOYEE", employee.getFullName()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        System.out.println("[DEBUG] Processing signup request for email: " + signUpRequest.getEmail());
        try {
            boolean userexist = authService.checkUser(signUpRequest.getEmail());
            if (userexist) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Email already registered"));
            }

            User user = new User();
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setFullName(signUpRequest.getFullName());
            user.setMobileNumber(signUpRequest.getMobileNumber());

            authService.saveUser(user);

            String token = generateJwt(user.getEmail(), "USER");
            return ResponseEntity.ok(new LoginResponse(user.getEmail(), "USER", user.getFullName(), token));
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in signup: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to register user: " + e.getMessage()));
        }
    }
}