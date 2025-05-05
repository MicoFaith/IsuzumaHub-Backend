package com.isuzumahub.diagnostic.controller;

import com.isuzumahub.diagnostic.dto.ErrorResponse;
import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.AppointmentRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import com.isuzumahub.diagnostic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User patient = userRepository.findByEmail(auth.getName()).get();

            appointment.setPatient(patient);
            appointment.setEmail(patient.getEmail());
            appointment.setStatus(Appointment.AppointmentStatus.NEW);
            Appointment savedAppointment = appointmentService.bookAppointment(appointment);
            return ResponseEntity.ok(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to book appointment: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyAppointments() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User patient = userRepository.findByEmail(auth.getName()).get();

            List<Appointment> appointments = appointmentRepository.findByPatient(patient);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve appointments: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentDetails(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User patient = userRepository.findByEmail(auth.getName()).get();

            Optional<Appointment> appointment = appointmentRepository.findById(id);
            if (appointment.isPresent()) {
                Appointment appt = appointment.get();
                if (appt.getPatient().getEmail().equals(patient.getEmail())) {
                    return ResponseEntity.ok(appt);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You are not authorized to view this appointment."));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Appointment not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve appointment details: " + e.getMessage()));
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody CancelRequest cancelRequest) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User patient = userRepository.findByEmail(auth.getName()).get();

            Optional<Appointment> appointment = appointmentRepository.findById(id);
            if (appointment.isPresent()) {
                Appointment appt = appointment.get();
                if (appt.getPatient().getEmail().equals(patient.getEmail())) {
                    appt.setStatus(Appointment.AppointmentStatus.CANCELLED);
                    appt.setCancelReason(cancelRequest.getReason());
                    appt.setUpdatedAt(LocalDateTime.now());
                    appointmentRepository.save(appt);
                    return ResponseEntity.ok(new SuccessResponse("Appointment cancelled successfully."));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You are not authorized to cancel this appointment."));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Appointment not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to cancel appointment: " + e.getMessage()));
        }
    }
}

class CancelRequest {
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

class SuccessResponse {
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}