package com.service;

import com.dao.PoiExcelDAO;
import com.enums.ReportType;
import com.enums.SearchType;
import com.model.DataSet;
import com.util.Libra;

import java.util.Map;

public class ReportService {

    private PoiExcelDAO poiExcelDAO;

    public ReportService() {
        poiExcelDAO = new PoiExcelDAO();
    }

    public void buildReport(String path, SearchType headerQuery, SearchType masterQuery, Map<String, Object> params) throws Exception {
        DataSet header = Libra.libraService.selectDataSet(headerQuery, params);
        DataSet master = Libra.libraService.selectDataSet(masterQuery, params);
        poiExcelDAO.makeReport(path, header, master);
    }

    public void buildReport(ReportType type, Map<String, Object> params) throws Exception {
        buildReport(type.getTemplate(), type.getHeaderSQL(), type.getMasterSQL(), params);
    }

    public void buildReport(String path, DataSet header) throws Exception {
        poiExcelDAO.makeReport(path, header, null);
    }
}
