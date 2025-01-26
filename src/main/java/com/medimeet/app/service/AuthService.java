package com.medimeet.app.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.medimeet.app.dto.AuthResponse;
import com.medimeet.app.dto.GoogleAuthRequest;
import com.medimeet.app.dto.LoginRequest;
import com.medimeet.app.dto.SignupRequest;
import com.medimeet.app.model.User;
import com.medimeet.app.model.User.AuthProvider;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

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

    @Autowired
    private GoogleIdTokenVerifier googleTokenVerifier;

    public AuthResponse registerUser(SignupRequest signupRequest) {
        logger.info("Processing signup request for user: {}", signupRequest.getEmail());
        
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            logger.warn("Username {} is already registered", signupRequest.getUsername());
            throw new RuntimeException("This username is already taken. Please choose a different username.");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            logger.warn("Email {} is already registered", signupRequest.getEmail());
            throw new RuntimeException("An account with this email already exists. Please use a different email or try logging in.");
        }

        // Store the raw password for authentication after save
        String rawPassword = signupRequest.getPassword();
        
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        try {
            // Create authentication token with raw password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    signupRequest.getEmail(),  // Use email for authentication
                    rawPassword  // Use raw password for authentication
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            
            logger.info("Successfully authenticated new user: {}", savedUser.getId());
            
            return new AuthResponse(
                savedUser.getId(),
                jwt,
                refreshToken,
                savedUser.getUsername()
            );
        } catch (Exception e) {
            logger.error("Failed to authenticate user after signup: {}", e.getMessage());
            throw new RuntimeException("Account created but failed to auto-login. Please try logging in manually.");
        }
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Processing login request for user with email: {}", loginRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),  // Use email for authentication
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            logger.info("Successfully authenticated user: {}", user.getId());
            logger.debug("Generated authentication token for user: {}", user.getId());

            return new AuthResponse(
                user.getId(),
                accessToken,
                refreshToken,
                user.getUsername()
            );
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

        return new AuthResponse(user.getId(), newAccessToken, newRefreshToken, user.getUsername());
    }

    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        try {
            logger.info("Processing Google authentication for email: {}", request.getEmail());
            
            String email = request.getEmail();
            String token = request.getIdToken();
            boolean isIdToken = true;

            // If no ID token, try access token
            if (token == null || token.isEmpty()) {
                token = request.getAccessToken();
                isIdToken = false;
                if (token == null || token.isEmpty()) {
                    logger.error("No valid token provided");
                    throw new RuntimeException("No valid token provided");
                }
            }

            // Verify token and get payload
            GoogleIdToken.Payload payload;
            if (isIdToken) {
                GoogleIdToken idToken = googleTokenVerifier.verify(token);
                if (idToken == null) {
                    logger.error("Invalid Google ID token");
                    throw new RuntimeException("Invalid Google ID token");
                }
                payload = idToken.getPayload();
            } else {
                // For access token, we trust the email from the request since it came from Google Sign-In
                payload = new GoogleIdToken.Payload();
                payload.setEmail(email);
                payload.set("name", request.getName());
            }

            // Verify that the email matches
            if (!email.equals(payload.getEmail())) {
                logger.error("Email mismatch between token and request");
                throw new RuntimeException("Email verification failed");
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
                if (user.getAuthProvider() != AuthProvider.GOOGLE) {
                    logger.error("User {} is registered with {}", email, user.getAuthProvider());
                    throw new RuntimeException("Account exists with different auth provider: " + user.getAuthProvider());
                }
                updateExistingUser(user, payload);
            } else {
                user = createGoogleUser(payload);
            }

            // Create authentication token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserPrincipalMapper.build(user),
                null,
                Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT tokens
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            logger.info("Google authentication successful for user: {}", email);
            return new AuthResponse(
                user.getId(),
                accessToken,
                refreshToken,
                user.getUsername()
            );
        } catch (Exception e) {
            logger.error("Google authentication failed", e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    private User createGoogleUser(GoogleIdToken.Payload payload) {
        logger.info("Creating new user from Google account: {}", payload.getEmail());
        
        User user = new User();
        user.setEmail(payload.getEmail());
        user.setUsername(generateUsername(payload));
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderId(payload.getSubject());
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        
        return userRepository.save(user);
    }

    private void updateExistingUser(User user, GoogleIdToken.Payload payload) {
        logger.info("Updating existing Google user: {}", user.getEmail());
        
        user.setProviderId(payload.getSubject());
        userRepository.save(user);
    }

    private String generateUsername(GoogleIdToken.Payload payload) {
        String name = (String) payload.get("name");
        if (name == null) {
            name = payload.getEmail().split("@")[0];
        }
        
        // Remove spaces and special characters
        String baseUsername = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String username = baseUsername;
        int counter = 1;
        
        // Keep trying until we find a unique username
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }
        
        return username;
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString();
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