package com.reporting.bean;

import com.reporting.model.CustomItem;
import com.reporting.service.ReportService;

import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractReportBean implements Serializable {

    @ManagedProperty(value = "#{reportServiceImpl}")
    private ReportService reportService;

    @ManagedProperty(value = "#{authBean}")
    private AuthBean authBean;

    @ManagedProperty("#{msg}")
    private ResourceBundle bundle;

    private List<SelectItem> seasons;
    private Integer season;
    private List<CustomItem> cultures;
    private CustomItem culture;
    private List<CustomItem> regions;
    private CustomItem region;
    private List<CustomItem> elevators;
    private CustomItem elevator;
    private String[] selectedPoints;
    private Date startDate;
    private Date endDate;

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

    public AuthBean getAuthBean() {
        return authBean;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }

    public List<CustomItem> getElevators() {
        return elevators;
    }

    public void setElevators(List<CustomItem> elevators) {
        this.elevators = elevators;
    }

    public CustomItem getElevator() {
        return elevator;
    }

    public void setElevator(CustomItem elevator) {
        this.elevator = elevator;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
