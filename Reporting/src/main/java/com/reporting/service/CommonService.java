package com.reporting.service;

import com.reporting.model.CustomItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CommonService {

    CustomItem saveListItem(CustomItem selectedItem, String table, String tip,
                            String gr1) throws Exception;

    List<CustomItem> completeListItem(int i, String query, String table,
                                      String tip, String gr1);

    List<CustomItem> getTransportList(int i, String query);

    List<CustomItem> getDestinatarList(int i, String query);

    List<CustomItem> getPunctulList(int i, String query);

    List<CustomItem> getTipulList(int i, String query);

    List<CustomItem> getDepList(int i, String query);

    BigDecimal getDefSezon(Date fDate, long div);
}