package net.scales.service;

import net.scales.model.CustomUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
	
	void updateUserDetails(CustomUser cu) throws UsernameNotFoundException;
}
