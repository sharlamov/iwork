package com.enums;

public enum SearchType {

    INITCONTEXT("begin Libra_actions_pkg.initcontext(:plevel, :puserid, :plimit); end;"),
    CROPS("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    CROPSROMIN("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip in ('M','P') and gr1 in ('P','F') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    CROPSROMOUT("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip in ('P','M') and gr1 in ('P','R','F') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    UNIVOIE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 = 'E' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOI("select * from (select cod, cod||', '||denumirea clccodt from vms_univers where tip='O' and gr1 = 'I' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    UNIVCELL("select * from (select cod, cod||', '||denumirea clccodt from vms_univers where tip='O' and gr1 = 'CELL' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    PLACES("select cod, clccodt from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt, denumirea from vms_syss s where tip='S' and cod='12' and cod1>0) where lower(denumirea) like :findQuery and rownum < 31 order by 2"),
    PLACES1("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='S' and cod='12' and cod1 in (21973, 17518, 2072, 17051, 22040, 22932)) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    TRAILER("select * from (select cod, denumirea as clccodt from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    OPERTYPE("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='OT' and cod='1') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    STATUS("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='Z' and cod='100') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    SOLA("select * from (select cod, denumirea as clccodt from vms_univers where tip = 'O' and gr1='SOLA' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    DELEGAT("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1='F' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    FINFO("select pasaport||', '||orgeloberat||', '||dataelibpas||', '||(select codvechi from vms_univers o where o.cod = m.cod)||KADR_FISC_COD info from tms_munc m where cod = :clcnamet"),
    FINFO1("select pasaport_seria||pasaport||', '||orgeloberat||', '||dataelibpas||', '||KADR_FISC_COD info from tms_munc m where cod = :clcnamet"),
    FINDCONTRACT("select nrdoc1,nr_manual,data_alccontr, partener, clcpartenert from (\n" +
            "SELECT nrdoc1,nr_manual,data_alccontr,sc_mp,clcsc_mpt\n" +
            ",(select denumirea from vms_univers u where u.cod=div) clcdivt\n" +
            ", clientid partener, clcclientidt clcpartenert\n" +
            "FROM YVCN1D_CLIENTS_MPFS0M\n" +
            "WHERE ((\n" +
            "(case when :clcshippert in (1078,3963,17645,3948,18360,3966,4235,3964,3962,3742,1082,67520,26823) then 1 else clientid end) =\n" +
            "(case when :clcshippert in (1078,3963,17645,3948,18360,3966,4235,3964,3962,3742,1082,67520,26823) then 1 else to_number(:clcshippert) end)\n" +
            "and ( div=decode(:clcclientt\n" +
            ",1078,3335,3963,3336,17645, 3337,3948, 3338,18360, 3339\n" +
            ",3966,3340,4235,3341,3964, 3342,3962, 3343,3742, 3344,\n" +
            "1082, 61766,67520,67905,26823,74647,1000)\n" +
            "))\n" +
            "or\n" +
            "(clientid= :clcpartenert\n" +
            "and ( div=decode(:clcshippert,67520,67905,26823,74647,1000)\n" +
            "))\n" +
            ")\n" +
            "and (sc_mp=:clcsct)\n" +
            "and sezon_yyyy=:season\n" +
            "union all\n" +
            "select nrdoc,nr_manual_sales nr_manual,datastart_buy data_alccontr,\n" +
            "(select min(sc) from ytrans_contract_add_d where nrdoc = t.nrdoc) sc_mp,\n" +
            "(select clcsct from yvtrans_contract_add_d where nrdoc = t.nrdoc group by sc,clcsct having sc=min(sc)) clcsc_mpt,\n" +
            "null,dep_buy, clcdep_buyt\n" +
            "from yvtrans_contract_add_tripart t\n" +
            "where nrdoc_contract_sales is not null\n" +
            "and dep_sales =  :clcclientt\n" +
            "and :clcshippert = (select codi from vms_univers where cod = div_sales)\n" +
            ") where lower(nr_manual) like :findQuery order by data_alccontr desc, nr_manual asc"),
    INSSCALEIN("insert into vmdb_scales\n" +
            "(id, in_out, userid, type_vehicle, nr_analysis, season, masa_brutto, masa_tara, masa_netto, \n" +
            " time_in, time_out, inv_nr, inv_data, inv_cant, inv2_nr, \n" +
            " ticket_nr, contract_id, contract_nr, contract_data, \n" +
            " comments, act_loss, act_rest, cant_return, order_shipment, \n" +
            " nrdoc_out, mark, categ_hum, clcccant_losst, vehicle, trailer, driver, \n" +
            " elevator, div, sc, client, transporter, shipper, \n" +
            " partener, type_oper, status, place_load, dep_load, \n" +
            " cell_load, land_load, place_unload, dep_unload, cell_unload, \n" +
            " land_unload, place2_unload,MASA_BRUTTO_HAND,MASA_TARA_HAND,SCALEID)\n" +
            "values(:id, :in_out, :userid, :clctype_vehiclet, :nr_analysis, :season, :masa_brutto, :masa_tara, :masa_netto, \n" +
            " :time_in, :time_out, :inv_nr, :inv_data, :inv_cant, :inv2_nr, \n" +
            " :ticket_nr, :contract_id, :contract_nr, :contract_data, \n" +
            " :comments, :act_loss, :act_rest, :cant_return, :order_shipment, \n" +
            " :nrdoc_out, :mark, :categ_hum, :clcccant_losst, :clcvehiclet, :clctrailert, :clcdrivert, \n" +
            " :clcelevatort, :clcdivt, :clcsct, :clcclientt, :clctransportert, :clcshippert, \n" +
            " :clcpartenert, :clctype_opert, :clcstatust, :clcplace_loadt, :clcdep_loadt, \n" +
            " :clccell_loadt, :clcland_loadt, :clcplace_unloadt, :clcdep_unloadt, :clccell_unloadt, \n" +
            " :clcland_unloadt, :clcplace2_unloadt,:MASA_BRUTTO,:MASA_TARA, :SCALEID)\n"),
    UPDSCALEIN("update vmdb_scales\n" +
            " set(in_out, userid, type_vehicle, nr_analysis, season, masa_brutto, masa_tara, masa_netto, \n" +
            " time_in, time_out, inv_nr, inv_data, inv_cant, inv2_nr, \n" +
            " ticket_nr, contract_id, contract_nr, contract_data, \n" +
            " comments, act_loss, act_rest, cant_return, order_shipment, \n" +
            " nrdoc_out, mark, categ_hum, clcccant_losst, vehicle, trailer, driver, \n" +
            " elevator, div, sc, client, transporter, shipper, \n" +
            " partener, type_oper, status, place_load, dep_load, \n" +
            " cell_load, land_load, place_unload, dep_unload, cell_unload, \n" +
            " land_unload, place2_unload,MASA_BRUTTO_HAND,MASA_TARA_HAND,SCALEID) =\n" +
            "(select :in_out, :userid, :clctype_vehiclet, :nr_analysis, :season, :masa_brutto, :masa_tara, :masa_netto, \n" +
            " :time_in, :time_out, :inv_nr, :inv_data, :inv_cant, :inv2_nr, \n" +
            " :ticket_nr, :contract_id, :contract_nr, :contract_data, \n" +
            " :comments, :act_loss, :act_rest, :cant_return, :order_shipment, \n" +
            " :nrdoc_out, :mark, :categ_hum, :clcccant_losst, :clcvehiclet, :clctrailert, :clcdrivert, \n" +
            " :clcelevatort, :clcdivt, :clcsct, :clcclientt, :clctransportert, :clcshippert, \n" +
            " :clcpartenert, :clctype_opert, :clcstatust, :clcplace_loadt, :clcdep_loadt, \n" +
            " :clccell_loadt, :clcland_loadt, :clcplace_unloadt, :clcdep_unloadt, :clccell_unloadt, \n" +
            " :clcland_unloadt, :clcplace2_unloadt,:MASA_BRUTTO,:MASA_TARA,:SCALEID from dual)\n" +
            " where id = :id"),
    INSHISTORY("insert into vtf_prohodn_scales (tip,id,nr,dt,br,userid,sc,masa,app,scaleId, photo1, photo2) values (:tip,:id,id_tmdb_cm.nextval,:dt,:br,:userid,:sc,:masa,1,:scaleId, :photo1, :photo2)"),
    NEXTVAL("{call select ID_MP_VESY.NEXTVAL into ? from dual}"),
    GETUSERPROP("select prop from (\n" +
            "      select trim((select value from a$adp$v p where key = ? and obj_id = a.obj_id)) prop \n" +
            "      from a$adm a connect by obj_id = prior parent_id start with obj_id = (select obj_id from a$adp$v p where key='ID' and value=?) order by level\t\n" +
            ")where prop is not null and rownum = 1"),
    GETFILIALS("select elevator_id,CLCELEVATOR_IDT, div_id, clcdiv_idt from vms_elevator_company where elevator_id in (select elevator from vms_user_elevator where userid = :userid)"),
    DATABYELEVATOR("select\n" +
            "(select value from a$adp$v p WHERE section = 'COMPANY'||:clcdivt and key = 'SIGN1') DIRECTOR,\n" +
            "(select value from a$adp$v p WHERE section = 'COMPANY'||:clcdivt and key = 'SIGN2') CONT_SEF,\n" +
            "(select (select denumirea from vms_syss where cod1 = oras and tip='S' and cod='12') from vms_org where cod = :clcelevatort) oras\n" +
            "from dual"),
    MERGEPRINTDETAIL("merge into libra_printinfo_tbl a\n" +
            "using (select :pid pid, :pl1c pl1c, :pl2c pl2c, :pl3c pl3c, :pl4c pl4c, :pl5c pl5c, :pl6c pl6c, :pl7c pl7c, :pl8c pl8c, :pl9c pl9c, :pl10c pl10c, :pl11c pl11c\n" +
            ", :pl12c pl12c, :pl13c pl13c, :pl14c pl14c, :pl15c pl15c, :pl16c pl16c, :pl17c pl17c, :pl18c pl18c, :pl19c pl19c \n" +
            ", :tva tva, :pricetva pricetva, :tiptara tiptara,:suma suma, :sumaTva sumaTva, :total total, :price price from dual) b\n" +
            "on (a.pid = b.pid)\n" +
            "when matched then update set\n" +
            " pl1c = :pl1c, pl2c = :pl2c, pl3c = :pl3c, pl4c = :pl4c,\n" +
            " pl5c = :pl5c, pl6c = :pl6c, pl7c = :pl7c, pl8c = :pl8c,\n" +
            " pl9c = :pl9c, pl10c = :pl10c, pl11c = :pl11c, pl12c = :pl12c, \n" +
            " pl13c = :pl13c, pl14c = :pl14c, pl15c = :pl15c, pl16c = :pl16c,\n" +
            " pl17c = :pl17c, pl18c = :pl18c, pl19c = :pl19c, tva = :tva, \n" +
            " pricetva = :pricetva , tiptara = :tiptara, suma = :suma, sumaTva = :sumaTva, total = :total, price = :price \n" +
            "when not matched then\n" +
            "  insert (pid, pl1c, pl2c, pl3c, pl4c, pl5c, pl6c, pl7c, pl8c, pl9c, pl10c, pl11c, pl12c, pl13c, pl14c, pl15c, pl16c, pl17c, pl18c, pl19c, tva, pricetva, tiptara, suma, sumaTva, total, price)\n" +
            "  values (b.pid, b.pl1c, b.pl2c, b.pl3c, b.pl4c, b.pl5c, b.pl6c, b.pl7c, b.pl8c, b.pl9c, b.pl10c, b.pl11c, b.pl12c, b.pl13c, b.pl14c, b.pl15c, b.pl16c, b.pl17c, b.pl18c, b.pl19c\n" +
            "  , b.tva, b.pricetva, b.tiptara, b.suma, b.sumaTva, b.total, b.price)"),
    MERGEPRINTDETAILRO("MERGE INTO libra_printinfo_view a\n" +
            "   USING (SELECT :pid pid, :masa_brutto masa_brutto, :masa_tara masa_tara, :masa_netto masa_netto, :umiditate umiditate, :impuritati impuritati\n" +
            "   ,:clcdelegatt delegat, :clcgestionart gestionar, :clcdrivert driver, :clccusert cuser, :alte alte, :lot lot\n" +
            "            FROM DUAL) b\n" +
            "   ON (a.pid = b.pid)\n" +
            "   WHEN MATCHED THEN\n" +
            "      UPDATE\n" +
            "         SET masa_brutto = b.masa_brutto, masa_tara = b.masa_tara, masa_netto = b.masa_netto,\n" +
            "             umiditate = b.umiditate, impuritati = b.impuritati, delegat = b.delegat, gestionar = b.gestionar,\n" +
            "             driver = b.driver, cuser = b.cuser, alte = b.alte, lot = b.lot\n" +
            "   WHEN NOT MATCHED THEN\n" +
            "      INSERT (pid, masa_brutto, masa_tara, masa_netto, umiditate, impuritati, delegat, gestionar, driver, cuser, alte, lot)\n" +
            "      VALUES (b.pid, b.masa_brutto, b.masa_tara, b.masa_netto, b.umiditate, b.impuritati, b.delegat, b.gestionar, b.driver, b.cuser, b.alte, b.lot)\n"),
    HISTORY("select br, dt,userid, (select username from vms_users u where u.cod=s.userid) clcuseridt, masa, photo1, photo2 from tf_prohodn_scales s where id = :id"),
    LOSTCAR("select * from (\n" +
            "select to_char(time_out, 'dd.mm.yyyy') dd from vmdb_scales a\n" +
            "where in_out = :in_out \n" +
            "and type_vehicle = :type\n" +
            "and masa_netto is null\n" +
            "and elevator in (:elevator)\n" +
            "and decode(:div, null, 1, div) = nvl(:div, 1)\n" +
            "order by trunc(time_out)) where rownum = 1"),
    FINDAUTOIN("select * from (\n" +
            " select * from (\n" +
            "  select distinct vehicle,clcvehiclet,trailer, clctrailert, driver,clcdrivert from vmdb_scales where vehicle is not null\n" +
            "  union all\n" +
            "  select cod, denumirea, null, null, null, null from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null\n" +
            " ) order by instr(lower(clcvehiclet),trim(both '%' from :findquery)) asc, clcvehiclet, clctrailert  desc, clcdrivert desc \n" +
            ")where lower(clcvehiclet) like :findQuery and rownum < 31"),
    LOADOUTDOC("select \n" +
            "ctnum2 type_oper, (select denumirea from vms_syss where tip='OT' and cod=1 and cod1=ctnum2) clctype_opert\n" +
            ",ctnum1 place_unload, (select denumirea from vms_syss where tip='S' and cod=12 and cod1=ctnum1) clcplace_unloadt\n" +
            ",DTSC1 driver, CLCDTSC1T clcdrivert\n" +
            ",DTSC2 vehicle, CLCDTSC2T clcvehiclet\n" +
            ",DTSC3 trailer, CLCDTSC3T clctrailert\n" +
            ",DTSC4 transporter, CLCDTSC4T clctransportert\n" +
            ",DTSTR0||DTSTR4 inv_nr, dtdata0 inv_data, DTSTR1 inv2_nr\n" +
            ",CTSC0 sc, CLCCTSC0T clcsct\n" +
            ",DTSTR3 contract_nr, DTDATA3 contract_data\n" +
            ",DTSC0 client,CLCDTSC0T clcclientt\n" +
            ",CTSC2 cell_load, CLCCTSC2T clccell_loadt\n" +
            ",DTSC6 dep_unload, CLCDTSC6T clcdep_unloadt\n" +
            ",DTSC7 cell_unload, CLCDTSC7T clccell_unloadt\n" +
            ",(select sum(ctcant0) from vmdb_reg_a ra where ra.nrdoc = r.nrdoc) inv_cant\n" +
            "from vmdb_reg r where nrdoc = :nrdoc \n" +
            "and exists(select 1 from vmdb_docs where cod = r.nrdoc and sysfid = 48621)"),
    FINDCONTRACTROIN("select m.contractid, nr_manual, m.data_contract, datastart, dataend, clcmasa_typet tip_masa, clcdelivery_typet deliver_type\n" +
            ", clccontract_subtypet, NRMANUAL_CONTR_BASE contr_baza\n" +
            ", contract_subtype, clcsct, cant, D.PRICE\n" +
            "from yroVmdb_contract m, YROVMDB_CONTRACT_d d\n" +
            "where clientid=:clcdep_postavt and d.contractid=m.contractid\n" +
            "and d.cod_sc=:clcsc_mpt and d.sezon=:sezon_yyyy"),
    FINDPRIKAZ("select nrdoc, dtstr0||' '||dtstr4 aviz, dtdata0, CLCDTSC0T, CLCDTSC2T, CLCDTSC1T\n" +
            ",(select denumirea from vms_syss where tip='S' and cod=12 and cod1=ctnum1) CLCLOCALITATET \n" +
            "from vmdb_reg a where ctnum3 in (1,2) and nrdoc in \n" +
            "(select cod  from tmdb_docs where sysfid = 48621 and datamanual >= trunc(sysdate) - 3)");

    private String sql;

    SearchType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}

