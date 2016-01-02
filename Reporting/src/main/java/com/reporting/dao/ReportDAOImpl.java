package com.reporting.dao;

import com.reporting.util.MapUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public class ReportDAOImpl extends AbstractDAOImpl implements ReportDAO {

    @Override
    public void initLang(String lang) {
        int param = 2;
        if (lang.equals("en")) {
            param = 0;
        } else if (lang.equals("ro")) {
            param = 1;
        }
        Query updateQuery = currentSession().createSQLQuery(" begin envun4.envsetvalue('UNLANG',:param); end; ");
        updateQuery.setParameter("param", param).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Object> getSeasons(int divId) {
        String sql = "select sezon_yyyy from yvtrans_sezon a where div=:div";
        return currentSession().createSQLQuery(sql).setInteger("div", divId).list();
    }

    @Override
    public List getCultures() {
        String sql = "select cod, rdb_codvechi, upper(langText) from vms_univers_lang where tip in ('M','P') and gr1 in ('2171','2161','2111') and cod not in (211899, 50367, 38320) and gr2='W' and  isarhiv is null";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List getElevators() {
        String sql = "select cod, langText as \"denumirea\" from vms_univers_lang where tip='O' and gr1='I' and gr2='E' and  isarhiv is null";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List<Object> getReportContracts(int season, int region, int sc) {
        String predicate = sc == 0 ? "" : " and sc_mp = " + sc;
        String sql = "select sum(suma_contract) over (partition by manager) all_sum " +
                ",manager,case when manager = -1 then 'Call center' else (select initcap(lower(langText))from vms_univers_lang where cod = manager) end clcmanagert  " +
                ",sc_mp,(select initcap(lower(langText)) from vms_univers_lang where cod = sc_mp)clcsc_mpt " +
                ",suma_contract from ( " +
                "select manager,sc_mp, nvl(sum(zacontractovano),0)  suma_contract " +
                "from web_clients_mpfs0m c, tmdb_docs d where sezon_yyyy =:season " + predicate + " and d.cod = c.CONTRACTID and sysfid = 48701 and d.div <> 97623 " +
                "and exists (select * from vregion_settings where sc_elevator = elevator and region = :region) " +
                "group by manager, sc_mp)a " +
                "order by 1 ";
        return currentSession().createSQLQuery(sql)
                .setInteger("season", season)
                .setInteger("region", region).list();
    }

    @Override
    public List<Object> getReportContractsByCulture(int season, int region, int sc) {
        String sql = "select sum (suma_contract) over(partition by manager) all_sum " +
                ", manager,case when manager = -1 then 'Call center'else(select initcap(lower(langText)) " +
                "from vms_univers_lang where cod = manager)end clcmanagert " +
                ",case when suma_contract < suma_otpis then 0 else suma_contract - suma_otpis end suma_contract " +
                ",suma_otpis " +
                "from(select manager, nvl(sum(zacontractovano), 0)suma_contract " +
                ", round(nvl(sum((select sum(cant)from vmdb_cmr cm where ct = 9421and cm.ctnrdoc = c.nrdoc1)), 0), 3) suma_otpis " +
                "from web_clients_mpfs0m c, tmdb_docs d where sezon_yyyy =:season and sc_mp = :sc and d.cod = c.CONTRACTID " +
                "and sysfid = 48701 and d.div <> 97623 " +
                "and exists (select * from vregion_settings where sc_elevator = elevator and region =:region)group by manager " +
                ")a order by 1 ";

        return currentSession().createSQLQuery(sql)
                .setParameter("season", season)
                .setParameter("sc", sc)
                .setParameter("region", region).list();
    }

    @Override
    public List<Object> getReportManagersOnline(int season, int region, boolean isManager, int sc, Date d1, Date d2) {
        String predicate = sc == 0 ? "" : " and sc_mp = " + sc + " ";
        String client = isManager ? "manager" : "clientid";

        String sql = "select " + client + ", initcap(lower(clc" + client + "t)) \n" +
                ",sum(zacontractovano)zacontractovano \n" +
                ",sum(decode(price_in,0,0,round(suma_paid/price_in,2)))cant_paid \n" +
                ",sum(suma_paid)suma_paid \n" +
                ",case when sum(zacontractovano) - sum(decode(price_in,0,0,round(suma_paid/price_in,2))) < 0 then 0 else sum(zacontractovano) - sum(decode(price_in,0,0,round(suma_paid/price_in,2))) end cant_ordered\n" +
                ",case when sum(zacontractovano * price_in) - sum(suma_paid) < 0 then 0 else sum(zacontractovano * price_in) - sum(suma_paid) end suma_ordered \n" +
                "from( select m.nrdoc1 contract," + client + ", clc" + client + "t ,nvl(price_in,0)price_in \n" +
                ",sum(zacontractovano)zacontractovano \n" +
                ",(select sum(suma)from vmdb_cmr where dt=9431 and dtnrdoc=m.nrdoc1 and dtsc in (4962,4963) and data between :d1 and :d2)suma_paid \n" +
                "from web_clients_mpfs0m m,tmdb_docs d \n" +
                "where d.cod = m.CONTRACTID and d.sysfid=48701 and d.div <> 97623 and sezon_yyyy=:season " + predicate +
                "and exists (select * from vregion_settings where sc_elevator = elevator and region = :region) \n" +
                "and datastart between :d1 and :d2 \n" +
                "group by m.nrdoc1," + client + ", clc" + client + "t ,nvl(price_in,0) having sum(zacontractovano)>0 )a \n" +
                "group by " + client + ", clc" + client + "t  order by zacontractovano desc";
        return currentSession().createSQLQuery(sql)
                .setDate("d1", d1).setDate("d2", d2)
                .setInteger("season", season)
                .setInteger("region", region).list();
    }

    @Override
    public List<Object> getReportPayment(int season, int region, int sc) {
        String predicate = sc == 0 ? "" : " and sc_mp = " + sc;
        String sql = "select data_alccontr, SUM (contract) OVER (ORDER BY data_alccontr) contract" +
                ", SUM (paid) OVER (ORDER BY data_alccontr) paid " +
                ", SUM (paid1) OVER (ORDER BY data_alccontr) paid1 " +
                "from( " +
                "select data_alccontr, nvl(sum(zacontractovano),0) contract " +
                ",round(nvl(sum((select sum(suma) from vmdb_cmr cm where dt = 9431 and dtsc in (4962, 4963) and cm.dtnrdoc = c.nrdoc1)/price_in ),0),3) paid " +
                ",round(nvl(sum((select sum(cant) from vmdb_cmr cm where ct = 9421 and cm.ctnrdoc = c.nrdoc1) ),0),3) paid1 " +
                "from YTCN1D_CLIENTS_MPFS0M c, tmdb_docs d " +
                "where sezon_yyyy = :season and d.cod = c.contractid " + predicate + " and sysfid=48701 and d.div <> 97623 " +
                "and exists (select * from vregion_settings where sc_elevator = elevator and region = :region) " +
                "group by data_alccontr)";
        return currentSession().createSQLQuery(sql)
                .setInteger("season", season)
                .setInteger("region", region).list();

    }

    @Override
    public List<Object> getSilosGroupSold(String date, int region, int sc) {
        String sqlRegion = "select elevator from vregion_settings where region = " + region;
        String sql0 = "BEGIN UN$SLD.MAKE('','E1','CDEK1',pcont=>'9211', psc1 => :region, pAn_Data=>:d1); END;";
        Query updateQuery = currentSession().createSQLQuery(sql0);
        updateQuery.setParameter("d1", date).setParameter("region", sqlRegion).executeUpdate();

        String sql1 = "select sc, (select langtext from vms_univers_lang where cod = sc),\n" +
                "elevator, (select langtext from vms_univers_lang where cod = elevator) clcelevatort, dep, sum(cant)\n" +
                "from ( select sc,sc1 elevator,\n" +
                "decode((select count(*) from vms_univers where tip = 'O' and gr1 = 'DIV' and codi = x.dep),0, 'Trans Oil Group', 'Strangers siloses') dep,cant\n" +
                "from xsld x\n" +
                "where id = 'E1'\n" +
                "and exists (select * from vms_univers where cod = x.sc1 and gr1s = x.div)\n" +
                "and nvl (cant, 0) <> 0)\n" +
                "group by sc, elevator, dep order by sc, elevator, dep ";
        return currentSession().createSQLQuery(sql1).list();

    }

    @Override
    public List<Object> getSilosSoldValues(String d1, int region, String sc) {
        String sqlRegion = "select elevator from vregion_settings where region = " + region;
        String sql0 = "BEGIN UN$SLD.MAKE('','E1','DEK1',pcont=>'9211',psc=>:sc, psc1 => :region, pAn_Data=>:d1); END;";
        Query updateQuery = currentSession().createSQLQuery(sql0);
        updateQuery.setParameter("d1", d1).setParameter("region", sqlRegion).setParameter("sc", sc).executeUpdate();

        String sql1 = "select sum(cant) over (partition by elevator) total, x.* from " +
                "(select elevator,(select langText from vms_univers_lang where cod = elevator) clcelevatort " +
                ",dep , nvl((select langText from vms_univers_lang where cod = dep),'Trans Oil Group') clcdept " +
                ",sum(cant) cant from (  " +
                "select sc1 elevator " +
                ",nvl((select min(-1) from vms_univers where tip='O' and gr1='DIV' and codi = x.dep), dep) dep ,cant  " +
                "from xsld x where id = 'E1' " +
                "and exists (select * from vms_univers where cod = x.sc1 and gr1s = x.div) and nvl(cant,0) <> 0) " +
                "group by elevator , dep) x";
        return currentSession().createSQLQuery(sql1).list();
    }

    @Override
    public List<Object> getLastContractByClient(Integer silos, Integer client, int season, int sc) {
        String sql = "select * from ( " +
                "select nr_manual, zacontractovano, (select langText from vms_univers_lang where cod = manager) vName " +
                ",row_number() over(partition by sc_elevator, clientid order by data_alccontr desc ,zacontractovano desc) rn " +
                "from YTCN1D_CLIENTS_MPFS0M c, tmdb_docs d " +
                "where sezon_yyyy = :season and d.cod = c.contractid and sysfid=48701 and d.div <> 97623 and sc_elevator = :silos and clientid = :client  and sc_mp = :sc and  CONTRACT_TYPE_FS_1 = 38 " +
                ") where rn = 1 ";
        return currentSession().createSQLQuery(sql)
                .setInteger("silos", silos)
                .setInteger("client", client)
                .setInteger("season", season)
                .setInteger("sc", sc)
                .list();
    }

    @Override
    public List<Object> getHistoryByClient(Integer silos, Integer client, int sc) {
        String sql = "select * from (  " +
                "select dtnum1, wm_concat(clcdtsc3t) mng from ( " +
                "select distinct dtsc0, dtnum1, clcdtsc3t  from vmdb_reg_a  " +
                "where exists (select 1 from tmdb_docs where cod = nrdoc and sysfid = 7000) " +
                "and dtsc2 = :sc and dtsc0 = :client " +
                "order by dtnum1 desc,dtsc0) " +
                "group by dtsc0, dtnum1) " +
                "where mng is not null ";
        return currentSession().createSQLQuery(sql)
                .setInteger("client", client)
                .setInteger("sc", sc)
                .list();
    }

    @Override
    public List<Object> getCulturePriceBySilos(Integer silos, int season, int sc) {
        String sql = "select * from ( select id, datastart, pret, row_number() over(order by datastart desc) rn " +
                "from VMS_PRIKAZ where sc_mp = :sc and sc_elevator = :silos " +
                "order by datastart desc) where rn = 1 ";
        return currentSession().createSQLQuery(sql)
                .setInteger("silos", silos)
                .setInteger("sc", sc)
                .list();
    }

    @Override
    public List<Object> getLastContractsBySilos(Integer silos, int season, int sc, int count) {
        String sql = "select * from ( " +
                "select data_alccontr, price_in, zacontractovano, " +
                "(select langText from vms_univers_lang where cod = clientId), " +
                "(select langText from vms_univers_lang where cod = manager), " +
                "row_number() over(order by data_alccontr desc ,zacontractovano desc) rn " +
                "from YTCN1D_CLIENTS_MPFS0M c, tmdb_docs d where sezon_yyyy = :season and d.cod = c.contractid and sysfid=48701 and d.div <> 97623 and sc_elevator = :silos and sc_mp = :sc and  CONTRACT_TYPE_FS_1 = 38 " +
                ") where rn < :count ";
        return currentSession().createSQLQuery(sql)
                .setInteger("silos", silos)
                .setInteger("sc", sc)
                .setInteger("season", season)
                .setInteger("count", count + 1)
                .list();
    }

    @Override
    public List getRegions() {
        String sql = "select cod1 cod, langText denumirea from vms_syss_lang where tip='S' and cod='55' and cod1 <> 0";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List<Object> getContractByDistrict(int season, int sc) {
        String sql = "select raion\n" +
                ",(select langText from vms_syss_lang where tip='S' and cod=25 and cod1 = raion), sum(contract), sum(masa_netto)/1000\n" +
                ",(select urojai from YTRANS_TMS_RAION_UROJAI_SC where raion_s_25 = raion and sc = :sc and datastart between '01.07.' || :season and '30.06.' || (:season + 1) and rownum = 1)\n" +
                "from (\n" +
                "select (select number2 from vms_syss where tip='S' and cod='12' and cod1 = raion) raion,contract,masa_netto \n" +
                "from (\n" +
                "select ppogruz_s_12 raion, null contract,masa_netto  from vtf_prohodn_mpfs where sezon_yyyy = :season and sc_mp = :sc and priznak_arm = 1 \n" +
                "union all\n" +
                "SELECT raion, null contract, cant_accves FROM Ytrans_VMDB_MPFS_ttn where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48315 and doccolor is null and cod=nrdoc)\n" +
                "union all\n" +
                "SELECT raion, null contract, CANT_ACCVES_FACT  FROM Ytrans_VMDB_MPFS where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48309 and doccolor is null and cod=nrdoc)\n" +
                "union all\n" +
                "select oras_from, (select zacontractovano from YTCN1D_CLIENTS_MPFS0M c where c.nrdoc1 = (select contract_id2 from yvtrans_contract_application a where a.nrdoc = p.contract_id and rownum = 1)) contract, null \n" +
                "from yvtrans_purchase_process p where oras_from is not null and sezon_yyyy = :season and sc = :sc \n" +
                "))group by raion having sum(masa_netto) >= 0\n" +
                "order by 4 desc";

        return currentSession().createSQLQuery(sql)
                .setInteger("season", season)
                .setInteger("sc", sc)
                .list();
    }

    @Override
    public List<Object> getContractByDistrictDetail(int season, int region, int sc) {
        //updateCoordinates();
        String sql = "select \n" +
                "raion, \n" +
                "(select langText from vms_syss_lang where tip='S' and cod=12 and cod1 = raion) raion_name,\n" +
                "(select langText from vms_univers_lang where cod = agent) agent_name,\n" +
                "sum(masa_netto)/1000,\n" +
                "sum(masac) \n" +
                "from (\n" +
                "select ppogruz_s_12 raion, nvl(dep_hoz,dep_gruzootpr) agent, masa_netto, null masac  from vtf_prohodn_mpfs where sezon_yyyy = :season and sc_mp = :sc and priznak_arm = 1 \n" +
                "and exists (select * from vms_syss where tip='S' and cod = 12 and cod1 = ppogruz_s_12 and number2 = :region)\n" +
                "union all\n" +
                "SELECT raion, dep_gruzootpr, cant_accves, null masac  FROM ytrans_vmdb_mpfs_ttn where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48315 and doccolor is null and cod=nrdoc)\n" +
                "and exists (select * from vms_syss where tip='S' and cod = 12 and cod1 = raion and number2 = :region)\n" +
                "union all\n" +
                "SELECT raion, dep_gruzootpr, CANT_ACCVES_FACT, null masac  FROM Ytrans_VMDB_MPFS where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48309 and doccolor is null and cod=nrdoc)\n" +
                "and exists (select * from vms_syss where tip='S' and cod = 12 and cod1 = raion and number2 = :region)\n" +
                "union all\n" +
                "select oras_from, dep, null, \n" +
                "(select zacontractovano from YTCN1D_CLIENTS_MPFS0M c where c.nrdoc1 = (select contract_id2 from yvtrans_contract_application a where a.nrdoc = p.contract_id and rownum = 1)) contract \n" +
                "from yvtrans_purchase_process p where oras_from is not null and sezon_yyyy = :season and sc = :sc\n" +
                "and exists (select * from vms_syss where tip='S' and cod = 12 and cod1 = oras_from and number2 = :region) \n" +
                ")\n" +
                "group by raion, agent\n" +
                "order by 2, 3\n";
        return currentSession().createSQLQuery(sql)
                .setInteger("season", season)
                .setInteger("region", region)
                .setInteger("sc", sc)
                .list();
    }

    @Override
    public double[] getCoordinatesByCod(String tip, Integer regionId) {
        String sql = "select lat, lng from yplace_coordinates a where tip=:tip and cod = :cod";
        Object obj = currentSession().createSQLQuery(sql)
                .setParameter("tip", tip)
                .setParameter("cod", regionId)
                .uniqueResult();
        if (obj != null) {
            Object[] row = (Object[]) obj;
            double[] res = new double[2];
            res[0] = ((BigDecimal) row[0]).doubleValue();
            res[1] = ((BigDecimal) row[1]).doubleValue();
            return res;
        } else
            return null;
    }

    @Override
    public void saveCoordinates(String tip, Integer regionId, double lat, double lng) {
        String sql = "insert into yplace_coordinates(tip, cod, lat, lng) values(:tip, :cod, :lat, :lng) ";

        Query updateQuery = currentSession().createSQLQuery(sql);
        updateQuery.setParameter("tip", tip)
                .setParameter("cod", regionId)
                .setParameter("lat", lat)
                .setParameter("lng", lng);
        updateQuery.executeUpdate();
    }


    private void updateCoordinates() {
        String sql = "select 'S12', cod1, denumirea, nmb2t, NMB1T from vms_syss s where tip = 'S' and cod = '12' and number1 = 3 and  denumirea is not null and nmb2t  is not null and NMB1T  is not null\n" +
                "and not exists (select * from YPLACE_VCOORDINATES u where u.cod=s.cod1 and tip='S12')";

        List list = currentSession().createSQLQuery(sql).list();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            double[] point = getCoordinatesByCod(row[0].toString(), ((BigDecimal) row[1]).intValue());
            if (point == null) {
                point = MapUtil.getGeoCode(row[4].toString(), row[3].toString(), row[2].toString());
                if (point != null)
                    saveCoordinates(row[0].toString(), ((BigDecimal) row[1]).intValue(), point[0], point[1]);
            }
        }
    }

    @Override
    public List<Object> getOurElevators() {
        String sql = "select s.elevator, s.clcelevatort, lat, lng from \n" +
                "vregion_settings s, YPLACE_COORDINATES c\n" +
                "where c.tip = 'UNIV' and s.elevator = c.cod and s.our = 'P'\n";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List<Object> getAnyElevators() {
        String sql = "select s.elevator, s.clcelevatort, lat, lng from \n" +
                "vregion_settings s, YPLACE_COORDINATES c\n" +
                "where c.tip = 'UNIV' and s.elevator = c.cod and s.our is null\n";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List<Object> getBuyPlaces(int season, int sc) {
        String sql = "select cod,place_name,lat,lng from YPLACE_VCOORDINATES c where tip='S12' and c.cod in (\n" +
                "select ppogruz_s_12 from vtf_prohodn_mpfs where sezon_yyyy = :season and sc_mp = :sc \n" +
                "union all\n" +
                "SELECT raion FROM Ytrans_VMDB_MPFS_ttn where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48315 and doccolor is null and cod=nrdoc)\n" +
                "union all\n" +
                "SELECT raion FROM Ytrans_VMDB_MPFS where sezon_yyyy = :season and sc_mp = :sc and raion is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48309 and doccolor is null and cod=nrdoc)\n" +
                ")";
        return currentSession().createSQLQuery(sql).setParameter("season", season).setParameter("sc", sc).list();
    }

    @Override
    public List<Object> getPlacesConnections(int season, int sc) {
        String sql = "select distinct * from (\n" +
                "select a.* \n" +
                ",(select place_name from YPLACE_VCOORDINATES where tip='S12' and cod=raion) from_name\n" +
                ",(select lat from YPLACE_VCOORDINATES where tip='S12' and cod=raion) from_lat\n" +
                ",(select lng from YPLACE_VCOORDINATES where tip='S12' and cod=raion) from_lng\n" +
                ",(select place_name from YPLACE_VCOORDINATES where tip='UNIV' and cod=elevator) into_name\n" +
                ",(select lat from YPLACE_VCOORDINATES where tip='UNIV' and cod=elevator) into_lat\n" +
                ",(select lng from YPLACE_VCOORDINATES where tip='UNIV' and cod=elevator) into_lng\n" +
                "from (\n" +
                "select distinct ppogruz_s_12 raion,elevator\n" +
                "from vtf_prohodn_mpfs where sezon_yyyy = :season and sc_mp = :sc and ppogruz_s_12 is not null and elevator is not null\n" +
                "union all\n" +
                "SELECT raion,dep_mp FROM Ytrans_VMDB_MPFS_ttn where sezon_yyyy = :season and sc_mp = :sc and raion is not null and dep_mp is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48315 and doccolor is null and cod=nrdoc)\n" +
                "union all\n" +
                "SELECT raion,dep_mp FROM Ytrans_VMDB_MPFS where sezon_yyyy = :season and sc_mp = :sc and raion is not null and dep_mp is not null\n" +
                "and not exists (select * from vms_univers where tip='O' and gr1 = 'DIV' and codi in (dep_postav,dep_mp))\n" +
                "and exists( select * from tmdb_docs where sysfid = 48309 and doccolor is null and cod=nrdoc)\n" +
                ")a) where from_lat is not null and into_lat is not null";
        return currentSession().createSQLQuery(sql).setParameter("season", season).setParameter("sc", sc).list();
    }

    @Override
    public List<Object> getComodityContractedByDate(Date d1, Date d2, int sc, int dep) {
        String depTerm = dep != 0 ? " AND sc_elevator = " + dep + "\n" : "";
        String scTerm = sc != 0 ? " AND sc_mp = " + sc + "\n" : "";
        String sql = "SELECT DATA, sc,( select initCap(langText) from vms_univers_lang where cod = sc) clcsct,SUM(cant) cant\n" +
                "FROM ( SELECT sc_mp sc,datastart DATA, NVL(ZACONTRACTOVANO,tonaj_plan)  cant\n" +
                "FROM YVCN1D_CLIENTS_MPFS0M m, vmdb_docs d\n" +
                "WHERE d.cod=m.contractid AND sysfid=48701 and d.div <> 97623  AND CONTRACT_TYPE_FS_1 IN (15,38) \n" +
                "     AND m.datastart BETWEEN :d1 AND :d2 " + depTerm + scTerm +
                ")group by data, sc\n" +
                "order by 1";

        return currentSession().createSQLQuery(sql)
                .setParameter("d1", d1)
                .setParameter("d2", d2)
                .list();
    }

    @Override
    public List<Object> getComodityByElevator(Date d1, Date d2, int sc) {
        String sql = "    SELECT sc_elevator,( select initCap(langText) from vms_univers_lang where cod = sc_elevator) clcsc_elevatort,SUM(cant) cant\n" +
                "    FROM ( SELECT sc_elevator , NVL(ZACONTRACTOVANO,tonaj_plan)  cant\n" +
                "    FROM YVCN1D_CLIENTS_MPFS0M m, vmdb_docs d\n" +
                "    WHERE d.cod=m.contractid AND sysfid = 48701 and d.div <> 97623  AND CONTRACT_TYPE_FS_1 IN (15,38)\n" +
                "    AND m.datastart BETWEEN :d1 AND :d2 and sc_mp = :sc\n" +
                "    )group by sc_elevator\n" +
                "    order by 3";

        return currentSession().createSQLQuery(sql)
                .setParameter("d1", d1)
                .setParameter("d2", d2)
                .setParameter("sc", sc)
                .list();
    }

    @Override
    public List<Object> getFirstLevelMenu() {
        String sql = "select cod, pcod, name_en pname, desc_en pdesc, image, reference pref from web_tmenu where pcod is null";
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public List<Object> getChildrenMenu(int pid) {
        String sql = "select cod, pcod, name_en pname, desc_en pdesc, image, reference pref from web_tmenu where pcod = :pid ";
        return currentSession().createSQLQuery(sql)
                .setParameter("pid", pid).list();
    }
}
