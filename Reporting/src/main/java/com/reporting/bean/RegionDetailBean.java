package com.reporting.bean;

import com.reporting.model.CustomItem;
import com.reporting.service.ReportService;
import com.reporting.util.MapUtil;
import com.reporting.util.WebUtil;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.model.map.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ManagedBean(name = "regionDetailBean")
@ViewScoped
public class RegionDetailBean extends AbstractReportBean {

    private Integer season;
    private Integer regionId;
    private String regionName;
    private List<Object> detail;
    private MapModel geoMap;
    private String centerGeoMap = "41.850033, -87.6500523";

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        season = Integer.valueOf(params.get("season"));
        regionId = Integer.valueOf(params.get("regionId"));
        regionName = params.get("regionName");

        setCultures(getReportService().getCultures());
        setCulture(getCultures().get(getCultures().indexOf(new CustomItem(new BigDecimal(params.get("cid")), ""))));
        applyFilters();
    }

    @Override
    public void applyFilters() {
        detail = getReportService().getContractByDistrictDetail(season, regionId, getCulture().getId().intValue());
        addMapMarkers();
    }


    public void addMapMarkers() {
        geoMap = new DefaultMapModel();

        int n = regionName.indexOf('/');
        if (n != -1) {
            regionName = regionName.substring(0, n).trim();
        }
        double[] point = getReportService().getCoordinatesByCod("S25", regionId);
        if (point == null) {
            point = MapUtil.getGeoCode("moldova", "raion " + regionName, "");
            if (point != null)
                getReportService().saveCoordinates("S25", regionId, point[0], point[1]);
        }
        if (point != null) {
            centerGeoMap = point[0] + ", " + point[1];

            Set<Object> mapAddresses = new HashSet<>();
            for (Object obj : detail) {
                Object row[] = (Object[]) obj;
                String target = row[1].toString();
                if (!mapAddresses.contains(target)) {
                    int rid = ((BigDecimal) row[0]).intValue();
                    double[] place = getReportService().getCoordinatesByCod("S12", rid);
                    if (place == null) {
                        place = MapUtil.getGeoCode("moldova", "raion " + regionName, target);
                        getReportService().saveCoordinates("S12", rid, place[0], place[1]);
                    }

                    LatLng crd = new LatLng(place[0], place[1]);
                    geoMap.addOverlay(new Marker(crd, target));
                    mapAddresses.add(target);
                }
            }
        }
    }

    public double getSumaByRegion() {
        double res = 0d;
        for (Object obj : detail) {
            Object row[] = (Object[]) obj;
            if(row[3] != null)
                res += Double.valueOf(row[3].toString());
        }
        return WebUtil.round(res);
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public List<Object> getDetail() {
        return detail;
    }

    public void setDetail(List<Object> detail) {
        this.detail = detail;
    }

    public MapModel getGeoMap() {
        return geoMap;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public void setCenterGeoMap(String centerGeoMap) {
        this.centerGeoMap = centerGeoMap;
    }

    public String getRegionName() {
        return regionName;
    }
}