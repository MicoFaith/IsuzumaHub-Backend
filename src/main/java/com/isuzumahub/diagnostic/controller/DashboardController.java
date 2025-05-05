package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.DashboardStatsDTO;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.TestResultRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @GetMapping
    public ResponseEntity<?> getDashboardStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Gather statistics
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalReportsUploaded(testResultRepository.countByPatient(user));

        return ResponseEntity.ok(stats);
    }
}