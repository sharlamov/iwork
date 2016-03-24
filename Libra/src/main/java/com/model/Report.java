package com.model;

public class Report {

    private java.lang.String template;
    private String headerSQL;
    private String masterSQL;
    private String detailSQL;

    public Report(java.lang.String template, String headerSQL, String masterSQL, String detailSQL) {
        this.template = template;
        this.headerSQL = headerSQL;
        this.masterSQL = masterSQL;
        this.detailSQL = detailSQL;
    }

    public Report(java.lang.String template, String headerSQL, String masterSQL) {
        this(template, headerSQL, masterSQL, null);
    }

    public Report(java.lang.String template, String headerSQL) {
        this(template, headerSQL, null);
    }

    public java.lang.String getTemplate() {
        return template;
    }

    public void setTemplate(java.lang.String template) {
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
}
