package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.AppointmentDto;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.AppointmentStatus;
import com.isuzumahub.diagnostic.service.AppointmentService;
import com.isuzumahub.diagnostic.service.FileStorageService;
import com.isuzumahub.diagnostic.service.UserService;
import com.isuzumahub.diagnostic.util.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByEmployeeId(employeeId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentDto appointmentDto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, appointmentDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<AppointmentDto> assignEmployee(
            @PathVariable Long id,
            @RequestParam Long employeeId) {
        return ResponseEntity.ok(appointmentService.assignEmployee(id, employeeId));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<AppointmentDto> uploadReport(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile reportFile) throws IOException {
        // Store the file in the reports directory with compression
        String reportFilePath = fileStorageService.storeFile(reportFile, "reports", true);
        
        // Update the appointment with the report file path
        return ResponseEntity.ok(appointmentService.uploadReport(id, reportFilePath));
    }
    
    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean decompress) throws IOException {
        // Get the appointment to find the report file path
        AppointmentDto appointment = appointmentService.getAppointmentById(id);
        
        if (appointment.getReportFilePath() == null || appointment.getReportFilePath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Extract the file name from the path
        String filePath = appointment.getReportFilePath();
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String subDirectory = "reports";
        
        // Get the file as bytes
        byte[] fileData = fileStorageService.getFileAsBytes(fileName, subDirectory, decompress);
        
        // Determine the content type based on the file extension
        String contentType = FileUtils.determineContentType(fileName);
        
        // Return the file as a response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileName);
        
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-assigned")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<AppointmentDto>> getMyAssignedAppointments() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.getAppointmentsByEmployeeId(currentUser.getId()));
    }
} 