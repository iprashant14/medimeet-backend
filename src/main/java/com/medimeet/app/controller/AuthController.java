package com.medimeet.app.controller;

import com.medimeet.app.dto.AuthResponse;
import com.medimeet.app.dto.GoogleAuthRequest;
import com.medimeet.app.dto.LoginRequest;
import com.medimeet.app.dto.SignupRequest;
import com.medimeet.app.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Received signup request for user: {}", signupRequest.getEmail());
        return ResponseEntity.ok(authService.registerUser(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        logger.info("Received Google authentication request");
        return ResponseEntity.ok(authService.authenticateWithGoogle(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        logger.info("Received token refresh request");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
