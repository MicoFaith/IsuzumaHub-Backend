package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    
    List<Appointment> findByPatient(User patient);
    
    List<Appointment> findByAssignedEmployee(User employee);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);
    
    List<Appointment> findByAssignedEmployeeAndStatus(User employee, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN ?1 AND ?2")
    List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "(LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(a.patient.phone) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(a.appointmentNumber) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Appointment> searchAppointments(String searchTerm);

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByAssignedEmployeeId(Long employeeId);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
} 