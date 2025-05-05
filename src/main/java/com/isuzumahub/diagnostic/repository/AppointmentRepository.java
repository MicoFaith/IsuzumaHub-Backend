package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);

    List<Appointment> findByEmail(String email);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(String status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient = :patient")
    long countByPatient(User patient);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient = :patient AND a.status = :status")
    long countByPatientAndStatus(User patient, String status);

    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
}