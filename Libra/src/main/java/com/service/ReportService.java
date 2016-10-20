package com.service;

import com.dao.model.DataSet;
import com.report.bin.ReportGear;
import com.report.model.Report;
import com.util.Libra;

import java.io.File;

public class ReportService {

    private ReportGear report;

    public ReportService() {
        report = new ReportGear();
    }

    private void buildReport(File template, String query, DataSet params) throws Exception {
        DataSet queries = Libra.libraService.executeOut(query, params);
        String sql0 = queries.getString("sqlHeader");
        String sql1 = queries.getString("sqlMaster");

        DataSet ds0 = sql0.isEmpty() ? new DataSet() : Libra.libraService.executeQuery(sql0, params);
        DataSet ds1 = sql1.isEmpty() ? new DataSet() : Libra.libraService.executeQuery(sql1, ds0);
        report.make(template, ds0, ds1);
    }

    public void buildReport(Report report, DataSet params) throws Exception {
        buildReport(report.getTemplate(), report.getSql(), params);
    }
}
