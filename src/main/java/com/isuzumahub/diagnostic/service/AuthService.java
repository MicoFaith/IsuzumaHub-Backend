package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.AdminRepository;
import com.isuzumahub.diagnostic.repository.EmployeeRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public boolean authenticate(String email, String password) {
        System.out.println("[DEBUG] Authenticating user with email: " + email);
        Object user = findUserByEmail(email);
        if (user == null) {
            System.out.println("[DEBUG] User not found for email: " + email);
            return false;
        }

        String storedPassword = user instanceof User ? ((User) user).getPassword() :
                user instanceof Admin ? ((Admin) user).getPassword() :
                        ((Employee) user).getPassword();
        System.out.println("[DEBUG] Stored encoded password: " + storedPassword);
        System.out.println("[DEBUG] Provided raw password: " + password);
        boolean matches = passwordEncoder.matches(password, storedPassword);
        System.out.println("[DEBUG] Password match: " + matches);
        return matches;
    }

    public Object findUserByEmail(String email) {
        System.out.println("[DEBUG] Finding user by email: " + email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            System.out.println("[DEBUG] Found User: " + user.getEmail());
            return user;
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            System.out.println("[DEBUG] Found Admin: " + admin.getEmail());
            return admin;
        }

        Employee employee = employeeRepository.findByEmail(email).orElse(null);
        if (employee != null) {
            System.out.println("[DEBUG] Found Employee: " + employee.getEmail());
            return employee;
        }

        System.out.println("[DEBUG] No user found for email: " + email);
        return null;
    }

    public void saveUser(User user) {
        System.out.println("[DEBUG] Saving user: " + user.getEmail());
        userRepository.save(user);
    }

    public void saveAdmin(Admin admin) {
        System.out.println("[DEBUG] Saving admin: " + admin.getEmail());
        adminRepository.save(admin);
    }

    public void saveEmployee(Employee employee) {
        System.out.println("[DEBUG] Saving employee: " + employee.getEmail());
        employeeRepository.save(employee);
    }

    public boolean checkUser(String email) {
        System.out.println("[DEBUG] Checking user by email: " + email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            System.out.println("[DEBUG] Found User: " + user.getEmail());
            return true;
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            System.out.println("[DEBUG] Found Admin: " + admin.getEmail());
            return true;
        }

        Employee employee = employeeRepository.findByEmail(email).orElse(null);
        if (employee != null) {
            System.out.println("[DEBUG] Found Employee: " + employee.getEmail());
            return true;
        }

        System.out.println("[DEBUG] No user found for email: " + email);
        return false;
    }
}