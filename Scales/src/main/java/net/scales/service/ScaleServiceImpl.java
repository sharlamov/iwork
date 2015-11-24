package net.scales.service;

import net.scales.dao.CommonDAO;
import net.scales.dao.ScaleDAO;
import net.scales.dao.UserDAO;
import net.scales.model.CustomUser;
import net.scales.model.Scale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ScaleServiceImpl implements ScaleService {

	@Autowired
	private ScaleDAO scalesDataDAO;

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private CommonDAO commonDAO;

	public List<Scale> getScales(String type, Date date1, Date date2, CustomUser user) {
		return type.equalsIgnoreCase("OUT") ? scalesDataDAO.getScalesOut(date1, date2, user) : scalesDataDAO.getScalesIn(date1, date2, user);
	}

	public void updateScale(String type, Scale data, CustomUser user) throws Exception {
		if (user.getScaleType() == 0)
			throw new Exception("The current user doesn't have rights");

		userDAO.initContext(user);
		
		if(type.equalsIgnoreCase("OUT"))
			scalesDataDAO.updateScale(data);
		else
			scalesDataDAO.updateScaleIn(data);
	}

	public void insertScale(String type, Scale data, CustomUser user) throws Exception {
		if (user.getScaleType() == 0)
			throw new Exception("The current user doesn't have rights");

		userDAO.initContext(user);

		if (user.getScaleType() == 5) {
			data.setDiv(user.getDiv().getId());
			data.setClcelevatort(user.getElevator());
			data.setUserid(user.getId());
			
			if(type.equalsIgnoreCase("OUT"))
				scalesDataDAO.insertScale(data);
			else
				scalesDataDAO.insertScaleIn(data);
		} else	
			throw new Exception("The current user doesn't have rights");
	}

	public int getLimit() {
		Object obj = commonDAO.getEnvParam("YFSR_LIMIT_DIFF_MPFS");
		return obj == null ? 0 : Integer.parseInt(obj.toString());
	}
}
