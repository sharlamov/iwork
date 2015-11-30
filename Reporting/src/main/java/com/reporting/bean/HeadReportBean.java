package com.reporting.bean;

import com.reporting.model.CustomItem;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


@ManagedBean(name = "headReportBean")
@ViewScoped
public class HeadReportBean extends AbstractReportBean {

    private List<Object> dataTable;
    private List<CustomItem> summaryTable;
    private HorizontalBarChartModel model;
    private String chartName;

    @PostConstruct
    public void init() {
        model = new HorizontalBarChartModel();
        model.setExtender("positionModel");

        List<CustomItem> lst0 = getReportService().getCultures();
        lst0.add(0, new CustomItem(new BigDecimal(0), getBundle().getString("filter.allculture")));
        setCultures(lst0);
        setCulture(getCultures().get(0));

        List<CustomItem> lst1 = getReportService().getElevators();
        lst1.add(0, new CustomItem(new BigDecimal(0), getBundle().getString("filter.allelevator")));
        setElevators(lst1);
        setElevator(getElevators().get(0));

        Calendar cal = Calendar.getInstance();
        setEndDate(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        setStartDate(cal.getTime());

        applyFilters();
    }

    @Override
    public void applyFilters() {
        List<Object> pageDataList = getReportService().getComodityContractedByDate(getStartDate(), getEndDate(), getCulture(), getElevator());
        Map<Date, Map<CustomItem, Double>> pivot = new LinkedHashMap<>();

        summaryTable = new ArrayList<>();

        for(Object obj: pageDataList){
            Object[] row = (Object[]) obj;

            CustomItem ci = new CustomItem(row[1], row[2]);
            Double value = ((BigDecimal)row[3]).doubleValue();

            int cInd = summaryTable.indexOf(ci);
            if(cInd == -1){
                ci.setName(value.toString());
                summaryTable.add(ci);
            }else {
                CustomItem item = summaryTable.get(cInd);
                Double newValue = Double.valueOf(item.getName()) + value;
                item.setName(newValue.toString());
            }

            if(pivot.containsKey(row[0])){
                pivot.get(row[0]).put(ci, value);
            }else{
                Map<CustomItem, Double> line = new HashMap<>();
                line.put(ci, value);
                pivot.put((Date) row[0], line);
            }
        }

        int count = summaryTable.size();
        dataTable = new ArrayList<>();

        for (Map.Entry<Date, Map<CustomItem, Double>> entry : pivot.entrySet()) {
            Object[] row = new Object[count + 1];
            row[0] = entry.getKey();
            for (int i = 0; i < count; i++) {
                if(entry.getValue().containsKey(summaryTable.get(i))){
                    double val = entry.getValue().get(summaryTable.get(i));
                    row[i + 1] = val;
                }
            }
            dataTable.add(row);
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (CustomItem customItem : summaryTable) {
            customItem.setName(df.format(Double.valueOf(customItem.getName())));
        }

    }

    public void initChart(CustomItem ci){
        List<Object> lst = getReportService().getComodityByElevator(getStartDate(), getEndDate(), ci);
        model.clear();
        ChartSeries cs = new ChartSeries();
        for(Object obj: lst) {
            Object[] row = (Object[]) obj;
            CustomItem silos = new CustomItem(row[0], row[1]);
            cs.set(silos, (BigDecimal) row[2]);
        }
        model.addSeries(cs);

        chartName = ci.getLabel();
    }

    public void initChartDate(CustomItem ci, Date date){
        List<Object> lst = getReportService().getComodityByElevator(date, date, ci);
        model.clear();
        ChartSeries cs = new ChartSeries();
        for(Object obj: lst) {
            Object[] row = (Object[]) obj;
            CustomItem silos = new CustomItem(row[0], row[1]);
            cs.set(silos, (BigDecimal) row[2]);
        }
        model.addSeries(cs);
        chartName = ci.getLabel() + ", " + date.toString();
    }

    public List<Object> getDataTable() {
        return dataTable;
    }

    public void setDataTable(List<Object> dataTable) {
        this.dataTable = dataTable;
    }

    public List<CustomItem> getSummaryTable() {
        return summaryTable;
    }

    public void setSummaryTable(List<CustomItem> summaryTable) {
        this.summaryTable = summaryTable;
    }

    public BarChartModel getModel() {
        return model;
    }

    public void setModel(HorizontalBarChartModel model) {
        this.model = model;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }
}