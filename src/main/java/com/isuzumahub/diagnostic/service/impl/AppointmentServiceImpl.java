package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.dto.AppointmentDto;
import com.isuzumahub.diagnostic.exception.BadRequestException;
import com.isuzumahub.diagnostic.exception.ResourceNotFoundException;
import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.AppointmentStatus;
import com.isuzumahub.diagnostic.repository.AppointmentRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import com.isuzumahub.diagnostic.service.AppointmentService;
import com.isuzumahub.diagnostic.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        // Validate patient exists
        User patient = userRepository.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", appointmentDto.getPatientId()));

        // Generate unique appointment number
        String appointmentNumber = generateAppointmentNumber();
        
        Appointment appointment = modelMapper.toAppointment(appointmentDto);
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPaid(false);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return modelMapper.toAppointmentDto(savedAppointment);
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return modelMapper.toAppointmentDto(appointment);
    }

    @Override
    public AppointmentDto getAppointmentByNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "appointmentNumber", appointmentNumber));
        return modelMapper.toAppointmentDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(modelMapper::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(modelMapper::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId) {
        return appointmentRepository.findByAssignedEmployeeId(employeeId).stream()
                .map(modelMapper::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(modelMapper::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByAppointmentDateTimeBetween(startDate, endDate).stream()
                .map(modelMapper::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        // Validate patient exists if being updated
        if (appointmentDto.getPatientId() != null) {
            User patient = userRepository.findById(appointmentDto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", appointmentDto.getPatientId()));
            existingAppointment.setPatient(patient);
        }

        // Update other fields
        if (appointmentDto.getAppointmentDateTime() != null) {
            existingAppointment.setAppointmentDateTime(appointmentDto.getAppointmentDateTime());
        }
        if (appointmentDto.getCollectionAddress() != null) {
            existingAppointment.setCollectionAddress(appointmentDto.getCollectionAddress());
        }
        if (appointmentDto.getRemarks() != null) {
            existingAppointment.setRemarks(appointmentDto.getRemarks());
        }
        if (appointmentDto.getAmount() != null) {
            existingAppointment.setAmount(appointmentDto.getAmount());
        }

        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        return modelMapper.toAppointmentDto(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return modelMapper.toAppointmentDto(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto assignEmployee(Long id, Long employeeId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employeeId));
        
        appointment.setAssignedEmployee(employee);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return modelMapper.toAppointmentDto(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto uploadReport(Long id, String reportFilePath) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        appointment.setReportFilePath(reportFilePath);
        appointment.setReportUploadedAt(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return modelMapper.toAppointmentDto(updatedAppointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment", "id", id);
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed appointment");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setRemarks(reason);
        appointmentRepository.save(appointment);
    }

    private String generateAppointmentNumber() {
        return "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 