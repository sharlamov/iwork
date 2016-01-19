package com.driver;

public enum ScaleType {
    R320(2400, "([\u0002][-|\\s|0-9][0-9|\\s]{7}[G])", 20),
    ALEX2400NEW(2400, "([=][0-9|\\s]{8}[\\D])", 20),
    ALEX2400OLD(2400, "([+][0-9]+)", 20),
    ALEX9600OLD(9600, "([+][0-9]+)", 20),
    FSI9600(9600, "([,][0-9|\\s]{8}[,])", 20),
    VAS9600(9600, "([$][\\s]+[0-9]+)", 7, 20);

    private int rate;
    private String format;
    private int deviation;
    private int bits;

    ScaleType(int rate, String format, int deviation) {
        this.rate = rate;
        this.format = format;
        this.deviation = deviation;
        this.bits = 8;
    }

    ScaleType(int rate, String format, int bits, int deviation) {
        this.rate = rate;
        this.format = format;
        this.deviation = deviation;
        this.bits = bits;
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

    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }
}
