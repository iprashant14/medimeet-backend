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

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Load the user from the database (MongoDB in this case)
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		// Convert the User entity to a UserPrincipal
		return UserPrincipalMapper.build(user);
	}

	public UserDetails loadUserById(String id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return UserPrincipalMapper.build(user);
	}
}
