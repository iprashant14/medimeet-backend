package com.medimeet.app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collection = "users")
public class User {
	@Id
	private String id;

	@Indexed(unique = true)
	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	private String password;

	@Indexed(unique = true)
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	private String role;
	private AuthProvider authProvider = AuthProvider.LOCAL;
	private String providerId;

	public enum AuthProvider {
		LOCAL,
		GOOGLE
	}

	public AuthProvider getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthProvider authProvider) {
		this.authProvider = authProvider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
}