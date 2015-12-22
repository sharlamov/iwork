package com.service;

import com.dao.UserDAO;
import com.enums.WebRole;
import com.model.CustomItem;
import com.model.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    /*public UserDetails loadUserByUsername(String arg0)
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
    }*/


    public void updateUserDetails(CustomUser cu)  {
        CustomItem elevator = userDAO.getUserElevator(cu.getId());
/*
        if (elevator == null) {
            throw new UsernameNotFoundException("Elevator not found");
        }

        CustomItem div = userDAO.getUserDiv(cu.getId());
        if (div == null || div.getLabel() == null) {
            throw new UsernameNotFoundException("DIV not found");
        }

        Integer type = userDAO.getUserScaleType(cu.getId());
        String str = userDAO.getUserRole(cu.getId());
        if (!str.isEmpty()) {
            WebRole role = WebRole.valueOf(str);
            if (role != null)
                cu.setLevel(role);
            else {
                throw new UsernameNotFoundException("WebRole doesn't exist");
            }
        } else {
            throw new UsernameNotFoundException("WebRole not found");
        }

*/
        //cu.setScaleType(type);
        cu.setElevator(elevator);
        //cu.setDiv(div);
    }
}
