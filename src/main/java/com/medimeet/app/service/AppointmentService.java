package com.medimeet.app.service;

import com.medimeet.app.exception.ResourceNotFoundException;
import com.medimeet.app.model.Appointment;
import com.medimeet.app.model.Doctor;
import com.medimeet.app.model.User;
import com.medimeet.app.repository.AppointmentRepository;
import com.medimeet.app.repository.DoctorRepository;
import com.medimeet.app.repository.UserRepository;
import com.medimeet.app.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    private User findUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new ResourceNotFoundException("User not found");
            });
    }

    private void validateUserAccess(String userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            String authenticatedUserId = userPrincipal.getId();
            logger.debug("Comparing authenticated user ID: {} with requested user ID: {}", 
                authenticatedUserId, userId);
            
            // First check if user exists
            findUserById(userId);
            
            if (!authenticatedUserId.equals(userId)) {
                logger.error("Access denied: User {} attempted to access appointments of user {}", 
                    authenticatedUserId, userId);
                throw new AccessDeniedException("You are not authorized to access these appointments");
            }
        } else {
            logger.error("No authenticated user found in SecurityContext");
            throw new AccessDeniedException("Authentication required");
        }
    }

    public Appointment scheduleAppointment(String userId, String doctorId, LocalDateTime time) {
        // First validate user exists and has access
        User user = findUserById(userId);
        validateUserAccess(userId);
        
        logger.info("Scheduling appointment for user: {} with doctor: {}", userId, doctorId);
        
        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setDoctorId(doctorId);
        appointment.setDoctorName(doctor.getName());
        appointment.setDoctorSpecialty(doctor.getSpecialty());
        appointment.setAppointmentTime(time);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Successfully scheduled appointment with ID: {}", savedAppointment.getId());
        
        return savedAppointment;
    }

    public List<Appointment> getUserAppointments(String userId) {
        // First validate user exists and has access
        User user = findUserById(userId);
        validateUserAccess(userId);
        
        logger.info("Fetching appointments for user: {}", userId);
        
        // Get appointments sorted by time
        List<Appointment> appointments = appointmentRepository.findByUserIdOrderByAppointmentTimeDesc(userId);
        
        // Fetch and set doctor details for each appointment
        appointments = appointments.stream()
            .map(appointment -> {
                Doctor doctor = doctorRepository.findById(appointment.getDoctorId())
                    .orElse(null);
                if (doctor != null) {
                    appointment.setDoctorName(doctor.getName());
                    appointment.setDoctorSpecialty(doctor.getSpecialty());
                }
                return appointment;
            })
            .collect(Collectors.toList());
            
        logger.debug("Found {} appointments for user: {}", appointments.size(), userId);
        return appointments;
    }

    public List<Appointment> getUpcomingAppointments(String userId) {
        validateUserAccess(userId);
        logger.info("Fetching upcoming appointments for user: {}", userId);
        
        return appointmentRepository.findByUserIdAndAppointmentTimeGreaterThanEqual(
            userId,
            LocalDateTime.now()
        );
    }

    public List<Appointment> getPastAppointments(String userId) {
        validateUserAccess(userId);
        logger.info("Fetching past appointments for user: {}", userId);
        
        return appointmentRepository.findByUserIdAndAppointmentTimeLessThan(
            userId,
            LocalDateTime.now()
        );
    }

    public Appointment cancelAppointment(String appointmentId) {
        logger.info("Cancelling appointment: {}", appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment not found with ID: {}", appointmentId);
                    return new ResourceNotFoundException("Appointment not found");
                });

        // Validate user exists and has access
        User user = findUserById(appointment.getUserId());
        validateUserAccess(appointment.getUserId());
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        logger.info("Successfully cancelled appointment: {}", appointmentId);
        
        return cancelledAppointment;
    }
}
