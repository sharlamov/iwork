package com.model.settings;

public class DataGridSetting {

    private boolean useBgColor;
    private boolean useSummary;
    private boolean useSorting;
    private String query;
    private GridField[] names;

    public GridField[] getNames() {
        return names;
    }

    public void setNames(GridField[] names) {
        this.names = names;
    }

    public boolean isUseBgColor() {
        return useBgColor;
    }

    public void setUseBgColor(boolean useBgColor) {
        this.useBgColor = useBgColor;
    }

    public boolean isUseSummary() {
        return useSummary;
    }

    public void setUseSummary(boolean useSummary) {
        this.useSummary = useSummary;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isUseSorting() {
        return useSorting;
    }

    public void setUseSorting(boolean useSorting) {
        this.useSorting = useSorting;
    }
}
