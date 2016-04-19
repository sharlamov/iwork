package com.service;

import com.dao.PoiExcelDAO;
import com.model.DataSet;
import com.model.Report;
import com.util.Libra;

import java.io.File;

public class ReportService {

    private PoiExcelDAO poiExcelDAO;

    public ReportService() {
        poiExcelDAO = new PoiExcelDAO();
    }

    public void buildReport(File template, String query, DataSet params) throws Exception {
        DataSet queries = Libra.libraService.execute1(query, params);
        String sql0 = queries.getStringValue("sqlHeader", 0);
        String sql1 = queries.getStringValue("sqlMaster", 0);

        DataSet ds0 = sql0.isEmpty() ? null : Libra.libraService.executeQuery(sql0, params);
        DataSet ds1 = sql1.isEmpty() ? null : Libra.libraService.executeQuery(sql1, ds0);
        poiExcelDAO.makeReport(template, ds0, ds1);
    }

    public void buildReport(Report report, DataSet params) throws Exception {
        buildReport(report.getTemplate(), report.getSql(), params);
    }
}
