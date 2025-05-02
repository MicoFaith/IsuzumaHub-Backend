package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.dto.LoginRequest;
import com.isuzumahub.diagnostic.dto.SignUpRequest;
import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

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

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (authService.authenticate(email, password)) {
            Object user = authService.findUserByEmail(email);
            String role;
            String fullName;

            if (user instanceof User) {
                role = "USER";
                fullName = ((User) user).getFullName();
            } else if (user instanceof Admin) {
                role = "ADMIN";
                fullName = ((Admin) user).getFullName();
            } else {
                role = "EMPLOYEE";
                fullName = ((Employee) user).getFullName();
            }

            if (role != "USER") {
                return ResponseEntity.status(403).body(new ErrorResponse("Use the appropriate login page for admins or employees."));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("fullName", fullName);
            responseData.put("role", role);

            return ResponseEntity.ok(responseData);
        } else {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }
    }

    @PostMapping("/auth/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (authService.authenticate(email, password)) {
            Object user = authService.findUserByEmail(email);
            String role;
            String fullName;

            if (user instanceof Admin) {
                role = "ADMIN";
                fullName = ((Admin) user).getFullName();
            } else {
                return ResponseEntity.status(403).body(new ErrorResponse("This login is for admins only."));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("fullName", fullName);
            responseData.put("role", role);

            return ResponseEntity.ok(responseData);
        } else {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }
    }

    @PostMapping("/auth/employee-login")
    public ResponseEntity<?> employeeLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (authService.authenticate(email, password)) {
            Object user = authService.findUserByEmail(email);
            String role;
            String fullName;

            if (user instanceof Employee) {
                role = "EMPLOYEE";
                fullName = ((Employee) user).getFullName();
            } else {
                return ResponseEntity.status(403).body(new ErrorResponse("This login is for employees only."));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("fullName", fullName);
            responseData.put("role", role);

            return ResponseEntity.ok(responseData);
        } else {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpServletRequest request) {
        String email = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (email == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Unauthorized"));
        }

        Object user = authService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("User not found"));
        }

        if (!(user instanceof User)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied: This dashboard is for normal users only."));
        }

        Map<String, Object> userData = new HashMap<>();
        User normalUser = (User) user;
        userData.put("fullName", normalUser.getFullName());
        userData.put("email", normalUser.getEmail());
        userData.put("role", "USER");

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/dashboard/admin")
    public ResponseEntity<?> adminDashboard(HttpServletRequest request) {
        String email = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (email == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Unauthorized"));
        }

        Object user = authService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("User not found"));
        }

        if (!(user instanceof Admin)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied: This dashboard is for admins only."));
        }

        Map<String, Object> userData = new HashMap<>();
        Admin admin = (Admin) user;
        userData.put("fullName", admin.getFullName());
        userData.put("email", admin.getEmail());
        userData.put("role", "ADMIN");

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/dashboard/employee")
    public ResponseEntity<?> employeeDashboard(HttpServletRequest request) {
        String email = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (email == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Unauthorized"));
        }

        Object user = authService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("User not found"));
        }

        if (!(user instanceof Employee)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied: This dashboard is for employees only."));
        }

        Map<String, Object> userData = new HashMap<>();
        Employee employee = (Employee) user;
        userData.put("fullName", employee.getFullName());
        userData.put("email", employee.getEmail());
        userData.put("role", "EMPLOYEE");

        return ResponseEntity.ok(userData);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(new ErrorResponse("Password reset functionality not yet implemented"));
    }
}