package com.service;

import com.dao.PoiExcelDAO;
import com.enums.SearchType;
import com.model.DataSet;
import com.model.Report;
import com.util.Libra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportService {

    private PoiExcelDAO poiExcelDAO;
    private List<Report> reportList;

    public ReportService() {
        poiExcelDAO = new PoiExcelDAO();
        reportList = new ArrayList<Report>();
        reportList.add(new Report("income", new File("templates/income.xls"), SearchType.REPINCOMEH.getSql(), SearchType.REPINCOMEM.getSql()));
        reportList.add(new Report("consume", new File("templates/outcome.xls"), SearchType.REPINCOMEH.getSql(), SearchType.REPOUTCOMEM.getSql()));
        reportList.add(new Report("repttn", new File("templates/TTN_oriz_negraf_vesi_jd.xls"), SearchType.REPTTNH.getSql(), SearchType.REPTTNM.getSql()));
    }

    public void buildReport(File template, String headerQuery, String masterQuery, Map<String, Object> params) throws Exception {
        DataSet header = Libra.libraService.selectDataSet(headerQuery, params);
        DataSet master = Libra.libraService.selectDataSet(masterQuery, params);
        poiExcelDAO.makeReport(template, header, master);
    }

    public void buildReport(File template, DataSet header) throws Exception {
        poiExcelDAO.makeReport(template, header, null);
    }

    public void buildReport(Report report, Map<String, Object> params) throws Exception {
        buildReport(report.getTemplate(), report.getHeaderSQL(), report.getMasterSQL(), params);
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
}
