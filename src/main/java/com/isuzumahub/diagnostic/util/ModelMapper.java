package com.isuzumahub.diagnostic.util;

import com.isuzumahub.diagnostic.dto.AppointmentDto;
import com.isuzumahub.diagnostic.dto.LabTestDto;
import com.isuzumahub.diagnostic.dto.UserDto;
import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.model.LabTest;
import com.isuzumahub.diagnostic.model.User;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
    
    public User toUser(UserDto dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        
        return user;
    }
    
    public LabTestDto toLabTestDto(LabTest labTest) {
        if (labTest == null) return null;
        
        LabTestDto dto = new LabTestDto();
        dto.setId(labTest.getId());
        dto.setName(labTest.getName());
        dto.setDescription(labTest.getDescription());
        dto.setPrice(labTest.getPrice());
        dto.setCategory(labTest.getCategory());
        dto.setPreparationInstructions(labTest.getPreparationInstructions());
        dto.setReportDeliveryHours(labTest.getReportDeliveryHours());
        dto.setSampleType(labTest.getSampleType());
        dto.setHomeCollection(labTest.isHomeCollection());
        dto.setActive(labTest.isActive());
        
        return dto;
    }
    
    public LabTest toLabTest(LabTestDto dto) {
        if (dto == null) return null;
        
        LabTest labTest = new LabTest();
        labTest.setId(dto.getId());
        labTest.setName(dto.getName());
        labTest.setDescription(dto.getDescription());
        labTest.setPrice(dto.getPrice());
        labTest.setCategory(dto.getCategory());
        labTest.setPreparationInstructions(dto.getPreparationInstructions());
        labTest.setReportDeliveryHours(dto.getReportDeliveryHours());
        labTest.setSampleType(dto.getSampleType());
        labTest.setHomeCollection(dto.isHomeCollection());
        labTest.setActive(dto.isActive());
        
        return labTest;
    }
    
    public AppointmentDto toAppointmentDto(Appointment appointment) {
        if (appointment == null) return null;
        
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setAppointmentNumber(appointment.getAppointmentNumber());
        
        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatient(toUserDto(appointment.getPatient()));
        }
        
        if (appointment.getAssignedEmployee() != null) {
            dto.setAssignedEmployeeId(appointment.getAssignedEmployee().getId());
            dto.setAssignedEmployee(toUserDto(appointment.getAssignedEmployee()));
        }
        
        if (appointment.getTest() != null) {
            dto.setTestId(appointment.getTest().getId());
            dto.setTest(toLabTestDto(appointment.getTest()));
        }
        
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setCollectionAddress(appointment.getCollectionAddress());
        dto.setStatus(appointment.getStatus());
        dto.setRemarks(appointment.getRemarks());
        dto.setAmount(appointment.getAmount());
        dto.setPaid(appointment.isPaid());
        dto.setReportFilePath(appointment.getReportFilePath());
        dto.setReportUploadedAt(appointment.getReportUploadedAt());
        
        return dto;
    }
    
    public Appointment toAppointment(AppointmentDto dto) {
        if (dto == null) return null;
        
        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setAppointmentNumber(dto.getAppointmentNumber());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        appointment.setCollectionAddress(dto.getCollectionAddress());
        appointment.setStatus(dto.getStatus());
        appointment.setRemarks(dto.getRemarks());
        appointment.setAmount(dto.getAmount());
        appointment.setPaid(dto.isPaid());
        appointment.setReportFilePath(dto.getReportFilePath());
        appointment.setReportUploadedAt(dto.getReportUploadedAt());
        
        return appointment;
    }
} 