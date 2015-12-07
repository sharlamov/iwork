package com.reporting.service;

import com.reporting.dao.UserDAO;
import com.reporting.enums.WebRole;
import com.reporting.model.CustomItem;
import com.reporting.model.CustomUser;
import com.reporting.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserDAO userDAO;

    public UserDetails loadUserByUsername(String arg0)
            throws UsernameNotFoundException {

        CustomUser userDetails = new CustomUser();
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (!arg0.isEmpty()) {
            Object userCredentials = userDAO.loadUserByUsername(arg0);
            if (userCredentials != null) {
                int i = 0;
                Object[] data = (Object[]) userCredentials;
                userDetails.setId((BigDecimal) data[i++]);
                userDetails.setUsername(WebUtil.parse(data[i++], String.class));
                userDetails.setAdminLevel(Integer.parseInt(data[i++].toString()));
                userDetails.setPassword(WebUtil.parse(data[i], String.class));
                userDetails.setCustomerAuthorities(roles);
            }
        }
        return userDetails;
    }
}
