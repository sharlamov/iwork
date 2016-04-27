package com.enums;

public enum SearchType {

    INITCONTEXT("begin Libra_actions_pkg.initcontext(:plevel, :puserid, :plimit); end;"),
    LANGUAGES("select * from libra_translate_tbl where nameid is not null"),
    CROPS("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    CROPSROMIN("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 = 'P' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    CROPSROMOUT("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip in ('P','M') and gr1 in ('P','R','F') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    UNIVOIE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 = 'E' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOI("select * from (select cod, cod||', '||denumirea clccodt from vms_univers where tip='O' and gr1 = 'I' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    PLACES("select cod, clccodt from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt, denumirea from vms_syss s where tip='S' and cod='12' and cod1>0) where lower(denumirea) like :findQuery and rownum < 31 order by 2"),
    PLACES1("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='S' and cod='12' and cod1 in (21973, 17518, 2072, 17051, 22040, 22932)) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    AUTO("select * from (select cod, denumirea as clccodt from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    TRAILER("select * from (select cod, denumirea as clccodt from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    DRIVERMD("select * from (select cod1 as cod ,denumirea as clccodt from vms_syss s where tip='S' and  cod=14 and cod1<>0) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    AUTOMD("select * from (select denumirea clccodt from vms_syss s where tip='S' and  cod=15 and cod1<>0) where lower(clccodt) like :findQuery and rownum < 31 order by 1"),
    TRAILERMD("select * from (select denumirea clccodt  from vms_syss s where tip='S' and  cod=16 and cod1<>0) where lower(clccodt) like :findQuery and rownum < 31 order by 1"),
    OPERTYPE("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='OT' and cod='1') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    STATUS("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='Z' and cod='100') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    SOLA("select * from (select cod, denumirea as clccodt from vms_univers where tip = 'O' and gr1='SOLA' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    DELEGAT("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1='F' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    FINFO("select pasaport||', '||orgeloberat||', '||dataelibpas||', '||(select codvechi from vms_univers o where o.cod = m.cod)||KADR_FISC_COD info from tms_munc m where cod = :clcnamet"),
    FINFO1("select pasaport_seria||pasaport||', '||orgeloberat||', '||dataelibpas||', '||KADR_FISC_COD info from tms_munc m where cod = :clcnamet"),
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
            "   :nr_remorca, :clcdep_transpt, :cant_diff_ttn,:clcdivt\n" +
            "  ,:contract_nrmanual,:contract_data,:contract_nr,:vin\n" +
            "  ,:masa_return, :nr_act_nedovygruzki, :clcdep_hozt, :clcelevatort)"),
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
            "  prikaz_id, nrdoc_out, dep_destinat, nr_analiz, sezon_yyyy, print_chk, dep_perevoz, vin,punctto_s_12, sklad_pogruzki, ttn_nn_perem, elevator, div)\n" +
            "VALUES\n" +
            "  (:id, :nr_vagon, :time_in, :time_out, :clcsct, :masa_brutto,:masa_netto, :masa_tara, 2, :commentarii, \n" +
            "  :ttn_n, :ttn_data, :clcsofer_s_14t, :nr_remorca, :clcprazgruz_s_12t, :prikaz_masa, :prikaz_masa_max, \n" +
            "  :prikaz_id, :nrdoc_out, :clcdep_destinatt, :nr_analiz, :sezon_yyyy, :print_chk, :clcdep_perevozt, :vin, :clcpunctto_s_12t, :clcsklad_pogruzkit, \n" +
            "  :ttn_nn_perem, :clcelevatort, :clcdivt)"),
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
    INSSCALEINROM("insert into vtf_prohodn_mpfs\n" +
            "  (id, time_in, time_out, ttn_n, ttn_data, dep_postav,\n" +
            "   dep_gruzootpr, sc_mp, masa_brutto, masa_netto, masa_tara, sezon_yyyy, nr_analiz, masa_ttn,\n" +
            "   ppogruz_s_12, priznak_arm, dep_transp, cant_diff_ttn, div\n" +
            "  ,contract_nrmanual,contract_data,contract_nr, vin, nr_act_nedostaci\n" +
            "  ,masa_return, nr_act_nedovygruzki, dep_hoz, elevator" +
            "  , dep_mp,delegat, sola,ticket,opertype,sklad_pogruzki,status_z_100, nr_locuri, driver, nr_auto, nr_trailer)\n" +
            "values\n" +
            "  (:id, nvl(:time_in,sysdate), :time_out, :ttn_n, :ttn_data, :clcdep_postavt,\n" +
            "   :clcdep_gruzootpravitt, :clcsc_mpt,:masa_brutto, :masa_netto,:masa_tara, :sezon_yyyy,\n" +
            "   :nr_analiz, :masa_ttn, :clcppogruz_s_12t,1, :clcdep_transpt, :cant_diff_ttn,:clcdivt\n" +
            "  ,:contract_nrmanual,:contract_data,:contract_nr,:vin, :nr_act_nedostaci\n" +
            "  ,:masa_return, :nr_act_nedovygruzki, :clcdep_hozt, :clcelevatort, " +
            "   :clcdep_mpt,:clcdelegatt, :clcsolat,:ticket,:clcopertypet,:clcsklad_pogruzkit,:clcstatus_z_100t, :nr_locuri," +
            "   :clcdrivert, :clcnr_autot, :clcnr_trailert)"),
    UPDSCALEINROM("update vtf_prohodn_mpfs set\n" +
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
            "  sezon_yyyy = :sezon_yyyy,\n" +
            "  nr_analiz = :nr_analiz,\n" +
            "  masa_ttn = :masa_ttn,\n" +
            "  ppogruz_s_12 = :clcppogruz_s_12t,\n" +
            "  priznak_arm = 1,\n" +
            "  dep_transp = :clcdep_transpt,\n" +
            "  cant_diff_ttn = :cant_diff_ttn,\n" +
            "  contract_nrmanual = :contract_nrmanual,\n" +
            "  contract_data = :contract_data,\n" +
            "  contract_nr = :contract_nr,\n" +
            "  vin = :vin,\n" +
            "  masa_return = :masa_return,\n" +
            "  nr_act_nedovygruzki = :nr_act_nedovygruzki,\n" +
            "  nr_act_nedostaci = :nr_act_nedostaci,\n" +
            "  sofer = :sofer,\n" +
            "  dep_hoz = :clcdep_hozt," +
            "  dep_mp = :clcdep_mpt, delegat = :clcdelegatt,sola = :clcsolat,ticket = :ticket,opertype = :clcopertypet," +
            "  sklad_pogruzki= :clcsklad_pogruzkit,status_z_100 = :clcstatus_z_100t, nr_locuri = :nr_locuri" +
            "  , driver = :clcdrivert, nr_auto = :clcnr_autot, nr_trailer = :clcnr_trailert" +
            "  where id = :id"),
    INSSCALEOUTROM("insert into ytrans_vtf_prohodn_out\n" +
            "  (id, nr_vagon, time_in, time_out, sc, masa_brutto, masa_netto, masa_tara, priznak_arm, commentarii, \n" +
            "  ttn_n, ttn_data, sofer_s_14, nr_remorca, prazgruz_s_12, prikaz_masa, prikaz_masa_max, \n" +
            "  prikaz_id, nrdoc_out, dep_destinat, nr_analiz, sezon_yyyy, print_chk, dep_perevoz, vin,punctto_s_12, sklad_pogruzki" +
            "  ,contract_nrmanual,contract_data, elevator, div" +
            "  ,status_z_100, ttn_vagon, ticket, opertype, delegat, driver, nr_auto, nr_trailer,silo_dest, cell_dest)\n" +
            "VALUES\n" +
            "  (:id, :nr_vagon, :time_in, :time_out, :clcsct, :masa_brutto,:masa_netto, :masa_tara, 2, :commentarii, \n" +
            "  :ttn_n, :ttn_data, :clcsofer_s_14t, :nr_remorca, :clcprazgruz_s_12t, :prikaz_masa, :prikaz_masa_max, \n" +
            "  :prikaz_id, :nrdoc_out, :clcdep_destinatt, :nr_analiz, :sezon_yyyy, :print_chk, :clcdep_perevozt, :vin, :clcpunctto_s_12t, :clcsklad_pogruzkit, \n" +
            "  :contract_nrmanual,:contract_data, :clcelevatort, :clcdivt," +
            "  :clcstatus_z_100t, :ttn_vagon, :ticket,:clcopertypet,:clcdelegatt,:clcdrivert,:clcnr_autot,:clcnr_trailert,:clcsilo_destt, :clccell_destt)"),
    UPDSCALEOUTROM("update ytrans_vtf_prohodn_out set\n" +
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
            "   contract_nrmanual = :contract_nrmanual,\n" +
            "   contract_data = :contract_data,\n" +
            "   sezon_yyyy = :sezon_yyyy,\n" +
            "   print_chk = :print_chk,\n" +
            "   dep_perevoz = :clcdep_perevozt,\n" +
            "   vin = :vin,\n" +
            "   punctto_s_12 = :clcpunctto_s_12t,\n" +
            "   sklad_pogruzki = :clcsklad_pogruzkit,\n" +
            "   status_z_100 = :clcstatus_z_100t, ttn_vagon = :ttn_vagon, ticket = :ticket, opertype = :clcopertypet, delegat = :clcdelegatt," +
            "   driver = :clcdrivert, nr_auto = :clcnr_autot, nr_trailer = :clcnr_trailert, silo_dest = :clcsilo_destt, cell_dest = :clccell_destt\n" +
            "where id = :id"),
    INSHISTORY("insert into vtf_prohodn_scales (tip,id,nr,dt,br,userid,sc,masa,app,scaleId) values (:tip,:id,id_tmdb_cm.nextval,:dt,:br,:userid,:sc,:masa,1,:scaleId)"),
    NEXTVAL("{call select ID_MP_VESY.NEXTVAL into ? from dual}"),
    GETUSERPROP("select prop from (\n" +
            "      select trim((select value from a$adp$v p where key = ? and obj_id = a.obj_id)) prop \n" +
            "      from a$adm a connect by obj_id = prior parent_id start with obj_id = (select obj_id from a$adp$v p where key='ID' and value=?) order by level\t\n" +
            ")where prop is not null and rownum = 1"),
    GETSILOSBYUSER("select elevator, clcelevatort from vms_user_elevator where userid = ?"),
    GETDIVBYSILOS("select distinct div_id, clcdiv_idt from vms_elevator_company where elevator_id = :elevator_id"),
    DATABYELEVATOR("select\n" +
            "(select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'SIGN1') DIRECTOR,\n" +
            "(select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'SIGN2') CONT_SEF,\n" +
            "(select (select denumirea from vms_syss where cod1 = oras and tip='S' and cod='12') from vms_org where cod = :clcelevatort) oras\n" +
            "from dual"),
    MERGEPRINTDETAIL("merge into libra_printinfo_tbl a\n" +
            "using (select :p_id p_id, :pl1c pl1c, :pl2c pl2c, :pl3c pl3c, :pl4c pl4c, :pl5c pl5c, :pl6c pl6c, :pl7c pl7c, :pl8c pl8c, :pl9c pl9c, :pl10c pl10c, :pl11c pl11c\n" +
            ", :pl12c pl12c, :pl13c pl13c, :pl14c pl14c, :pl15c pl15c, :pl16c pl16c, :pl17c pl17c, :pl18c pl18c, :pl19c pl19c \n" +
            ", :tva tva, :pricetva pricetva, :tiptara tiptara,:suma suma, :sumaTva sumaTva, :total total, :price price from dual) b\n" +
            "on (a.p_id = b.p_id)\n" +
            "when matched then update set\n" +
            " pl1c = :pl1c, pl2c = :pl2c, pl3c = :pl3c, pl4c = :pl4c,\n" +
            " pl5c = :pl5c, pl6c = :pl6c, pl7c = :pl7c, pl8c = :pl8c,\n" +
            " pl9c = :pl9c, pl10c = :pl10c, pl11c = :pl11c, pl12c = :pl12c, \n" +
            " pl13c = :pl13c, pl14c = :pl14c, pl15c = :pl15c, pl16c = :pl16c,\n" +
            " pl17c = :pl17c, pl18c = :pl18c, pl19c = :pl19c, tva = :tva, \n" +
            " pricetva = :pricetva , tiptara = :tiptara, suma = :suma, sumaTva = :sumaTva, total = :total, price = :price \n" +
            "when not matched then\n" +
            "  insert (p_id, pl1c, pl2c, pl3c, pl4c, pl5c, pl6c, pl7c, pl8c, pl9c, pl10c, pl11c, pl12c, pl13c, pl14c, pl15c, pl16c, pl17c, pl18c, pl19c, tva, pricetva, tiptara, suma, sumaTva, total, price)\n" +
            "  values (b.p_id, b.pl1c, b.pl2c, b.pl3c, b.pl4c, b.pl5c, b.pl6c, b.pl7c, b.pl8c, b.pl9c, b.pl10c, b.pl11c, b.pl12c, b.pl13c, b.pl14c, b.pl15c, b.pl16c, b.pl17c, b.pl18c, b.pl19c\n" +
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
    INSSYSS("{call insert into tms_syss (tip, cod, denumirea, um, cod1) values (:tip, :cod, :denumirea, :um, (select nvl(max(cod1),0) + 1 from vms_syss where tip = :tip  and cod = :cod )) RETURNING cod1 INTO ? }"),
    HISTORY("select br, dt,userid, (select username from vms_users u where u.cod=s.userid) clcuseridt, masa  from tf_prohodn_scales s where id = :id"),
    LOSTCARIN("select to_char(time_out, 'dd.mm.yyyy') dd from vtf_prohodn_mpfs a\n" +
            "where priznak_arm = 1 \n" +
            "and masa_netto is null\n" +
            "and elevator in (:elevator)\n" +
            "and decode(:div, null, 1, div) = nvl(:div, 1)\n" +
            "and rownum = 1 order by time_out"),
    LOSTCAROUT("select to_char(time_out, 'dd.mm.yyyy') dd from ytrans_VTF_PROHODN_OUT a\n" +
            "where PRIZNAK_ARM=2 and elevator in (:elevator)\n" +
            "and decode(:div, null, 1, div) = nvl(:div, 1)\n" +
            "and masa_netto is null\n" +
            "and rownum = 1 order by time_out"),
    FINDAUTOIN("select * from (\n" +
            " select * from (\n" +
            "  select distinct nr_auto,clcnr_autot,nr_trailer, clcnr_trailert, driver,clcdrivert from VTF_PROHODN_MPFS where nr_auto is not null\n" +
            "  union all\n" +
            "  select cod, denumirea, null, null, null, null from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null\n" +
            " ) order by instr(lower(clcnr_autot),trim(both '%' from :findquery)) asc, clcnr_autot, clcnr_trailert  desc, clcdrivert desc \n" +
            ")where lower(clcnr_autot) like :findQuery and rownum < 31"),
    FINDAUTOOUT("select * from (\n" +
            " select * from (\n" +
            "  select distinct nr_auto,clcnr_autot,nr_trailer, clcnr_trailert, driver,clcdrivert from  ytrans_vtf_prohodn_out where nr_auto is not null\n" +
            "  union all\n" +
            "  select cod, denumirea, null, null, null, null from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null\n" +
            " ) order by instr(lower(clcnr_autot),trim(both '%' from :findquery)) asc, clcnr_autot, clcnr_trailert  desc, clcdrivert desc \n" +
            ")where lower(clcnr_autot) like :findQuery and rownum < 31"),
    LOADOUTDOC("select \n" +
            "ctnum2 opertype, (select denumirea from vms_syss where tip='OT' and cod=1 and cod1=ctnum2) clcopertypet\n" +
            ",ctnum1 punctto_s_12, (select denumirea from vms_syss where tip='S' and cod=12 and cod1=ctnum1) clcpunctto_s_12t\n" +
            ",DTSC1 driver, CLCDTSC1T clcdrivert\n" +
            ",DTSC2 nr_auto, CLCDTSC2T clcnr_autot\n" +
            ",DTSC3 nr_trailer, CLCDTSC3T clcnr_trailert\n" +
            ",DTSC4 dep_perevoz, CLCDTSC4T clcdep_perevozt\n" +
            ",DTSC5 delegat, CLCDTSC5T clcdelegatt\n" +
            ",DTSTR0||DTSTR4 ttn_n, dtdata0 ttn_data, DTSTR1 ttn_vagon\n" +
            ",CTSC0 sc, CLCCTSC0T clcsct\n" +
            ",DTSTR3 contract_nrmanual, DTDATA3 contract_data\n" +
            ",DTSC0 dep_destinat,CLCDTSC0T clcdep_destinatt\n" +
            ",CTSC2 sklad_pogruzki, CLCCTSC2T clcsklad_pogruzkit\n" +
            ",DTSC6 silo_dest, CLCDTSC6T clcsilo_destt\n" +
            ",DTSC7 cell_dest, CLCDTSC7T clccell_destt\n" +
            ",(select sum(ctcant0) from vmdb_reg_a ra where ra.nrdoc = r.nrdoc) prikaz_masa\n" +
            "from vmdb_reg r where nrdoc = :nrdoc \n" +
            "and exists(select 1 from vmdb_docs where cod = r.nrdoc and sysfid = 48621)"),
        FINDCONTRACTROIN("select m.contractid, nr_manual, m.data_contract, datastart, dataend, clcmasa_typet tip_masa, clcdelivery_typet deliver_type\n" +
                ", clccontract_subtypet, NRMANUAL_CONTR_BASE contr_baza\n" +
                ", contract_subtype, clcsct, cant, D.PRICE\n" +
                "from yroVmdb_contract m, YROVMDB_CONTRACT_d d\n" +
                "where clientid=:clcdep_postavt and d.contractid=m.contractid\n" +
                "and d.cod_sc=:clcsc_mpt and d.sezon=:sezon_yyyy"),
        UPDATETICKET("{ :out_ticket = call Libra_actions_pkg.createTicketCintar(:time_out, :id, :priznak_arm, :scaleid) }"),
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

