package com.enums;

public enum ReportType {
    INCOMES("templates/income.xls", SearchType.REPINCOMEH, SearchType.REPINCOMEM),
    OUTCOMES("templates/outcome.xls", SearchType.REPINCOMEH, SearchType.REPOUTCOMEM);

    private String template;
    private SearchType headerSQL;
    private SearchType masterSQL;

    ReportType(String template, SearchType headerSQL, SearchType masterSQL) {
        this.template = template;
        this.headerSQL = headerSQL;
        this.masterSQL = masterSQL;
    }

    public String getTemplate() {
        return template;
    }

    public SearchType getHeaderSQL() {
        return headerSQL;
    }

    public SearchType getMasterSQL() {
        return masterSQL;
    }
}