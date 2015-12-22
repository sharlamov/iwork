package com.dao;

import com.model.Labor;
import org.hibernate.HibernateException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface LaborDAO {

	List getLaborsIn(Date date, Long elevatorId);
	
	List<Labor> getLaborsOut(Date date, Long elevatorId);

	void updateLabor(Labor data, Labor oldData) throws HibernateException, SQLException;

	void insertLaborIn(Labor data) throws HibernateException, SQLException;

	void insertLaborOut(Labor data) throws HibernateException, SQLException;
}