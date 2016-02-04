package com.enums;

public enum SearchType {

    CROPS("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOIE("select * from (select cod, denumirea ||', '|| codvechi as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOE("select * from (select cod, denumirea ||', '|| codvechi as clccodt from vms_univers where tip='O' and gr1 in ('E') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOI("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1 in ('I') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOIECOTA("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1 in ('I','E','COTA') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    PLACES("select * from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt from vms_syss s where tip='S' and cod='12' and cod1>0) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    PLACES1("select * from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt from vms_syss s where tip='S' and cod='12' and cod1 in (21973, 17518, 2072, 17051, 22040)"),
    DRIVER("select * from (select cod1 as cod ,denumirea as clccodt from vms_syss s where tip='S' and  cod=14 and cod1<>0) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    AUTO("select * from (select distinct NMB1T clccodt  from vms_syss v where v.tip='S' and v.cod=14 and v.cod1<>0 and NMB1T is not null) where lower(clccodt) like :findQuery and rownum < 11 order by 1"),
    REMORCA("select * from (select distinct NMB2T clccodt  from vms_syss v where v.tip='S' and v.cod=14 and v.cod1<>0 and NMB2T is not null) where lower(clccodt) like :findQuery and rownum < 11 order by 1"),
    SCALEIN("select * from (select a.*, dep_gruzootpr dep_gruzootpravit,\n" +
            "(select denumirea from vms_syss where tip='S' and  cod=14 and cod1 = a.sofer_s_14) clcsofer_s_14t\n" +
            "from VTF_PROHODN_MPFS a\n" +
            "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(:date1),'DD') and TRUNC(to_date(:date2),'DD')\n" +
            "and PRIZNAK_ARM=1\n" +
            "and div = :div\n" +
            "and exists (\n" +
            "select 1 from vms_user_elevator where :admin='1'\n" +
            "or (userid=:userid\n" +
            "and elevator = a.elevator\n" +
            "and trunc(sysdate) between datastart and dataend))\n" +
            ")order by id desc"),
    SCALEOUT("select * from (select a.*, dep_gruzootpr dep_gruzootpravit,\n" +
            "(select denumirea from vms_syss where tip='S' and  cod=14 and cod1 = a.sofer_s_14) clcsofer_s_14t\n" +
            "from VTF_PROHODN_MPFS a\n" +
            "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(:date1),'DD') and TRUNC(to_date(:date2),'DD')\n" +
            "and PRIZNAK_ARM=1\n" +
            "and div = :userid\n" +
            "and exists (\n" +
            "select 1 from vms_user_elevator where :admin='1'\n" +
            "or (userid=:userid\n" +
            "and elevator = a.elevator\n" +
            "and trunc(sysdate) between datastart and dataend))\n" +
            ")order by id desc"),
    FINDCONTRACT("select * from (\n" +
            "SELECT nrdoc1,nr_manual,data_alccontr,sc_mp,clcsc_mpt\n" +
            ",(select denumirea from vms_univers u where u.cod=div) clcdivt\n" +
            ", 0 as dep_hoz, '' as clcdep_HOZt\n" +
            "FROM YVCN1D_CLIENTS_MPFS0M\n" +
            "WHERE ((\n" +
            "(case when :clcdep_gruzootpravitt in (1078,3963,17645,3948,18360,3966,4235,3964,3962,3742,1082,67520,26823) then 1 else clientid end) =\n" +
            "(case when :clcdep_gruzootpravitt in (1078,3963,17645,3948,18360,3966,4235,3964,3962,3742,1082,67520,26823) then 1 else to_number(:clcdep_gruzootpravitt) end)\n" +
            "and ( div=decode(:clcdep_postavt\n" +
            ",1078,3335,3963,3336,17645, 3337,3948, 3338,18360, 3339\n" +
            ",3966,3340,4235,3341,3964, 3342,3962, 3343,3742, 3344,\n" +
            "1082, 61766,67520,67905,26823,74647,1000)\n" +
            "))\n" +
            "or\n" +
            "(clientid= :dep_hoz\n" +
            "and ( div=decode(:clcdep_gruzootpravitt\n" +
            ",67520,67905,26823,74647,1000)\n" +
            "))\n" +
            ")and (sc_mp=:clcsc_mpt)\n" +
            "and sezon_yyyy=:sezon_yyyy\n" +
            "union all\n" +
            "select nrdoc,nr_manual_sales nr_manual,datastart_buy data_alccontr,\n" +
            "(select min(sc) from ytrans_contract_add_d where nrdoc = t.nrdoc) sc_mp,\n" +
            "(select clcsct from yvtrans_contract_add_d where nrdoc = t.nrdoc group by sc,clcsct having sc=min(sc)) clcsc_mpt,\n" +
            "null,dep_buy, clcdep_buyt\n" +
            "from yvtrans_contract_add_tripart t\n" +
            "where nrdoc_contract_sales is not null\n" +
            "and dep_sales =  :clcdep_postavt\n" +
            "and :clcdep_gruzootpravitt = (select codi from vms_univers where cod = div_sales)\n" +
            ") where lower(nr_manual) like :findQuery " +
            "order by data_alccontr desc, nr_manual asc");

    private String sql;

    SearchType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }


}

