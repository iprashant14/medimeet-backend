package com.medimeet.app.security;

import com.medimeet.app.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

public class UserPrincipalMapper {
	public static UserPrincipal build(User user) {
		return new UserPrincipal(
				user.getId(),
				user.getUsername(),
				user.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Add roles/authorities if required
		);
	}
}
