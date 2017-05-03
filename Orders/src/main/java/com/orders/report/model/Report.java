package com.orders.report.model;

import java.io.File;

public class Report {

    private String name;
    private File template;
    private String sql;

    public Report(String name, File template, String sql) {
        this.name = name;
        this.template = template;
        this.sql = sql;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
