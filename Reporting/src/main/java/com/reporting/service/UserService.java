package com.reporting.service;

import com.reporting.model.CustomUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    void updateUserDetails(CustomUser cu) throws UsernameNotFoundException;
}
