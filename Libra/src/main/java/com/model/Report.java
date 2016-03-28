package com.model;

import java.io.File;
public class Report {

    private String name;
    private File template;
    private String headerSQL;
    private String masterSQL;
    private String detailSQL;

    public Report(String name, File template, String headerSQL, String masterSQL, String detailSQL) {
        this.name = name;
        this.template = template;
        this.headerSQL = headerSQL;
        this.masterSQL = masterSQL;
        this.detailSQL = detailSQL;
    }

    public Report(String name, File template, String headerSQL, String masterSQL) {
        this(name, template, headerSQL, masterSQL, null);
    }

    public Report(String name, File template, String headerSQL) {
        this(name, template, headerSQL, null);
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }

    public String getHeaderSQL() {
        return headerSQL;
    }

    public void setHeaderSQL(String headerSQL) {
        this.headerSQL = headerSQL;
    }

    public String getMasterSQL() {
        return masterSQL;
    }

    public void setMasterSQL(String masterSQL) {
        this.masterSQL = masterSQL;
    }

    public String getDetailSQL() {
        return detailSQL;
    }

    public void setDetailSQL(String detailSQL) {
        this.detailSQL = detailSQL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
