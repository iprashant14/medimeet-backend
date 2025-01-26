package com.medimeet.app.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentRequest {
	@NotNull(message = "User ID is required")
	private String userId;

	@NotNull(message = "Doctor ID is required")
	private String doctorId;

	@Future(message = "Appointment time must be in the future")
	@NotNull(message = "Appointment time is required")
	private LocalDateTime appointmentTime;

	// Constructors
	public AppointmentRequest() {}

	public AppointmentRequest(String userId, String doctorId, LocalDateTime appointmentTime) {
		this.userId = userId;
		this.doctorId = doctorId;
		this.appointmentTime = appointmentTime;
	}

	// Getters and Setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
}