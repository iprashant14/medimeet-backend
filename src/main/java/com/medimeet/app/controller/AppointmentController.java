package com.medimeet.app.controller;

import com.medimeet.app.dto.AppointmentRequest;
import com.medimeet.app.model.Appointment;
import com.medimeet.app.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getUserAppointments(@PathVariable String userId) {
        logger.info("Fetching appointments for user: {}", userId);
        List<Appointment> appointments = appointmentService.getUserAppointments(userId);
        logger.info("Found {} appointments for user: {}", appointments.size(), userId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable String appointmentId) {
        logger.info("Fetching appointment: {}", appointmentId);
        Appointment appointment = appointmentService.getAppointment(appointmentId);
        logger.info("Found appointment: {}", appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        logger.info("Creating appointment for user: {} with doctor: {}", 
            request.getUserId(), request.getDoctorId());
        
        Appointment appointment = appointmentService.scheduleAppointment(
            request.getUserId(),
            request.getDoctorId(),
            request.getAppointmentTime()
        );
        
        logger.info("Created appointment: {}", appointment.getId());
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable String appointmentId) {
        logger.info("Cancelling appointment: {}", appointmentId);
        Appointment cancelledAppointment = appointmentService.cancelAppointment(appointmentId);
        logger.info("Cancelled appointment: {}", appointmentId);
        return ResponseEntity.ok(cancelledAppointment);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> cancelAppointmentDelete(@PathVariable String appointmentId) {
        logger.info("Cancelling appointment: {}", appointmentId);
        appointmentService.cancelAppointment(appointmentId);
        logger.info("Cancelled appointment: {}", appointmentId);
        return ResponseEntity.ok().build();
    }
}
