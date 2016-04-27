package com.model;

import java.util.List;

public class Doc {

    private int id;
    private String name;
    private boolean usePrintInfo;
    private String printInfoSql;
    private List<Act> actions;
    private List<Report> reports;

    public Doc(int id, String name, List<Act> actions, List<Report> reports) {
        this.id = id;
        this.name = name;
        this.actions = actions;
        this.reports = reports;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Act> getActions() {
        return actions;
    }

    public void setActions(List<Act> actions) {
        this.actions = actions;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public boolean isUsePrintInfo() {
        return usePrintInfo;
    }

    public void setUsePrintInfo(boolean usePrintInfo) {
        this.usePrintInfo = usePrintInfo;
    }

    public String getPrintInfoSql() {
        return printInfoSql;
    }

    public void setPrintInfoSql(String printInfoSql) {
        this.printInfoSql = printInfoSql;
    }
}
