package com.reporting.bean;

import org.primefaces.model.chart.BarChartModel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;


@ManagedBean(name = "elevatorsBean")
@ViewScoped
public class ElevatorsBean extends AbstractReportBean {

    private BarChartModel model;
    private List<Object> pageDataList;

    @Override
    public void init() {
        setSeasons(getReportService().getSeasons(getLoggedUser()));
        setSeason(Integer.valueOf(getSeasons().get(getSeasons().size() - 1).getLabel()));

        setRegions(getReportService().getRegions());
        setRegion(getRegions().get(0));

        setCultures(getReportService().getCultures());
        setCulture(getCultures().get(0));
        applyFilters();
    }

    @Override
    public void applyFilters() {
        long t = System.currentTimeMillis();
        pageDataList = getReportService().getSilosSoldValues(getSeason(), getRegion(), getCulture());
        System.out.println((System.currentTimeMillis() - t) / 1000);
    }

    public BarChartModel getModel() {
        return model;
    }

    public void setModel(BarChartModel model) {
        this.model = model;
    }

    public List<Object> getPageDataList() {
        return pageDataList;
    }

    public void setPageDataList(List<Object> pageDataList) {
        this.pageDataList = pageDataList;
    }
}