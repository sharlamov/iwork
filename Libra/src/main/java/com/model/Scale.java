package com.model;

import com.driver.ScalesDriver;

import java.net.URL;
import java.util.List;

public class Scale {
    private int scaleId;
    private ScalesDriver driver;
    private List<URL> cams;

    public Scale(int scaleId, ScalesDriver driver, List<URL> cams) {
        this.scaleId = scaleId;
        this.driver = driver;
        this.cams = cams;
    }

    public int getScaleId() {
        return scaleId;
    }

    public void setScaleId(int scaleId) {
        this.scaleId = scaleId;
    }

    public ScalesDriver getDriver() {
        return driver;
    }

    public void setDriver(ScalesDriver driver) {
        this.driver = driver;
    }

    public List<URL> getCams() {
        return cams;
    }

    public void setCams(List<URL> cams) {
        this.cams = cams;
    }
}
