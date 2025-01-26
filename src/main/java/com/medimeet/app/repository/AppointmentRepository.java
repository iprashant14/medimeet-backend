package com.medimeet.app.repository;

import com.medimeet.app.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
	List<Appointment> findByUserIdAndStatus(
			String userId,
			Appointment.AppointmentStatus status
	);

	List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
			String doctorId,
			LocalDateTime start,
			LocalDateTime end
	);
}