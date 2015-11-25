package com.reporting.service;

import com.reporting.model.CustomItem;
import com.reporting.model.CustomUser;

import javax.faces.model.SelectItem;
import java.util.Collection;
import java.util.List;

public interface ReportService {

    List<SelectItem> getSeasons(CustomUser user);

    List<CustomItem> getCultures();

    List<Object> getReportContracts(int season, CustomItem region, CustomItem sc);

    List<Object> getReportContractsByCulture(int season, CustomItem region, CustomItem sc);

    List<Object> getReportManagersOnline(int season, CustomItem region, boolean isManager, CustomItem sc);

    List<Object> getReportPayment(int season, CustomItem region, CustomItem sc);

    List<Object> getSilosSoldValues(Integer season, CustomItem region, CustomItem culture);

    List<CustomItem> getRegions();

    List<Object> getContractByDistrict(int season, int sc);

    List<Object> getContractByDistrictDetail(int season, int region, int sc);

    double[] getCoordinatesByCod(String tip, Integer regionId);

    void saveCoordinates(String tip, Integer regionId, double lat, double lng);

    List<Object> getOurElevators();

    List<Object> getAnyElevators();

    List<Object> getBuyPlaces(int season, int sc);

    List<Object> getPlacesConnections(int season, int sc);
}
