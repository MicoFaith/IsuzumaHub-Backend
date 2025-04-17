package com.isuzumahub.diagnostic.dto;

import com.isuzumahub.diagnostic.model.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private String appointmentNumber;
    
    private Long patientId;
    private UserDto patient;
    
    private Long assignedEmployeeId;
    private UserDto assignedEmployee;
    
    @NotNull(message = "Test ID is required")
    private Long testId;
    private LabTestDto test;
    
    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment date/time must be in the future")
    private LocalDateTime appointmentDateTime;
    
    @NotBlank(message = "Collection address is required")
    private String collectionAddress;
    
    private AppointmentStatus status;
    private String remarks;
    private BigDecimal amount;
    private boolean paid;
    
    private String reportFilePath;
    private LocalDateTime reportUploadedAt;
} 