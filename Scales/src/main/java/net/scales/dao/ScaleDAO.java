package net.scales.dao;

import net.scales.model.CustomUser;
import net.scales.model.Scale;
import org.hibernate.HibernateException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface ScaleDAO {

	List<Scale> getScalesOut(Date date, Date date2, CustomUser user);

	List<Scale> getScalesIn(Date date1, Date date2, CustomUser user);

	void updateScale(Scale data) throws HibernateException, SQLException;

	void insertScale(Scale data) throws HibernateException, SQLException;

	void updateScaleIn(Scale data) throws HibernateException,
			SQLException;

	void insertScaleIn(Scale data) throws HibernateException,
			SQLException;
}