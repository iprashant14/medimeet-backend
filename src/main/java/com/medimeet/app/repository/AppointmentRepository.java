package com.medimeet.app.repository;

import com.medimeet.app.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    // Find all appointments for a user
    List<Appointment> findByUserId(String userId);

    // Find appointments by user ID and status
    List<Appointment> findByUserIdAndStatus(
            String userId,
            Appointment.AppointmentStatus status
    );

    // Find appointments for a doctor within a time range
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            String doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Find appointments by user ID ordered by appointment time
    List<Appointment> findByUserIdOrderByAppointmentTimeDesc(String userId);

    // Find upcoming appointments for a user
    List<Appointment> findByUserIdAndAppointmentTimeGreaterThanEqual(
            String userId,
            LocalDateTime now
    );

    // Find past appointments for a user
    List<Appointment> findByUserIdAndAppointmentTimeLessThan(
            String userId,
            LocalDateTime now
    );
}