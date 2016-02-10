package com.enums;

public enum SearchType {

    CROPS("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOIE("select * from (select cod, denumirea ||', '|| codvechi as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOE("select * from (select cod, denumirea ||', '|| codvechi as clccodt from vms_univers where tip='O' and gr1 in ('E') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOI("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1 in ('I') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    UNIVOIECOTA("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1 in ('I','E','COTA') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    PLACES("select * from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt from vms_syss s where tip='S' and cod='12' and cod1>0) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
    PLACES1("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='S' and cod='12' and cod1 in (21973, 17518, 2072, 17051, 22040)) where lower(clccodt) like :findQuery and rownum < 11 order by 2"),
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
    SCALEOUT("select a.*, clcdep_perevoz as clcdep_perevozt\n" +
            "from ytrans_VTF_PROHODN_OUT a\n" +
            "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(:date1),'DD') and TRUNC(to_date(:date2),'DD') \n" +
            "and PRIZNAK_ARM=2\n" +
            "and div = :div\n" +
            "and exists (\n" +
            "select 1 from vms_user_elevator where :admin='1' \n" +
            "or (userid=:userid\n" +
            "and elevator = a.elevator \n" +
            "and trunc(sysdate) between datastart and dataend))"),
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
            "order by data_alccontr desc, nr_manual asc"),
    INSSCALEIN("insert into vtf_prohodn_mpfs\n" +
            "  (id, time_in, time_out, ttn_n, ttn_data, dep_postav,\n" +
            "   dep_gruzootpr, sc_mp, masa_brutto, masa_netto, masa_tara,\n" +
            "   sofer_s_14, sofer, auto, sezon_yyyy, nr_analiz, masa_ttn,\n" +
            "   ppogruz_s_12, priznak_arm, nr_remorca, dep_transp, cant_diff_ttn, div\n" +
            "  ,contract_nrmanual,contract_data,contract_nr, vin\n" +
            "  ,masa_return, nr_act_nedovygruzki, dep_hoz, elevator)\n" +
            "values\n" +
            "  (:id, nvl(:time_in,sysdate), :time_out, :ttn_n, :ttn_data, :clcdep_postavt,\n" +
            "   :clcdep_gruzootpravitt, :clcsc_mpt,:masa_brutto, :masa_netto,:masa_tara,\n" +
            "   :clcsofer_s_14t, :sofer, :auto, :sezon_yyyy,\n" +
            "   :nr_analiz, :masa_ttn, :clcppogruz_s_12t,1,\n" +
            "   :nr_remorca, :clcdep_transpt, :cant_diff_ttn,:div\n" +
            "  ,:contract_nrmanual,:contract_data,:contract_nr,:vin\n" +
            "  ,:masa_return, :nr_act_nedovygruzki, :clcdep_hozt, SYS_CONTEXT('ENVUN4', 'DEF_ELEVATOR'))"),
    UPDSCALEIN("update vtf_prohodn_mpfs set\n" +
            "  time_in = :time_in,\n" +
            "  time_out = nvl(:time_out,sysdate),\n" +
            "  ttn_n = :ttn_n,\n" +
            "  ttn_data = :ttn_data,\n" +
            "  dep_postav = :clcdep_postavt,\n" +
            "  dep_gruzootpr = :clcdep_gruzootpravitt,\n" +
            "  sc_mp = :clcsc_mpt,\n" +
            "  masa_brutto = :masa_brutto,\n" +
            "  masa_netto = :masa_netto,\n" +
            "  masa_tara = :masa_tara,\n" +
            "  sofer_s_14 = :clcsofer_s_14t,\n" +
            "  auto = :auto,\n" +
            "  sezon_yyyy = :sezon_yyyy,\n" +
            "  nr_analiz = :nr_analiz,\n" +
            "  masa_ttn = :masa_ttn,\n" +
            "  ppogruz_s_12 = :clcppogruz_s_12t,\n" +
            "  priznak_arm = 1,\n" +
            "  nr_remorca = :nr_remorca,\n" +
            "  dep_transp = :clcdep_transpt,\n" +
            "  cant_diff_ttn = :cant_diff_ttn,\n" +
            "  contract_nrmanual = :contract_nrmanual,\n" +
            "  contract_data = :contract_data,\n" +
            "  contract_nr = :contract_nr,\n" +
            "  vin = :vin,\n" +
            "  masa_return = :masa_return,\n" +
            "  nr_act_nedovygruzki = :nr_act_nedovygruzki,\n" +
            "  dep_hoz = :clcdep_hozt\n" +
            "where id = :id"),
    INSSCALEOUT("insert into ytrans_vtf_prohodn_out\n" +
            "  (id, nr_vagon, time_in, time_out, sc, masa_brutto, masa_netto, masa_tara, priznak_arm, commentarii, \n" +
            "  ttn_n, ttn_data, sofer_s_14, nr_remorca, prazgruz_s_12, prikaz_masa, prikaz_masa_max, \n" +
            "  prikaz_id, nrdoc_out, dep_destinat, nr_analiz, sezon_yyyy, print_chk, dep_perevoz, vin,punctto_s_12, sklad_pogruzki, ttn_nn_perem, elevator)\n" +
            "VALUES\n" +
            "  (:id, :nr_vagon, :time_in, :time_out, :clcsct, :masa_brutto,:masa_netto, :masa_tara, 2, :commentarii, \n" +
            "  :ttn_n, :ttn_data, :clcsofer_s_14t, :nr_remorca, :clcprazgruz_s_12t, :prikaz_masa, :prikaz_masa_max, \n" +
            "  :prikaz_id, :nrdoc_out, :clcdep_destinatt, :nr_analiz, :sezon_yyyy, :print_chk, :clcdep_perevozt, :vin, :clcpunctto_s_12t, :clcsklad_pogruzkit, " +
            "  :ttn_nn_perem, sys_context('ENVUN4', 'DEF_ELEVATOR'))"),
    UPDSCALEOUT("update ytrans_vtf_prohodn_out set\n" +
            "   nr_vagon = :nr_vagon,\n" +
            "   time_in = :time_in,\n" +
            "   time_out = :time_out,\n" +
            "   sc = :clcsct,\n" +
            "   masa_netto = :masa_netto,\n" +
            "   masa_brutto = :masa_brutto,\n" +
            "   masa_tara = :masa_tara,\n" +
            "   priznak_arm = :priznak_arm,\n" +
            "   commentarii = :commentarii,\n" +
            "   ttn_n = :ttn_n,\n" +
            "   ttn_data = :ttn_data,\n" +
            "   sofer_s_14 = :clcsofer_s_14t,\n" +
            "   sofer = :sofer,\n" +
            "   nr_remorca = :nr_remorca,\n" +
            "   prazgruz_s_12 = :clcprazgruz_s_12t,\n" +
            "   prikaz_masa = :prikaz_masa,\n" +
            "   prikaz_masa_max = :prikaz_masa_max,\n" +
            "   prikaz_id = :prikaz_id,\n" +
            "   nrdoc_out = :nrdoc_out,\n" +
            "   dep_destinat = :clcdep_destinatt,\n" +
            "   nr_analiz = :nr_analiz,\n" +
            "   sezon_yyyy = :sezon_yyyy,\n" +
            "   print_chk = :print_chk,\n" +
            "   dep_perevoz = :clcdep_perevozt,\n" +
            "   vin = :vin,\n" +
            "   punctto_s_12 = :clcpunctto_s_12t,\n" +
            "   sklad_pogruzki = :clcsklad_pogruzkit,\n" +
            "   ttn_nn_perem = :ttn_nn_perem\n" +
            "where id = :id"),
    INSHISTORY("insert into vtf_prohodn_scales (tip,id,nr,dt,br,userid,sc,masa) values (:tip,:id,id_tmdb_cm.nextval,:dt,:br,:userid,:sc,:masa)"),
    NEXTVAL("select ID_MP_VESY.NEXTVAL from dual");

    private String sql;

    SearchType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }


}

