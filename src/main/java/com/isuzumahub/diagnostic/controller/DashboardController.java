package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class DashboardController {

    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getUserDashboard() {
        System.out.println("[DEBUG] Accessing user dashboard");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            System.out.println("[DEBUG] No authentication found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = authentication.getName();
        System.out.println("[DEBUG] User dashboard requested for email: " + email);
        Object user = authService.findUserByEmail(email);
        if (user == null) {
            System.out.println("[DEBUG] User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found"));
        }

        if (!(user instanceof User)) {
            System.out.println("[DEBUG] Invalid user type for /dashboard: " + user.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("This endpoint is for users only"));
        }

        User userObj = (User) user;
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("email", userObj.getEmail());
        dashboardData.put("fullName", userObj.getFullName());
        dashboardData.put("role", "USER");
        dashboardData.put("appointments", new String[]{"Appointment 1", "Appointment 2"}); // Placeholder
        dashboardData.put("medicalReports", new String[]{"Report 1", "Report 2"}); // Placeholder

        System.out.println("[DEBUG] User dashboard data prepared for: " + email);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/dashboard/admin")
    public ResponseEntity<?> getAdminDashboard() {
        System.out.println("[DEBUG] Accessing admin dashboard");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            System.out.println("[DEBUG] No authentication found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = authentication.getName();
        System.out.println("[DEBUG] Admin dashboard requested for email: " + email);
        Object user = authService.findUserByEmail(email);
        if (user == null) {
            System.out.println("[DEBUG] Admin not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Admin not found"));
        }

        if (!(user instanceof Admin)) {
            System.out.println("[DEBUG] Invalid user type for /dashboard/admin: " + user.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("This endpoint is for admins only"));
        }

        Admin admin = (Admin) user;
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("email", admin.getEmail());
        dashboardData.put("fullName", admin.getFullName());
        dashboardData.put("role", "ADMIN");
        dashboardData.put("managedUsers", new String[]{"User 1", "User 2"}); // Placeholder

        System.out.println("[DEBUG] Admin dashboard data prepared for: " + email);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/dashboard/employee")
    public ResponseEntity<?> getEmployeeDashboard() {
        System.out.println("[DEBUG] Accessing employee dashboard");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            System.out.println("[DEBUG] No authentication found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated"));
        }

        String email = authentication.getName();
        System.out.println("[DEBUG] Employee dashboard requested for email: " + email);
        Object user = authService.findUserByEmail(email);
        if (user == null) {
            System.out.println("[DEBUG] Employee not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Employee not found"));
        }

        if (!(user instanceof Employee)) {
            System.out.println("[DEBUG] Invalid user type for /dashboard/employee: " + user.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("This endpoint is for employees only"));
        }

        Employee employee = (Employee) user;
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("email", employee.getEmail());
        dashboardData.put("fullName", employee.getFullName());
        dashboardData.put("role", "EMPLOYEE");
        dashboardData.put("assignedTasks", new String[]{"Task 1", "Task 2"}); // Placeholder

        System.out.println("[DEBUG] Employee dashboard data prepared for: " + email);
        return ResponseEntity.ok(dashboardData);
    }
}