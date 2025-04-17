package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.dto.AppointmentDto;
import com.isuzumahub.diagnostic.model.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentDto createAppointment(AppointmentDto appointmentDto);
    AppointmentDto getAppointmentById(Long id);
    AppointmentDto getAppointmentByNumber(String appointmentNumber);
    List<AppointmentDto> getAllAppointments();
    List<AppointmentDto> getAppointmentsByPatientId(Long patientId);
    List<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId);
    List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status);
    List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto);
    AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status);
    AppointmentDto assignEmployee(Long id, Long employeeId);
    AppointmentDto uploadReport(Long id, String reportFilePath);
    void deleteAppointment(Long id);
    void cancelAppointment(Long id, String reason);
} 