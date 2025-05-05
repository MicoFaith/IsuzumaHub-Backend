package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.model.Test;
import com.isuzumahub.diagnostic.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping
    public ResponseEntity<?> getAllTests() {
        try {
            List<Test> tests = testRepository.findAll();
            return ResponseEntity.ok(tests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve tests: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestById(@PathVariable Long id) {
        try {
            Optional<Test> test = testRepository.findById(id);
            if (test.isPresent()) {
                return ResponseEntity.ok(test.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Test not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve test details: " + e.getMessage()));
        }
    }
}