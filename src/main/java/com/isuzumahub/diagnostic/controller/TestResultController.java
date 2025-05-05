package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.model.TestResult;
import com.isuzumahub.diagnostic.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test-results")
@CrossOrigin(origins = "http://localhost:3000")
public class TestResultController {

    @Autowired
    private TestResultRepository testResultRepository;

    @GetMapping
    public ResponseEntity<?> getAllTestResults() {
        try {
            List<TestResult> testResults = testResultRepository.findAll();
            return ResponseEntity.ok(testResults);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve test results: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestResultById(@PathVariable Long id) {
        try {
            Optional<TestResult> testResult = testResultRepository.findById(id);
            if (testResult.isPresent()) {
                return ResponseEntity.ok(testResult.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Test result not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve test result details: " + e.getMessage()));
        }
    }
}