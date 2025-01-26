package com.medimeet.app.service;

import com.medimeet.app.exception.ResourceNotFoundException;
import com.medimeet.app.model.Appointment;
import com.medimeet.app.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment scheduleAppointment(String userId, String doctorId, LocalDateTime time) {
        logger.info("Scheduling appointment for user: {} with doctor: {}", userId, doctorId);
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setDoctorId(doctorId);
        appointment.setAppointmentTime(time);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Successfully scheduled appointment with ID: {}", savedAppointment.getId());
        
        return savedAppointment;
    }

    public List<Appointment> getUserAppointments(String userId) {
        logger.info("Fetching appointments for user: {}", userId);
        List<Appointment> appointments = appointmentRepository.findByUserIdAndStatus(
                userId,
                Appointment.AppointmentStatus.SCHEDULED
        );
        logger.debug("Found {} appointments for user: {}", appointments.size(), userId);
        return appointments;
    }

    public Appointment cancelAppointment(String appointmentId) {
        logger.info("Cancelling appointment: {}", appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment not found with ID: {}", appointmentId);
                    return new ResourceNotFoundException("Appointment not found");
                });

        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        logger.info("Successfully cancelled appointment: {}", appointmentId);
        
        return cancelledAppointment;
    }
}
