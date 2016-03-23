package com.enums;

import com.model.Act;
import com.model.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DocType {
    IN(1, new ArrayList<Act>(),
            Arrays.asList(
                    new Report("bon.xls", String.PRINTTTN.getSql()),
                    new Report("act1.xls", String.PRINTTTN.getSql()),
                    new Report("act2.xls", String.PRINTTTN.getSql())
    )),
    OUT(2, Arrays.asList(new Act("out.act1", String.ACTOUT0.getSql()), new Act("out.act2", String.ACTOUT1.getSql())));


    private int value;
    private List<Act> acts;
    private List<Report> reports;

    DocType(int value, List<Act> acts) {
        this.value = value;
        this.acts = acts;
    }

    public int getValue() {
        return value;
    }

    public List<Act> getActs() {
        return acts;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
