package com.reporting.service;

import com.reporting.bean.AuthBean;
import com.reporting.dao.ReportDAO;
import com.reporting.model.CustomItem;
import com.reporting.model.CustomUser;
import com.reporting.model.Pair;
import com.reporting.util.WebUtil;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    private void initLang() {
        AuthBean authBean = (AuthBean) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("authBean");
        reportDAO.initLang(authBean.getLocaleCode());
    }

    @Override
    public List<SelectItem> getSeasons(CustomUser user) {
        List<SelectItem> items = new ArrayList<>();
        List<Object> lst = reportDAO.getSeasons(user.getDiv().getId().intValue());
        for (Object s : lst) {
            items.add(new SelectItem(s.toString()));
        }
        return items;
    }

    @Override
    public List<CustomItem> getCultures() {
        initLang();
        List<CustomItem> items = WebUtil.toCustomItemList(reportDAO.getCultures());
        for (CustomItem ci : items) {
            Color c = WebUtil.getRandomColor();
            ci.setName(Integer.toHexString(c.getRGB()).substring(2));
        }
        return items;
    }

    @Override
    public List<CustomItem> getElevators() {
        List items = reportDAO.getElevators();
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<Object> getReportContracts(int season, CustomItem region, CustomItem sc) {
        initLang();
        int sc_mp = sc == null ? 0 : sc.getId().intValue();
        return reportDAO.getReportContracts(season, region.getId().intValue(), sc_mp);
    }

    @Override
    public List<Object> getReportContractsByCulture(int season, CustomItem region, CustomItem sc) {
        initLang();
        return reportDAO.getReportContractsByCulture(season, region.getId().intValue(), sc.getId().intValue());
    }

    @Override
    public List<Object> getReportManagersOnline(int season, CustomItem region, boolean isManager, CustomItem sc) {
        Date d1 = WebUtil.strInDate("01.01." + season);
        Date d2 = WebUtil.strInDate("30.06." + (season + 1));
        int sc_mp = sc == null ? 0 : sc.getId().intValue();
        return reportDAO.getReportManagersOnline(season, region.getId().intValue(), isManager, sc_mp, new Pair<>(d1, d2));
    }

    @Override
    public List<Object> getReportPayment(int season, CustomItem region, CustomItem sc) {
        int sc_mp = sc == null ? 0 : sc.getId().intValue();
        return reportDAO.getReportPayment(season, region.getId().intValue(), sc_mp);
    }

    @Override
    public List<Object> getSilosGroupSold(Integer season, CustomItem region, CustomItem culture) {
        initLang();
        return reportDAO.getSilosGroupSold("01.01." + season, region.getId().intValue(), culture.getId().intValue());
    }

    @Override
    public List<Object> getSilosSoldValues(Integer season, CustomItem region, CustomItem culture) {
        initLang();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        List<Object> data = reportDAO.getSilosSoldValues("01.01." + season, region.getId().intValue(), culture.getId().toString());
        Map<CustomItem, Object[]> map = new LinkedHashMap<>();

        for (Object r : data) {

            Object[] row = (Object[]) r;
            CustomItem silos = new CustomItem(row[1], row[2]);
            CustomItem client = new CustomItem(row[3], row[4]);

            Object[] newRow;

            if (map.containsKey(silos)) {
                newRow = map.get(silos);
                /*client*/

                List<Object[]> clients = (List<Object[]>) newRow[1];
                Object[] clientRow = new Object[6];
                clientRow[0] = client;
                clientRow[1] = row[5];

                List<Object> lastContract = reportDAO.getLastContractByClient(silos.getId().intValue(), client.getId().intValue(), season, culture.getId().intValue());
                if (!lastContract.isEmpty()) {
                    Object[] item = (Object[]) lastContract.get(0);
                    clientRow[2] = item[0];
                    clientRow[3] = item[1];
                    clientRow[4] = item[2];
                }

                clientRow[5] = reportDAO.getHistoryByClient(silos.getId().intValue(), client.getId().intValue(), culture.getId().intValue());

                clients.add(clientRow);

                /*CHART*/

                PieChartModel pie = (PieChartModel) newRow[2];
                pie.set(client.toString(), (Number) row[5]);



                newRow[3] = reportDAO.getLastContractsBySilos(silos.getId().intValue(), season, culture.getId().intValue(), 5);

            } else {
                newRow = new Object[7];
                newRow[0] = silos;

                /*client */

                List<Object[]> clients = new ArrayList<>();
                Object[] clientRow = new Object[6];
                clientRow[0] = client;
                clientRow[1] = row[5];

                List<Object> lastContract = reportDAO.getLastContractByClient(silos.getId().intValue(), client.getId().intValue(), season, culture.getId().intValue());
                if (!lastContract.isEmpty()) {
                    Object[] item = (Object[]) lastContract.get(0);
                    clientRow[2] = item[0];
                    clientRow[3] = item[1];
                    clientRow[4] = item[2];
                }

                clientRow[5] = reportDAO.getHistoryByClient(silos.getId().intValue(), client.getId().intValue(), culture.getId().intValue());

                clients.add(clientRow);
                newRow[1] = clients;



                /*CHART*/

                PieChartModel pie = new PieChartModel();
                pie.setShowDataLabels(true);
                pie.set(client.toString(), (Number) row[5]);
                newRow[2] = pie;



                newRow[3] = reportDAO.getLastContractsBySilos(silos.getId().intValue(), season, culture.getId().intValue(), 5);

                List<Object> silosPrice = reportDAO.getCulturePriceBySilos(silos.getId().intValue(), season, culture.getId().intValue());
                if (!silosPrice.isEmpty()) {
                    Object[] item = (Object[]) silosPrice.get(0);
                    newRow[4] = item[0];
                    newRow[5] = df.format((Date) item[1]);
                    newRow[6] = item[2];
                }
            }

            map.put(silos, newRow);
        }

        List<Object> lst = new ArrayList<>();
        for (Object obj : map.values()) {
            lst.add(obj);
        }
        return lst;
    }

    @Override
    public List<CustomItem> getRegions() {
        List items = reportDAO.getRegions();
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<Object> getContractByDistrict(int season, int sc) {
        return reportDAO.getContractByDistrict(season, sc);
    }

    @Override
    public List<Object> getContractByDistrictDetail(int season, int region, int sc) {
        initLang();
        return reportDAO.getContractByDistrictDetail(season, region, sc);
    }

    @Override
    public double[] getCoordinatesByCod(String tip, Integer regionId) {
        return reportDAO.getCoordinatesByCod(tip, regionId);
    }

    @Override
    public void saveCoordinates(String tip, Integer regionId, double lat, double lng) {
        reportDAO.saveCoordinates(tip, regionId, lat, lng);
    }

    @Override
    public List<Object> getOurElevators() {
        return reportDAO.getOurElevators();
    }

    @Override
    public List<Object> getAnyElevators() {
        return reportDAO.getAnyElevators();
    }

    @Override
    public List<Object> getBuyPlaces(int season, int sc) {
        return reportDAO.getBuyPlaces(season, sc);
    }

    @Override
    public List<Object> getPlacesConnections(int season, int sc) {
        return reportDAO.getPlacesConnections(season, sc);
    }

    @Override
    public List<Object> getComodityContractedByDate(Date d1, Date d2, CustomItem sc, CustomItem dep){
        initLang();
        int scParam =  sc != null ? sc.getId().intValue() : 0;
        int depParam =  dep != null ? dep.getId().intValue() : 0;
        return reportDAO.getComodityContractedByDate(d1, d2, scParam, depParam);
    }

    @Override
    public List<Object> getComodityByElevator(Date d1, Date d2, CustomItem sc){
        initLang();
        return reportDAO.getComodityByElevator(d1,d2, sc.getId().intValue());
    }
}


