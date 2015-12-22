package com.service;

import com.model.CustomUser;
import com.model.Labor;
import org.hibernate.HibernateException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface LaborService {

    List<Labor> getLabors(boolean isIn, Date date, CustomUser user);

    void insertLabor(boolean isIn, Labor labor, CustomUser user) throws HibernateException, SQLException;

    void updateLabor(Labor labor, Labor oldlabor, CustomUser user) throws HibernateException, SQLException;
}
