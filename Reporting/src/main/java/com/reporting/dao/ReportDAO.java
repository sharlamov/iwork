package com.reporting.dao;

import com.reporting.model.Pair;

import java.util.Date;
import java.util.List;

public interface ReportDAO {
    void initLang(String lang);

    List<Object> getSeasons(int divId);

    List getCultures();

    /*List getElevators();

    List<Object> getReportPrices(int season, long culture, String elevators);

    List getFilials();*/

    List<Object> getReportContracts(int season, int region, int sc);

    List<Object> getReportContractsByCulture(int season, int region, int sc);

    List<Object> getReportManagersOnline(int season, int region, boolean isManager, int sc, Pair<Date, Date> period);

    List<Object> getReportPayment(int season, int region, int sc);

    List<Object> getSilosSoldValues(String d1, int region, String sc);

    List<Object> getLastContractByClient(Integer silos, Integer client, int season, int sc);

    List<Object> getHistoryByClient(Integer silos, Integer client, int sc);

    List<Object> getCulturePriceBySilos(Integer silos, int season, int sc);

    List<Object> getLastContractsBySilos(Integer silos, int season, int sc, int count);

    List getRegions();

    List<Object> getContractByDistrict(int season, int sc);

    List<Object> getContractByDistrictDetail(int season, int region, int sc);

    double[] getCoordinatesByCod(String tip, Integer regionId);

    void saveCoordinates(String tip, Integer regionId, double lat, double lng);

    List<Object> getOurElevators();

    List<Object> getAnyElevators();

    List<Object> getBuyPlaces(int season, int sc);

    List<Object> getPlacesConnections(int season, int sc);
}