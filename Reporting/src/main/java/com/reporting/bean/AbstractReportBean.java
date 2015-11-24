package com.reporting.bean;

import com.reporting.model.CustomItem;
import com.reporting.service.ReportService;

import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by sharlamov on 19.11.2015.
 */
public abstract class AbstractReportBean extends AbstractBean {

    @ManagedProperty(value = "#{reportServiceImpl}")
    private ReportService reportService;

    private List<SelectItem> seasons;
    private Integer season;
    private List<CustomItem> cultures;
    private CustomItem culture;
    private List<CustomItem> regions;
    private CustomItem region;
    private String[] selectedPoints;

    public abstract void applyFilters();

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public List<SelectItem> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SelectItem> seasons) {
        this.seasons = seasons;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public List<CustomItem> getCultures() {
        return cultures;
    }

    public void setCultures(List<CustomItem> cultures) {
        this.cultures = cultures;
    }

    public CustomItem getCulture() {
        return culture;
    }

    public void setCulture(CustomItem culture) {
        this.culture = culture;
    }

    public List<CustomItem> getRegions() {
        return regions;
    }

    public void setRegions(List<CustomItem> regions) {
        this.regions = regions;
    }

    public CustomItem getRegion() {
        return region;
    }

    public void setRegion(CustomItem region) {
        this.region = region;
    }

    public String[] getSelectedPoints() {
        return selectedPoints;
    }

    public void setSelectedPoints(String[] selectedPoints) {
        this.selectedPoints = selectedPoints;
    }
}
