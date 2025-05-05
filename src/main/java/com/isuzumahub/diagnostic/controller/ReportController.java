package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    @GetMapping
    public ResponseEntity<?> getReports() {
        // Placeholder: Implement report retrieval logic
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ErrorResponse("Report retrieval not implemented yet."));
    }
}