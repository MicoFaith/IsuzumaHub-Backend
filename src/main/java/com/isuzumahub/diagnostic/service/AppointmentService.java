package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.model.Appointment;
import com.isuzumahub.diagnostic.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment bookAppointment(Appointment appointment) {
        appointment.setAppointmentNumber(generateAppointmentNumber());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setStatus(Appointment.AppointmentStatus.NEW);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getNewAppointments() {
        return appointmentRepository.findByStatus(Appointment.AppointmentStatus.NEW);
    }

    private String generateAppointmentNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(90000000) + 10000000);
    }
}