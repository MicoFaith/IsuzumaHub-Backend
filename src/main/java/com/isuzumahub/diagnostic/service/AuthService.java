package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.dto.SignUpRequest;
import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.AdminRepository;
import com.isuzumahub.diagnostic.repository.EmployeeRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signUp(SignUpRequest request) {
        // Validate input
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getMobileNumber() == null || request.getMobileNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        Set<String> roles = new HashSet<>();
        roles.add("USER"); // Default role
        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getMobileNumber(),
                passwordEncoder.encode(request.getPassword()),
                roles
        );

        // Save user to database
        return userRepository.save(user);
    }

    public Object findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user;
        }
        Admin admin = adminRepository.findById(email).orElse(null);
        if (admin != null) {
            return admin;
        }
        Employee employee = employeeRepository.findById(email).orElse(null);
        return employee;
    }

    public boolean authenticate(String email, String password) {
        Object user = findUserByEmail(email);
        if (user == null) {
            return false;
        }

        String storedPassword = user instanceof User ? ((User) user).getPassword() :
                user instanceof Admin ? ((Admin) user).getPassword() :
                        ((Employee) user).getPassword();
        return passwordEncoder.matches(password, storedPassword);
    }
}