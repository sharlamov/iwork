package com.model.settings;

import java.net.URL;
import java.util.List;

public class ScaleSettings {
    private int scaleId;
    private String port;
    private String driverName;
    private List<URL> cams;

    public ScaleSettings(int scaleId, String port, String driverName, List<URL> cams) {
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

    public List<URL> getCams() {
        return cams;
    }

    public void setCams(List<URL> cams) {
        this.cams = cams;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
