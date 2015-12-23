package com.driver;

/**
 * Created by sharlamov on 21.12.2015.
 */
public enum ScaleType {
    R320(2400, "([\u0002][-|\\s|0-9][0-9|\\s]{7}[G])", 20),
    ALEX2400NEW(2400, "([=][0-9|\\s]{8}[\\D])", 20),
    ALEX2400OLD(2400, "([+][0-9]+)", 20),
    ALEX9600OLD(9600, "([+][0-9]+)", 20);



    private int rate;
    private String format;
    private int deviation;

    ScaleType(int rate, String format, int deviation) {
        this.rate = rate;
        this.format = format;
        this.deviation = deviation;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDeviation() {
        return deviation;
    }

    public void setDeviation(int deviation) {
        this.deviation = deviation;
    }
}
