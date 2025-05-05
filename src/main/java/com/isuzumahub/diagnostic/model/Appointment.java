package com.isuzumahub.diagnostic.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Column(nullable = false)
    private String patientName;

    private String gender;

    private String dateOfBirth;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String appointmentDate; // Store as string (dd-mm-yyyy)

    @Column(nullable = false)
    private String appointmentTime; // Store as string (hh:mm)

    @ElementCollection
    @CollectionTable(name = "appointment_tests", joinColumns = @JoinColumn(name = "appointment_id"))
    private List<Integer> selectedTests;

    private String prescriptionFileName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (appointmentNumber == null) {
            appointmentNumber = generateAppointmentNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateAppointmentNumber() {
        return "APT" + System.currentTimeMillis();
    }

    public void setCancelReason(String reason) {
        this.status = AppointmentStatus.CANCELLED;
        this.notes = reason;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public enum AppointmentStatus {
        NEW,
        APPROVED,
        REJECTED,
        CANCELLED,
        ON_WAY,
        COLLECTED,
        COMPLETED
    }
}