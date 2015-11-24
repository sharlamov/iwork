package com.reporting.dao;

import com.reporting.model.CustomItem;
import com.reporting.model.CustomUser;
import org.hibernate.HibernateException;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface UserDAO {

    Object loadUserByUsername(String username);

    void initUserParams(String userName, String password) throws HibernateException, SQLException;

    CustomItem getUserDiv(BigDecimal userId);

    CustomItem getUserElevator(BigDecimal userId);

    int getUserScaleType(BigDecimal userId);

    String getUserRole(BigDecimal userId);

    void initContext(CustomUser user);
}