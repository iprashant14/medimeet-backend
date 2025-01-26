package com.medimeet.app.service;

import com.medimeet.app.model.User;
import com.medimeet.app.repository.UserRepository;
import com.medimeet.app.security.UserPrincipalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by login: {}", login);
        
        // Try to find user by email first
        User user = userRepository.findByEmail(login)
                .orElseGet(() -> {
                    // If not found by email, try username
                    return userRepository.findByUsername(login)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
                });

        logger.debug("Found user: {} with email: {}", user.getUsername(), user.getEmail());
        return UserPrincipalMapper.build(user);
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserPrincipalMapper.build(user);
    }
}
