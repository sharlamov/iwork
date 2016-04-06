package com.service;

import com.dao.PoiExcelDAO;
import com.enums.SearchType;
import com.model.DataSet;
import com.model.Report;
import com.util.Libra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private PoiExcelDAO poiExcelDAO;

    public ReportService() {
        poiExcelDAO = new PoiExcelDAO();
        List<Report> reportList = new ArrayList<Report>();
        reportList.add(new Report("income", new File("templates/income.xls"), SearchType.REPINCOMEH.getSql(), SearchType.REPINCOMEM.getSql()));
        reportList.add(new Report("consume", new File("templates/outcome.xls"), SearchType.REPINCOMEH.getSql(), SearchType.REPOUTCOMEM.getSql()));
        reportList.add(new Report("repttn", new File("templates/TTN_oriz_negraf_vesi_jd.xls"), SearchType.REPTTNH.getSql(), SearchType.REPTTNM.getSql()));

    }

    public void buildReport(File template, String headerQuery, String masterQuery, DataSet params) throws Exception {
        DataSet header = Libra.libraService.executeQuery(headerQuery, params);
        DataSet master = Libra.libraService.executeQuery(masterQuery, params);
        poiExcelDAO.makeReport(template, header, master);
    }

    public void buildReport(File template, DataSet header) throws Exception {
        poiExcelDAO.makeReport(template, header, null);
    }

    public void buildReport(Report report, DataSet params) throws Exception {
        buildReport(report.getTemplate(), report.getHeaderSQL(), report.getMasterSQL(), params);
    }


}
