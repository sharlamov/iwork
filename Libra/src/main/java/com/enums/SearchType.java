package com.enums;

/**
 * Created by sharlamov on 11.01.2016.
 */
public enum SearchType {
    CROPS("select cod, denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null"),
    DEP("select cod, denumirea as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null"),
    PLACES("select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt from vms_syss s where tip='S' and cod='12' and cod1>0"),
    DRIVER("select cod1 as cod ,denumirea ||', '|| nmb1t  as clccodt from vms_syss s where tip='S' and  cod=14 and cod1<>0");

    private String sql;

    SearchType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}

