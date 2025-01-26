package com.medimeet.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
	private String id;
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal(String id, String username, String password,
						 Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public String getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Update if account expiration logic is required
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // Update if account locking logic is required
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Update if credential expiration logic is required
	}

	@Override
	public boolean isEnabled() {
		return true; // Update if enabling/disabling users is required
	}
}
