package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.AppointmentDto;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.service.AppointmentService;
import com.isuzumahub.diagnostic.service.FileStorageService;
import com.isuzumahub.diagnostic.service.UserService;
import com.isuzumahub.diagnostic.util.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) {
        User currentUser = userService.getCurrentUser();
        appointmentDto.setPatientId(currentUser.getId());
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/number/{appointmentNumber}")
    @PreAuthorize("hasAnyRole('PATIENT', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<AppointmentDto> getAppointmentByNumber(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(appointmentService.getAppointmentByNumber(appointmentNumber));
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(currentUser.getId()));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/report")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<byte[]> downloadMyReport(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean decompress) throws IOException {
        // Get the current user
        User currentUser = userService.getCurrentUser();
        
        // Get the appointment to verify ownership
        AppointmentDto appointment = appointmentService.getAppointmentById(id);
        
        // Verify that the appointment belongs to the current user
        if (!appointment.getPatientId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Check if the report exists
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
} 