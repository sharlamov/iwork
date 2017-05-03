package com.orders.services;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.orders.dao.QueryFactory;
import com.orders.model.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CommonOrderServiceImpl implements CommonOrderService {

    @Autowired
    private QueryFactory factory;

    @Override
    public UserDetails loadUserByUsername(String arg0)
            throws UsernameNotFoundException {

        String userSql = "select id, username,nvl(admin,0) as admin ,nvl(encoded,a$util.encode(password)) as pass  from a$users$v a where enabled=1 and LOWER(username) = LOWER(:user_name)";

        CustomUser userDetails = new CustomUser();
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (!arg0.isEmpty()) {

            try {
                DataSet userCredentials = factory.exec(userSql, arg0);
                if (!userCredentials.isEmpty()) {
                    userDetails.setId(userCredentials.getDecimal("id"));
                    userDetails.setUsername(userCredentials.getString("username"));
                    userDetails.setAdminLevel(userCredentials.getInt("admin"));
                    userDetails.setPassword(userCredentials.getString("pass"));
                    userDetails.setCustomerAuthorities(roles);

                    String sId = userDetails.getId().toString();
                    DataSet params = factory.exec("select prop from (" +
                            "select trim((select value from a$adp$v p where key = :prop and obj_id = a.obj_id)) prop " +
                            "from a$adm a connect by obj_id = prior parent_id start with obj_id = (select obj_id from a$adp$v p where key='ID' and value=:userId) order by level\t\n" +
                            ")where prop is not null and rownum = 1", "DEFDIV", sId);

                    userDetails.setDiv(new CustomItem(params.getObject("prop"), null));

                    //String email = factory.execForValue("select max(value) from a$adp$v p where key = :prop and obj_id in (select obj_id from a$adp$v p where key = 'ID' and value =:userId)", String.class, "EMAIL", sId);
                    //userDetails.setEmail(email);
                    //System.out.println("Email: " + email);

                    userDetails.setDivFilter("select div_id cod, clcdiv_idt clccodt from vms_order_fin_resp where user_id = " + userDetails.getId());


                    return userDetails;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
