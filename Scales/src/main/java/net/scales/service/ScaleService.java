package net.scales.service;

import net.scales.model.CustomUser;
import net.scales.model.Scale;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ScaleService {

	List<Scale> getScales(String type, Date date1, Date date2, CustomUser user);

	void updateScale(String type, Scale data, CustomUser user) throws Exception;

	void insertScale(String type, Scale data, CustomUser user) throws Exception;

	int getLimit();
}
