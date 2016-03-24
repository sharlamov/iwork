package com.driver;

public enum ScaleType {
    R320(2400, "([\u0002][-|\\s|0-9][0-9|\\s]{7}[G])"),
    ALEX2400NEW(2400, "([=][0-9|\\s]{8}[\\D])"),
    ALEX2400INV(2400, "([=][0-9|\\s]{7}[\\D])", true),
    ALEX2400OLD(2400, "([+][0-9]+)"),
    ALEX9600OLD(9600, "([+][0-9]+)"),
    FSI9600(9600, "([,][0-9|\\s]{8}[,])"),
    VAS9600(9600, "([$][\\s]+[0-9]+)", 7, 20, false);

    private int rate;
    private String format;
    private int deviation;
    private int bits;
    private boolean isInverse;

    ScaleType(int rate, String format) {
        this(rate, format, 8, 20, false);
    }

    ScaleType(int rate, String format, boolean isInverse) {
        this(rate, format, 8, 20, isInverse);
    }

    ScaleType(int rate, String format, int bits, int deviation, boolean isInverse) {
        this.rate = rate;
        this.format = format;
        this.deviation = deviation;
        this.bits = bits;
        this.isInverse = isInverse;
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

    public boolean isInverse() {
        return isInverse;
    }

    public void setIsInverse(boolean isInverse) {
        this.isInverse = isInverse;
    }
}
