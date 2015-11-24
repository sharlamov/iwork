package com.reporting.enums;

public enum PeriodType {
    DAY(1), WEEK(7), MONTH(30), QUARTER(120), YEAR(360);

    private final int period;

    private final long oneDayMills = 86400000L;

    PeriodType(int period) {
        this.period = period;
    }

    public long getValue(){
        return oneDayMills * period;
    }

    public String getText(){
        return  String.valueOf(oneDayMills * period);
    }
}
