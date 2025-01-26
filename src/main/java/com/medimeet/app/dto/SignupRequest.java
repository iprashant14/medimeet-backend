package com.medimeet.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	@Email
	private String email;
}
