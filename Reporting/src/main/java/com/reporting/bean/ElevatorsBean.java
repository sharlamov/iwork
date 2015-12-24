package com.reporting.bean;

import com.reporting.model.CustomItem;
import com.reporting.util.PivotTable;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ManagedBean(name = "elevatorsBean")
@ViewScoped
public class ElevatorsBean extends AbstractReportBean {

    public final static String TRG = "Trans Oil Group";
    public final static String OTHERS = "Strangers siloses";
    private BarChartModel model;
    private List<Object> pageDataList;
    private List<Object[]> soldTable;
    private List<Object> summaryData;
    private PivotTable pivotTable;

    @PostConstruct
    public void init() {
        setSeasons(getReportService().getSeasons(getAuthBean().getCurrentUser()));
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
        List<Object> tempData = getReportService().getSilosGroupSold(getSeason(), getRegion(), getCulture());
        List<Object[]> processedData = new ArrayList<>();

        for (Object o : tempData) {
            Object[] tempRow = (Object[]) o;
            Object[] row = new Object[4];
            row[0] = new CustomItem(tempRow[0], tempRow[1]);
            row[1] = new CustomItem(tempRow[2], tempRow[3]);
            row[2] = tempRow[4];
            row[3] = tempRow[5];
            processedData.add(row);
        }

        pivotTable = new PivotTable(new int[]{1}, new int[]{0, 2}, new int[]{3}, processedData);
        pivotTable.print();

        System.out.println((System.currentTimeMillis() - t) / 1000);
    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        model.setShowPointLabels(true);
        model.setExtender("soldsModel");

        for(Object[] row :soldTable){
            ChartSeries cs = new ChartSeries();
            cs.setLabel(row[0].toString());
            for(int i = 1; i < row.length; i++){
                Object[] summary = (Object[]) summaryData.get(i - 1);
                cs.set(summary[0], (BigDecimal) row[i]);
            }
            model.addSeries(cs);
        }

        return model;
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

    public List<Object> getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(List<Object> summaryData) {
        this.summaryData = summaryData;
    }

    public List<Object[]> getSoldTable() {
        return soldTable;
    }

    public void setSoldTable(List<Object[]> soldTable) {
        this.soldTable = soldTable;
    }

    public PivotTable getPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(PivotTable pivotTable) {
        this.pivotTable = pivotTable;
    }
}