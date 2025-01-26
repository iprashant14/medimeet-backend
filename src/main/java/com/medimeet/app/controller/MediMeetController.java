package com.medimeet.app.controller;

import com.medimeet.app.dto.AppointmentRequest;
import com.medimeet.app.dto.AuthResponse;
import com.medimeet.app.dto.LoginRequest;
import com.medimeet.app.dto.SignupRequest;
import com.medimeet.app.model.Appointment;
import com.medimeet.app.service.AppointmentService;
import com.medimeet.app.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MediMeetController {
    private static final Logger logger = LoggerFactory.getLogger(MediMeetController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Received signup request for user: {}", signupRequest.getEmail());
        AuthResponse response = authService.registerUser(signupRequest);
        logger.info("Successfully created user account for: {}", signupRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for user with email: {}", loginRequest.getEmail());
        AuthResponse response = authService.authenticateUser(loginRequest);
        logger.info("Successfully authenticated user with email: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> scheduleAppointment(@RequestBody AppointmentRequest request) {
        logger.info("Creating appointment for user: {}", request.getUserId());
        Appointment appointment = appointmentService.scheduleAppointment(request.getUserId(), request.getDoctorId(), request.getAppointmentTime());
        logger.info("Successfully created appointment with ID: {}", appointment.getId());
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/appointments/{userId}")
    public ResponseEntity<List<Appointment>> getUserAppointments(@PathVariable String userId) {
        logger.info("Fetching appointments for user: {}", userId);
        List<Appointment> appointments = appointmentService.getUserAppointments(userId);
        logger.debug("Found {} appointments for user: {}", appointments.size(), userId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable String appointmentId) {
        logger.info("Cancelling appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentService.cancelAppointment(appointmentId);
        logger.info("Successfully cancelled appointment with ID: {}", appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/auth/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        logger.info("Validating token: {}", token);
        Boolean isValid = authService.validateToken(token);
        logger.info("Token is valid: {}", isValid);
        return ResponseEntity.ok(isValid);
    }
}
