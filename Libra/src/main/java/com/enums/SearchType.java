package com.enums;

public enum SearchType {

    LANGUAGES("select * from libra_translate_tbl where nameid is not null"),
    CROPS("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 in ('2161','2171','2173') and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    CROPSROMIN("select * from (select cod, cod||', '||denumirea as clccodt from vms_univers where tip = 'M' and gr1 = 'P' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    UNIVOIE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 in ('I','E') and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOE("select * from (select cod, denumirea__1 as clccodt from vms_univers where tip='O' and gr1 = 'E' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    UNIVOI("select * from (select cod, cod||', '||denumirea clccodt from vms_univers where tip='O' and gr1 = 'I' and isarhiv is null) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    PLACES("select cod, clccodt from (select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as clccodt, denumirea from vms_syss s where tip='S' and cod='12' and cod1>0) where lower(denumirea) like :findQuery and rownum < 31 order by 2"),
    PLACES1("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='S' and cod='12' and cod1 in (21973, 17518, 2072, 17051, 22040, 22932)) where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    AUTO("select * from (select cod, denumirea as clccodt from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    TRAILER("select * from (select cod, denumirea as clccodt from vms_univers where tip='T' and gr1 = 'A' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    OPERTYPE("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='OT' and cod='1') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    STATUS("select * from (select cod1 as cod, denumirea as clccodt from vms_syss s where tip='Z' and cod='100') where lower(clccodt) like :findQuery and rownum < 31 order by 2"),
    SOLA("select * from (select cod, denumirea as clccodt from vms_univers where tip = 'O' and gr1='SOLA' and isarhiv is null order by denumirea) where lower(clccodt) like :findQuery and rownum < 31"),
    DELEGAT("select * from (select cod, denumirea as clccodt from vms_univers where tip='O' and gr1='F' and isarhiv is null order by cod, denumirea, codvechi) where lower(clccodt) like :findQuery and rownum < 31"),
    FINFO("select pasaport||', '||orgeloberat||', '||dataelibpas||', '||(select codfiscal from tms_org o where o.cod = m.cod) info from tms_munc m where cod = :clcnamet"),
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
            "  prikaz_id, nrdoc_out, dep_destinat, nr_analiz, sezon_yyyy, print_chk, dep_perevoz, vin,punctto_s_12, sklad_pogruzki, elevator, div" +
            "  ,status_z_100, ttn_vagon, ticket, opertype, delegat, driver, nr_auto, nr_trailer,silo_dest, cell_dest)\n" +
            "VALUES\n" +
            "  (:id, :nr_vagon, :time_in, :time_out, :clcsct, :masa_brutto,:masa_netto, :masa_tara, 2, :commentarii, \n" +
            "  :ttn_n, :ttn_data, :clcsofer_s_14t, :nr_remorca, :clcprazgruz_s_12t, :prikaz_masa, :prikaz_masa_max, \n" +
            "  :prikaz_id, :nrdoc_out, :clcdep_destinatt, :nr_analiz, :sezon_yyyy, :print_chk, :clcdep_perevozt, :vin, :clcpunctto_s_12t, :clcsklad_pogruzkit, \n" +
            "  :clcelevatort, :clcdivt," +
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
    INSHISTORY("insert into vtf_prohodn_scales (tip,id,nr,dt,br,userid,sc,masa,app) values (:tip,:id,id_tmdb_cm.nextval,:dt,:br,:userid,:sc,:masa,1)"),
    NEXTVAL("{call select ID_MP_VESY.NEXTVAL into ? from dual}"),
    PRINTTTN("select\n" +
            " (select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'FACTURATVA') expeditor \n" +
            ",(select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'CODFISCAL') expedFisc \n" +
            ",(select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'CODTVA') expedCodTva \n" +
            ",(select value from a$adp$v p WHERE section = 'COMPANY'||:exped and key = 'NAME') clcdivnamet \n" +
            ",(SELECT denumirea FROM vms_univers WHERE cod=:dest)||',  '|| nvl((SELECT u1.adress FROM vms_org u1 WHERE :dest=U1.COD(+))\n" +
            ",(select nvl(domiciliu,kadr_adress1) from vms_munc where cod=:dest))||', '||nvl((select case when nvl(length(replace(account1,' ')),0) < 24 then 'c/d ' else 'IBAN ' end||account1 from vms_org_accounts where cod_org=:dest and rekvizit1 like '1')\n" +
            ",(select case when nvl(length(replace(account1,' ')),0) < 24 then 'c/d ' else 'IBAN ' end||account1 from vms_org_accounts where cod_org=:dest and rownum=1))||', '||nvl((select clccod_bankt1 from  vms_org_accounts where cod_org=:dest and rekvizit1 like '1')\n" +
            ",(select clccod_bankt1 from  vms_org_accounts where cod_org=:dest and rownum=1))||', '||nvl((select clcmfo_bankt from  vms_org_accounts where cod_org=:dest and rekvizit1 like '1')\n" +
            ",(select clcmfo_bankt from  vms_org_accounts where cod_org=:dest and rownum=1))destinatar \n" +
            ",(select codfiscal from vms_org where cod = :dest) destFisc\n" +
            ",(select codvechi from vms_univers where cod = :sc )sccodvechi\n" +
            ",(select um from vms_univers where cod = :sc )scum \n" +
            ",(select denumirea from vms_univers where cod = :sc )clcnamet \n" +
            ",(select denumirea from vms_univers u where cod = :transp )transport \n" +
            ",(select denumirea ||', '|| (select adress from tms_org o where o.cod = u.cod) from vms_univers u where cod = :transp )clctransportert \n" +
            ",(select codvechi from vms_univers u where cod = :transp )transpfisk \n" +
            ",(select spec1 from tms_org where cod = :transp )transpcodtva \n" +
            ",nvl((SELECT spec1 FROM VMS_ORG WHERE cod=:dest),(select kadr_fisc_cod from vms_munc where cod=:dest))destcodtva \n" +
            ",Pk_Spell.SPELL (:net) neto \n" +
            ",Pk_Spell.SPELL (:delta) delta \n" +
            ",:delta deltanr \n" +
            ",to_char(sysdate,'dd') dd \n" +
            ",to_char(sysdate,'MONTH yyyy') mon \n" +
            ",to_char(:time_in,'dd.mm.yyyy hh24-mi-ss') tin \n" +
            ",to_char(:time_out,'dd.mm.yyyy hh24-mi-ss') tout \n" +
            ",to_char(:time_in,'hh24') hhin \n" +
            ",to_char(:time_out,'hh24') hhout \n" +
            ",to_char(:time_in,'mi') mmin \n" +
            ",to_char(:time_out,'mi') mmout \n" +
            "from dual"),
    GETUSERPROP("select prop from (\n" +
            "      select trim((select value from a$adp$v p where key = ? and obj_id = a.obj_id)) prop \n" +
            "      from a$adm a connect by obj_id = prior parent_id start with obj_id = (select obj_id from a$adp$v p where key='ID' and value=?) order by level\t\n" +
            ")where prop is not null and rownum = 1"),
    GETSILOSBYUSER("select elevator, clcelevatort from vms_user_elevator where userid = ?"),
    GETDIVBYSILOS("select distinct div_id, clcdiv_idt from vms_elevator_company where elevator_id = :elevator_id"),
    SCALEPRINTDATA("select * from libra_printinfo_tbl where p_id = :p_id"),
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
            "   ,:clcdelegatt delegat, :clcgestionart gestionar, :clcdrivert driver, :clccusert cuser, :alte alte\n" +
            "            FROM DUAL) b\n" +
            "   ON (a.pid = b.pid)\n" +
            "   WHEN MATCHED THEN\n" +
            "      UPDATE\n" +
            "         SET masa_brutto = b.masa_brutto, masa_tara = b.masa_tara, masa_netto = b.masa_netto,\n" +
            "             umiditate = b.umiditate, impuritati = b.impuritati, delegat = b.delegat, gestionar = b.gestionar,\n" +
            "             driver = b.driver, cuser = b.cuser, alte = b.alte\n" +
            "   WHEN NOT MATCHED THEN\n" +
            "      INSERT (pid, masa_brutto, masa_tara, masa_netto, umiditate, impuritati, delegat, gestionar, driver, cuser, alte)\n" +
            "      VALUES (b.pid, b.masa_brutto, b.masa_tara, b.masa_netto, b.umiditate, b.impuritati, b.delegat, b.gestionar, b.driver, b.cuser, b.alte)\n"),
    REPINCOMEH("SELECT :datastart||' - '||:dataend  shapka\n" +
            ",(select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'NAME') AS Company\n" +
            ",(select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CODFISCAL') AS cfcod\n" +
            ",'приход' as type_movement, 'погрузки' as type_place\n" +
            "FROM dual\n"),
    REPINCOMEM("select rownum rn, to_char(time_in, 'dd.mm.yyyy hh24-mi-ss') time_in_text, to_char(time_out, 'dd.mm.yyyy hh24-mi-ss') time_out_text" +
            ",a. *, (select lng(denumirea,namerus,nameeng) from vms_univers u where u.cod=sc_mp) CLCSC_MPT2 \n" +
            "from (SELECT b.*\n" +
            ", anlz_vlajn vlajn,anlz_sorn sorn,trunc(time_in) data , b.clcppogruz_s_12t place\n" +
            ",CASE WHEN b.priznak_arm=1 THEN b.AUTO ELSE b.nr_remorca END transport\n" +
            "FROM VTF_PROHODN_MPFS b,VTF_LABOR_MP a\n" +
            "WHERE TRUNC(case when nvl(:cb1,0)=1 then time_IN else time_OUT end) BETWEEN :datastart AND :dataend\n" +
            "AND   NVL(b.DEP_POSTAV,0)=nvl2(:filt2,:filt2,NVL(b.DEP_POSTAV,0))\n" +
            "AND   NVL(b.SC_MP,0)=nvl2(:filt3,:filt3,NVL(b.SC_MP,0))\n" +
            "and b.div=:div and a.div(+)=b.div and b.nr_analiz=a.nr_analiz(+)  and b.ELEVATOR=a.ELEVATOR(+)\n" +
            "and b.sezon_yyyy=a.sezon_yyyy(+)\n" +
            "AND NVL(b.elevator,0)=nvl2(:elevator,:elevator,NVL(b.elevator,0)) and b.priznak_arm = 1\n" +
            "ORDER BY trunc(time_in),b.clcsc_mpt,b.CLCDEP_POSTAVT)a"),
    REPOUTCOMEM("select rownum rn, to_char(time_in, 'dd.mm.yyyy hh24-mi-ss') time_in_text, to_char(time_out, 'dd.mm.yyyy hh24-mi-ss') time_out_text" +
            ",a.* from(\n" +
            "SELECT b.time_in,b.time_out,b.CLCDEP_DESTINATT CLCDEP_POSTAVT, anlz_vlajn vlajn,anlz_sorn sorn,trunc(time_in) data \n" +
            ",b.nr_vagon transport, b.clcsct clcsc_mpt,b.MASA_BRUTTO MASA_BRUTTO_ro,b.MASA_TARA MASA_TARA_RO,b.MASA_NETTO  MASA_NETTO_RO\n" +
            ",CLCSOFER_S_14T sofer,b.priznak_arm,b.TTN_N,b.nr_analiz\n" +
            ", nvl(b.CLCPRAZGRUZ_S_12T,v.CLCDESCARCAREA_S_12T) AS Place\n" +
            "FROM ytrans_VTF_PROHODN_OUT b,VTF_LABOR_MP a, vmdb01m_vinz v \n" +
            "WHERE TRUNC(time_OUT) BETWEEN :datastart AND :dataend\n" +
            "AND   NVL(b.DEP_DESTINAT,0)=nvl2(:filt2,:filt2,NVL(b.DEP_DESTINAT,0))\n" +
            "AND   NVL(b.SC,0)=nvl2(:filt3,:filt3,NVL(b.SC,0))\n" +
            "and   NVL(b.elevator,0)=nvl2(:elevator,:elevator,NVL(b.elevator,0))\n" +
            "and b.div=:div\n" +
            "and a.div(+)=b.div\n" +
            "and b.nr_analiz=a.nr_analiz(+)\n" +
            "and b.elevator = a.elevator(+)\n" +
            "and b.sezon_yyyy=a.sezon_yyyy(+)\n" +
            "and b.nrdoc_out=v.cod(+)\n" +
            "ORDER BY trunc(time_in),b.clcsct,b.CLCDEP_DESTINATT\n" +
            ")a where a.PRIZNAK_ARM = 2"),
    REPTTNH("SELECT (SELECT DATAMANUAL FROM VMDB_DOCS WHERE COD=:nrdoc) AS DATA\n" +
            ", d.nrdoc\n" +
            ", v.cod\n" +
            ", decode(:div, 3335, 'Maistru Danilesco Alexandru', '') as MOL\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'DIRECTOR') AS Director\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CONT_SEF') AS Cont_sef\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CFFE') AS ExpCFTVA\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'FACTURATVA') AS EXPEDITOR\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CODTVA') AS TVCFEXP\n" +
            ", (select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CODFISCAL') AS ExpCFiscal\n" +
            ", (SELECT valuta FROM vmdb_st201m  WHERE nrdoc=:nrdoc) AS valuta\n" +
            ", DECODE(un.gr1,'I',(select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CODFISCAL'),un.codvechi) AS ExpCFDst\n" +
            ", DECODE(un.gr1,'I',(select value from a$adp$v p WHERE section = 'COMPANY'||:div and key = 'CODTVA'),U1.Spec1) AS ExpTvCFDst\n" +
            ", (select denumirea from vms_univers where cod=dtdep1)||', '||u1.adress AS Destinatar\n" +
            ", v.CLCINCARCAREA_S_12T AS LocIncarc\n" +
            ", v.CLCDESCARCAREA_S_12T AS LocDescarc\n" +
            ", v.PRFACT_SERIA AS FESeria\n" +
            ", v.PRFACT_NR AS FENumar\n" +
            ", v.PRFACT_DATA AS FEData\n" +
            ", nvl(v.scomment,v.PRTVA_SERIA||' '||v.PRTVA_NR) AS DocAnexate\n" +
            ", v.PRTVA_SERIA FTVASeria\n" +
            ", v.PRTVA_NR AS FTVANumar\n" +
            ", v.PRTVA_DATA AS FTVAdata\n" +
            ", v.PRPARC_SERIA AS FPSeria\n" +
            ", v.PRPARC_NR FPNumar\n" +
            ", v.PRPARC_DATA FPData\n" +
            ", v.AUTOMOBIL AS AUTO\n" +
            ", v.REMORCANR AS remorca\n" +
            ", v.SOFER AS sofer\n" +
            ", v.SOFERNRPERMISULUI permis\n" +
            ", v.EXPEDITOR_SC\n" +
            ", v.CLCEXPEDITOR_SCT\n" +
            ", v.INTREPAUTO_SC\n" +
            ", v.CLCINTREPAUTO_SCT\n" +
            ", v.INCARCAREA_S_12\n" +
            ", v.CLCINCARCAREA_S_12T\n" +
            ", v.DESCARCAREA_S_12\n" +
            ", v.CLCDESCARCAREA_S_12T\n" +
            ", v.PRCONTR_SERIA contractseria\n" +
            ", v.PRCONTR_NR contractnr\n" +
            ", v.PRCONTR_DATA contractdata\n" +
            ", v.PERSRESPCONTR_UNV\n" +
            ", v.CLCPERSRESPCONTR_UNVT\n" +
            ", v.ACHITAREA_S_8\n" +
            ", v.CLCACHITAREA_S_8T\n" +
            ", v.DATAACHIT\n" +
            ", v.INCOTERMS_S_45\n" +
            ", v.CLCINCOTERMS_S_45T\n" +
            ", v.CMR\n" +
            ", v.PRPROC_SERIA AS seriaproc\n" +
            ", v.PRPROC_NR AS NrProc\n" +
            ", v.PRPROC_DATA AS dataPROC\n" +
            ", v.PERSOANARESP AS persproc\n" +
            ", INTREPAUTO_SC AS transorg\n" +
            ", (select uu.codfiscal||', '||uu.adress from vms_org uu where uu.cod = v.intrepauto_univ_sc) AS transorg_adress\n" +
            ", 'de la pastrare' AS Number_contract\n" +
            ", d.ctnrdoc AS number_order\n" +
            ",(select UN$CURRENCYTOTXT.spell(SUMA) from(select sum(nvl(d.cant,0)) suma from vmdb_st201d d where d.nrdoc=:nrdoc)) TXTSumaT\n" +
            "FROM  (SELECT a.*, NVL(dtdep,ctdep) AS dtdep1 FROM vmdb_st201m a) D\n" +
            ", VMDB01M_VINZ V, vms_org U1, vms_univers UN, vparams_company vc\n" +
            "WHERE d.nrdoc=:nrdoc\n" +
            "AND d.nrdoc=V.cod(+)\n" +
            "AND d.dtdep1=U1.COD(+)\n" +
            "AND d.dtdep1=UN.COD(+)"),
    REPTTNM("select\n" +
            "d.dtsc AS sc\n" +
            ",(select codvechi from vms_univers where cod=d.dtsc) codvechi\n" +
            ",(select denumirea from vms_univers where cod=d.dtsc) denumirea\n" +
            ",(select um from vms_univers where cod=d.dtsc) um\n" +
            ",d.cant cant\n" +
            ",d.pret pret\n" +
            ",d.sumavaldt tva\n" +
            ",d.suma sumaftva\n" +
            ",d.suma+nvl(d.sumavaldt,0) sumat\n" +
            ",d.dtcant1 brutto\n" +
            ",0 tara\n" +
            "from vmdb_st201d d \n" +
            "where d.nrdoc=:nrdoc"),
    INSSYSS("{call insert into tms_syss (tip, cod, denumirea, um, cod1) values (:tip, :cod, :denumirea, :um, (select nvl(max(cod1),0) + 1 from vms_syss where tip = :tip  and cod = :cod )) RETURNING cod1 INTO ? }"),
    INSUNIV("{call insert into vms_univers (cod, denumirea, codvechi, tip, gr1) values (id_tms_univers.nextval, :clccodt, :fiskcod, :tip, :gr1) RETURNING cod INTO ? }"),
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
    ACTOUT0("Declare\n" +
            "vCod number;\n" +
            "vnrdoc_out number;\n" +
            "vttn varchar2(20);\n" +
            "vsysfid number;\n" +
            "prikaz number;\n" +
            "vdiv number;\n" +
            "BEGIN \n" +
            "envun4.envsetvalue('SysfIdList_AlwaysEditable', '48610,48623,48623,48608,48626');\n" +
            "Un$div.set_def(:div);\n" +
            "begin\n" +
            "SELECT sysfid,s.cod,s.div  INTO vsysfid, prikaz, vdiv  FROM vmdb_docs s,ytrans_VTF_PROHODN_OUT t WHERE s.cod=t.prikaz_id AND ID=:ID AND t.div=s.div;\n" +
            "If vsysfid not in (48621,48622,48618,48307) then \n" +
            "msg('Такой № приказа не существует!');\n" +
            "end if;\n" +
            "exception when no_data_found then\n" +
            "msg('Неправильный № документа!');\n" +
            "end;\n" +
            "select nvl(nrdoc_out,0) into vnrdoc_out from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "Select ttn_n into vttn from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "If vnrdoc_out=0 then\n" +
            "select id_tmdb_docs.nextval into vCod from dual;\n" +
            "INSERT INTO vmdb_docs (cod,sysfid,datamanual,nrmanual,div,nrset)\n" +
            "Select vcod,decode(vsysfid,48621,48610,48622,48623,48618,48608,48307,48626),time_out,ttn_n\n" +
            ",div,:nrset\n" +
            "from ytrans_VTF_PROHODN_OUT where id=:id;\n" +
            "-----\n" +
            "Insert into VMDB_ST201M (nrdoc,dtdep,ctdep,ctsc1,ctnrdoc,sa)\n" +
            "Select vcod,DEP_DESTINAT,(Select nvl(m.ctsc1,d.ctsc1) from VMDB_ST201M m, VMDB_ST201D d where m.nrdoc=d.nrdoc and m.nrdoc=prikaz_id and d.dtsc=sc)\n" +
            ",(Select nvl(m.ctsc,d.ctstrsc) from VMDB_ST201M m, VMDB_ST201D d where m.nrdoc=d.nrdoc and m.nrdoc=prikaz_id and d.dtsc=sc)\n" +
            ",prikaz_id,0\n" +
            "from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv and prikaz_id=prikaz;\n" +
            "-----\n" +
            "Insert into VMDB_ST201d (nrdoc,ct,dtsc,ctdep,ctsc1,cant,txtcoment,ctcant,rrowid,ctnrdoc,dtcant1,ctcant1,dtnrdoc,suma,ctstrsc,dtstrsc)\n" +
            "Select vcod,d.ct,d.dtsc,d.ctdep,nvl(m.ctsc1,d.ctsc1),nvl(masa_netto,masa_brutto),decode(t.priznak_arm,2,t.nr_vagon,3,t.commentarii)\n" +
            ",nvl(d.cant,d.ctcant),t.id,t.nr_analiz,anlz_vlajn,anlz_sorn,t.sezon_yyyy,d.suma,nvl(m.ctsc,d.ctstrsc),t.TTN_N\n" +
            " from ytrans_VTF_PROHODN_OUT t,VMDB_ST201d d,VMDB_ST201m m,VTF_LABOR_MP l\n" +
            " where t.ttn_n=vttn and d.nrdoc=t.prikaz_id and t.sc=d.dtsc \n" +
            "and d.nrdoc=m.nrdoc and (t.nr_analiz=l.nr_analiz(+) and t.sezon_yyyy=l.sezon_yyyy(+)\n" +
            "and (t.div=vdiv and t.div=l.div(+))) and t.id=:id;\n" +
            "-----\n" +
            "Insert into VMDB01M_VINZ (cod,PRFACT_SERIA,prfact_nr,prfact_data)\n" +
            "Select vcod,substr(ttn_n,1,2),substr(ttn_n,3),ttn_data\n" +
            "from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "-----\n" +
            "Update ytrans_VTF_PROHODN_OUT set nrdoc_out=vCod,PRINT_CHK=NULL where ttn_n=vttn and prikaz_id=prikaz and div=vdiv and nrdoc_out is null;\n" +
            "end if;\n" +
            "end;\n"),
    ACTOUT1("Declare\n" +
            "vCod number;\n" +
            "vnrdoc_out number;\n" +
            "vttn varchar2(20);\n" +
            "vsysfid number;\n" +
            "prikaz number;\n" +
            "vdiv number;\n" +
            "BEGIN\n" +
            "envun4.envsetvalue('SysfIdList_AlwaysEditable', '48613');\n" +
            "Un$div.set_def(:div);\n" +
            "SELECT div,ttn_n INTO vdiv,vttn  FROM ytrans_VTF_PROHODN_OUT t WHERE ID=:ID;\n" +
            "select id_tmdb_docs.nextval into vCod from dual;\n" +
            "---\n" +
            "INSERT INTO vmdb_docs (cod,sysfid,datamanual,nrmanual,div,nrset)\n" +
            "Select vcod,48613,time_out,ttn_n,vdiv,:nrset\n" +
            "from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "-----\n" +
            "Insert into VMDB_ST201M (nrdoc,dtdep)\n" +
            "Select vcod,DEP_DESTINAT from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "---------------------------------------------\n" +
            "Insert into VMDB_ST201d (nrdoc,ct,ct1,dtsc,cant,ctcant,rrowid,ctnrdoc,dtcant1,ctcant1,dtnrdoc)\n" +
            "Select vcod,2163,3,t.sc,t.masa_netto,l.anlz_zernprim,t.id,t.nr_analiz,l.anlz_vlajn,l.anlz_sorn,t.sezon_yyyy\n" +
            " from ytrans_VTF_PROHODN_OUT t,VTF_LABOR_MP l\n" +
            " where t.ttn_n=vttn \n" +
            "and (t.nr_analiz=l.nr_analiz(+) and t.sezon_yyyy=l.sezon_yyyy(+)\n" +
            "and (t.div=vdiv and t.div=l.div(+))) and t.id=:id;\n" +
            "--------------\n" +
            "Insert into VMDB01M_VINZ (cod,PRFACT_SERIA,prfact_nr,prfact_data)\n" +
            "Select vcod,substr(ttn_n,1,2),substr(ttn_n,3),ttn_data\n" +
            "from ytrans_VTF_PROHODN_OUT where id=:id and div=vdiv;\n" +
            "-----\n" +
            "Update ytrans_VTF_PROHODN_OUT set nrdoc_out=vCod,PRINT_CHK=NULL where ttn_n=vttn and div=vdiv  and nrdoc_out is null;\n" +
            "------------\n" +
            "end;");


    private String sql;

    SearchType(String sql) {

        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}

