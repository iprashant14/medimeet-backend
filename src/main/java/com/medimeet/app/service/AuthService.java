package com.medimeet.app.service;

import com.medimeet.app.dto.AuthResponse;
import com.medimeet.app.dto.LoginRequest;
import com.medimeet.app.dto.SignupRequest;
import com.medimeet.app.model.User;
import com.medimeet.app.repository.UserRepository;
import com.medimeet.app.security.JwtTokenProvider;
import com.medimeet.app.security.UserPrincipalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse registerUser(SignupRequest signupRequest) {
        logger.info("Processing signup request for user: {}", signupRequest.getEmail());
        
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            logger.warn("Username {} is already registered", signupRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            logger.warn("Email {} is already registered", signupRequest.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());

        User savedUser = userRepository.save(user);
        logger.info("Successfully created new user with ID: {}", savedUser.getId());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        savedUser.getUsername(),
                        signupRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        logger.debug("Generated authentication token for user: {}", savedUser.getId());

        return new AuthResponse(savedUser.getId(), accessToken, refreshToken);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Processing login request for user with email: {}", loginRequest.getEmail());
        
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),  // Still use username for Spring Security
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            logger.info("Successfully authenticated user: {}", user.getId());
            logger.debug("Generated authentication token for user: {}", user.getId());

            return new AuthResponse(user.getId(), accessToken, refreshToken);
        } catch (Exception e) {
            logger.error("Authentication failed for user with email: {}", loginRequest.getEmail(), e);
            throw new RuntimeException("Invalid email or password");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing token for user");
        
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            logger.warn("Invalid refresh token");
            throw new RuntimeException("Invalid refresh token");
        }

        String userId = tokenProvider.getUserIdFromToken(refreshToken, false);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserPrincipalMapper.build(user), null, Collections.emptyList()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
        logger.debug("Generated new authentication token for user: {}", user.getId());

        return new AuthResponse(user.getId(), newAccessToken, newRefreshToken);
    }

    public boolean validateToken(String token) {
        logger.debug("Validating token");
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return tokenProvider.validateAccessToken(token);
        } catch (Exception e) {
            logger.warn("Token validation failed", e);
            return false;
        }
    }
}