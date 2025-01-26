package com.medimeet.app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Represents a medical appointment between a patient and a doctor
@Data
@Setter
@Getter
@Document(collection = "appointments")
public class Appointment {
	@Id
	private String id;
	private String userId;      // Patient's ID
	private String doctorId;    // Doctor's ID
	private String doctorName;
	private String doctorSpecialty;
	private LocalDateTime appointmentTime;

	private AppointmentStatus status; 

	// Appointment status with user-friendly string representation
	public enum AppointmentStatus {
		SCHEDULED,   // Initial state when appointment is booked
		CANCELED,    // When either party cancels
		COMPLETED;   // After appointment is done

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
}
