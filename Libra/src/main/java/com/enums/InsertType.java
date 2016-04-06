package com.enums;

public enum InsertType {
    UNIVOE("{call insert into vms_univers (cod, denumirea, codvechi, tip, gr1) values (id_tms_univers.nextval, :clccodt, :fiskcod, :tip, :gr1) RETURNING cod INTO ? }"),
    UNIVOI("{call insert into vms_univers (cod, denumirea, codvechi, tip, gr1) values (id_tms_univers.nextval, :clccodt, :fiskcod, :tip, :gr1) RETURNING cod INTO ? }"),
    UNIVOF("declare\n" +
            " vcod number;\n" +
            " begin\n" +
            "  insert into vms_univers(cod, denumirea, codvechi, tip, gr1) values (id_tms_univers.nextval, :clccodt, :fiskcod, :tip, :gr1) RETURNING cod INTO vcod;\n" +
            "  insert into tms_munc(cod, pasaport, orgeloberat, dataelibpas)values(vcod, :seria, :orgelib, :dataelib) returning cod into vcod;  \n" +
            " end;"),
    UNIVTA( "declare\n" +
            " vcod number;\n" +
            "begin\n" +
            "   insert into vms_univers (cod, denumirea, tip, gr1) values (id_tms_univers.nextval, upper(:clccodt), :tip, :gr1) RETURNING cod INTO vcod;\n" +
            "   insert into tms_mpt(cod, text1, text2)values(vcod, :sort, :axis) returning cod into ?;  \n" +
            "end;"),
    UNIVOSOLA("{call insert into vms_univers (cod, denumirea, tip, gr1) values (id_tms_univers.nextval, :clccodt, :tip, :gr1) RETURNING cod INTO ? }");

    private String sql;

    InsertType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

}
