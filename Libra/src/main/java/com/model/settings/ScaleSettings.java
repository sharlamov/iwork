package com.model.settings;

import java.util.List;

public class ScaleSettings {
    private int scaleId;
    private String port;
    private String driverName;
    private List<String> cams;

    public ScaleSettings(int scaleId, String port, String driverName, List<String> cams) {
        this.scaleId = scaleId;
        this.port = port;
        this.driverName = driverName;
        this.cams = cams;
    }

    public int getScaleId() {
        return scaleId;
    }

    public void setScaleId(int scaleId) {
        this.scaleId = scaleId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public List<String> getCams() {
        return cams;
    }

    public void setCams(List<String> cams) {
        this.cams = cams;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
