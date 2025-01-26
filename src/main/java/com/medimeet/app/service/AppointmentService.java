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

/**
 * Service class for managing appointments.
 */
@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    // Find a user by ID, throwing an exception if not found
    private User findUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new ResourceNotFoundException("User not found");
            });
    }

    // Validate user access by checking authentication and user ID
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

    /**
     * Schedule a new appointment after validating user and doctor.
     * 
     * @param userId User ID of the patient
     * @param doctorId ID of the doctor
     * @param time Appointment time
     * @return The scheduled appointment
     */
    public Appointment scheduleAppointment(String userId, String doctorId, LocalDateTime time) {
        // Verify user exists and has access
        validateUserAccess(userId);
        User user = findUserById(userId);
        
        logger.info("Scheduling appointment for user: {} with doctor: {}", userId, doctorId);
        
        // Find and verify doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        // Create and save the appointment
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

    /**
     * Get upcoming appointments for a user.
     * 
     * @param userId User ID of the patient
     * @return List of upcoming appointments
     */
    public List<Appointment> getUpcomingAppointments(String userId) {
        validateUserAccess(userId);
        logger.info("Fetching upcoming appointments for user: {}", userId);
        
        return appointmentRepository.findByUserIdAndAppointmentTimeGreaterThanEqual(
            userId, LocalDateTime.now()
        );
    }

    /**
     * Get past appointments for a user.
     * 
     * @param userId User ID of the patient
     * @return List of past appointments
     */
    public List<Appointment> getPastAppointments(String userId) {
        validateUserAccess(userId);
        logger.info("Fetching past appointments for user: {}", userId);
        
        return appointmentRepository.findByUserIdAndAppointmentTimeLessThan(
            userId, LocalDateTime.now()
        );
    }

    /**
     * Cancel an existing appointment.
     * 
     * @param appointmentId ID of the appointment to cancel
     * @return The cancelled appointment
     */
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

    /**
     * Get appointment details by ID.
     * 
     * @param appointmentId ID of the appointment
     * @return The appointment details
     */
    public Appointment getAppointment(String appointmentId) {
        logger.info("Fetching appointment details for ID: {}", appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment not found with ID: {}", appointmentId);
                    return new ResourceNotFoundException("Appointment not found");
                });

        // Validate user exists and has access
        validateUserAccess(appointment.getUserId());
        
        // Fetch and set doctor details
        Doctor doctor = doctorRepository.findById(appointment.getDoctorId())
                .orElse(null);
        if (doctor != null) {
            appointment.setDoctorName(doctor.getName());
            appointment.setDoctorSpecialty(doctor.getSpecialty());
        }
        
        logger.info("Successfully fetched appointment details for ID: {}", appointmentId);
        return appointment;
    }

    /**
     * Get all appointments for a user.
     * 
     * @param userId User ID of the patient
     * @return List of appointments
     */
    public List<Appointment> getUserAppointments(String userId) {
        // First validate user exists and has access
        User user = findUserById(userId);
        validateUserAccess(userId);
        
        logger.info("Fetching appointments for user: {}", userId);
        
        return appointmentRepository.findByUserIdOrderByAppointmentTimeDesc(userId);
    }
}
