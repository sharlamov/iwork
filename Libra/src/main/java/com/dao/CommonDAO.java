package com.dao;

import com.model.CustomItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CommonDAO {

	BigDecimal insertUnivItem(CustomItem ci, String tip, String gr1);

	BigDecimal insertSyssItem(CustomItem ci, String tip, String gr1);
	
	List<Object> getTransportList(int i, String query);

	List<Object> getDestinatarList(int i, String query);

	List<Object> getPunctulList(int i, String query);

	List<Object> getTipulList(int i, String query);
	
	Object getEnvParam(String string);
	
	List<Object> completeListItem(int i, String query, String table,
			String tip, String gr1);

	List<Object> getDepList(int i, String query);

	BigDecimal getDefSezon(Date fDate, long div);
}
