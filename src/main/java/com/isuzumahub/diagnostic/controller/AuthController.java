package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.config.JwtTokenProvider;
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
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwt;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SECRET_KEY = "your-256-bit-secret-key-here-32-chars";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

//    private String generateJwt(String email, String role) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(getSigningKey())
//                .compact();
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Passwords do not match"));
        }

        if (authService.checkUser(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Email already registered"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setMobileNumber(request.getMobileNumber());



        authService.saveUser(user);
        String token = jwt.generateToken(user.getEmail(), "USER");

        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), "USER", user.getFullName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (!authService.authenticate(request.getEmail(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }

        Object user = authService.findUserByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found"));
        }

        String role = user instanceof Admin ? "ADMIN" :
                user instanceof Employee ? "EMPLOYEE" : "USER";
        String fullName = user instanceof Admin ? ((Admin) user).getFullName() :
                user instanceof Employee ? ((Employee) user).getFullName() :
                        ((User) user).getFullName();
        String token = jwt.generateToken(request.getEmail(), role);

        return ResponseEntity.ok(new LoginResponse(token, request.getEmail(), role, fullName));
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        if (!authService.authenticate(request.getEmail(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }

        Object user = authService.findUserByEmail(request.getEmail());
        if (!(user instanceof Admin admin)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Admin not found"));
        }

        String token = jwt.generateToken(admin.getEmail(), "ADMIN");
        return ResponseEntity.ok(new LoginResponse(admin.getEmail(), "ADMIN", admin.getFullName(), token));
    }

    @PostMapping("/employee-login")
    public ResponseEntity<?> employeeLogin(@RequestBody LoginRequest request) {
        if (!authService.authenticate(request.getEmail(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }

        Object user = authService.findUserByEmail(request.getEmail());
        if (!(user instanceof Employee employee)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Employee not found"));
        }

        String token = jwt.generateToken(employee.getEmail(), "EMPLOYEE");
        return ResponseEntity.ok(new LoginResponse(employee.getEmail(), "EMPLOYEE", employee.getFullName(), token));
    }

    @GetMapping("/admin/me")
    public ResponseEntity<?> getAdminProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = auth.getName();
        Object user = authService.findUserByEmail(email);
        if (!(user instanceof Admin admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Not an admin"));
        }

        return ResponseEntity.ok(new LoginResponse(email, "ADMIN", admin.getFullName(), null));
    }

    @GetMapping("/employee/me")
    public ResponseEntity<?> getEmployeeProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = auth.getName();
        Object user = authService.findUserByEmail(email);
        if (!(user instanceof Employee employee)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Not an employee"));
        }

        return ResponseEntity.ok(new LoginResponse(email, "EMPLOYEE", employee.getFullName(), null));
    }
}
