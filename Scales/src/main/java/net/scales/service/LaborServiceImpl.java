package net.scales.service;

import net.scales.dao.LaborDAO;
import net.scales.dao.UserDAO;
import net.scales.model.CustomUser;
import net.scales.model.Labor;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LaborServiceImpl implements LaborService {

    @Autowired
    private LaborDAO laborDAO;

    @Autowired
    private UserDAO userDAO;

    public List<Labor> getLabors(boolean isIn, Date date, CustomUser user) {
        userDAO.initContext(user);
        return isIn ? laborDAO.getLaborsIn(date, user.getElevator().getId().longValue()) : laborDAO.getLaborsOut(date, user.getElevator().getId().longValue());
    }

    public void insertLabor(boolean isIn, Labor labor, CustomUser user) throws HibernateException, SQLException {
        labor.setDiv(user.getDiv().getId());
        labor.setElevator(user.getElevator().getId());
        labor.setUserid(user.getId());
        userDAO.initContext(user);
        if (isIn) laborDAO.insertLaborIn(labor);
        else laborDAO.insertLaborOut(labor);
    }

    public void updateLabor(Labor labor, Labor oldlabor, CustomUser user) throws HibernateException, SQLException {
        userDAO.initContext(user);
        laborDAO.updateLabor(labor, oldlabor);
    }
}
