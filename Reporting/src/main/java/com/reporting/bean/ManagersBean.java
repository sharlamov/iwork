package com.reporting.bean;

import com.reporting.model.CustomItem;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ManagedBean(name = "managersBean")
@ViewScoped
public class ManagersBean extends AbstractReportBean {

    private HorizontalBarChartModel horizontalBarModel = new HorizontalBarChartModel();
    private LineChartModel modelPayment = new LineChartModel();
    private List<Object> dataManagers;
    private List<Object> dataClient;
    private Boolean isHorizontalBar;
    private Boolean isModelPayment;

    @PostConstruct
    public void init() {
        setSeasons(getReportService().getSeasons(getAuthBean().getLoggedUser()));
        setSeason(Integer.valueOf(getSeasons().get(getSeasons().size() - 1).getLabel()));

        setRegions(getReportService().getRegions());
        setRegion(getRegions().get(0));

        setCultures(getReportService().getCultures());
        setCulture(getCultures().get(0));
        applyFilters();
    }

    @Override
    public void applyFilters() {
        makeContractValue();
        dataManagers = getReportService().getReportManagersOnline(getSeason(), getRegion(), true, getCulture());
        dataClient = getReportService().getReportManagersOnline(getSeason(), getRegion(), false, getCulture());
        createReportPayment();
    }


    private void makeContractValue() {
        int i = 0;
        horizontalBarModel.clear();

        if (getCulture().getId().intValue() == 0) {
            List<Object> lst = getReportService().getReportContracts(getSeason(), getRegion(), getCulture());
            isHorizontalBar = !lst.isEmpty();
            if (isHorizontalBar) {
                Map<Object, ChartSeries> cultMap = new HashMap<>();
                List<CustomItem> personMap = new ArrayList<>();

                for (Object r : lst) {
                    Object[] row = (Object[]) r;
                    if (!cultMap.containsKey(row[3])) {
                        cultMap.put(row[3], new ChartSeries(row[4].toString()));
                    }

                    CustomItem person = new CustomItem(Long.valueOf(row[1].toString()), row[2].toString());
                    if (!personMap.contains(person))
                        personMap.add(person);
                    i++;
                }

                for (CustomItem pers : personMap) {
                    for (Map.Entry<Object, ChartSeries> cs : cultMap.entrySet()) {
                        BigDecimal bd = new BigDecimal(0);
                        for (Object r : lst) {
                            Object[] row = (Object[]) r;
                            i++;
                            if (pers.equals(row[1]) && cs.getKey().equals(row[3])) {
                                bd = (BigDecimal) row[5];
                                break;
                            }
                        }
                        cs.getValue().set(pers, bd);
                    }
                }

                for (Map.Entry<Object, ChartSeries> entry : cultMap.entrySet()) {
                    ChartSeries cs = entry.getValue();
                    horizontalBarModel.addSeries(cs);
                    for (CustomItem ci : getCultures()) {
                        if (ci.getId().equals(entry.getKey())) {
                            i++;
                            if (horizontalBarModel.getSeriesColors() != null) {
                                horizontalBarModel.setSeriesColors(horizontalBarModel.getSeriesColors() + "," + ci.getName());
                            } else {
                                horizontalBarModel.setSeriesColors(ci.getName());
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            List<Object> lst = getReportService().getReportContractsByCulture(getSeason(), getRegion(), getCulture());
            isHorizontalBar = !lst.isEmpty();
            if (isHorizontalBar) {
                ChartSeries otpis = new ChartSeries("Отписано");
                ChartSeries contr = new ChartSeries("Не отписано");

                for (Object r : lst) {
                    Object[] row = (Object[]) r;

                    CustomItem person = new CustomItem(row[1], row[2]);
                    otpis.set(person, (BigDecimal) row[4]);
                    contr.set(person, (BigDecimal) row[3]);
                    i++;
                }


                String hex0 = getCultures().get(getCultures().indexOf(getCulture())).getName();
                int value = new BigInteger(hex0, 16).intValue();
                String hex1 = Integer.toHexString(value - 50);

                horizontalBarModel.addSeries(otpis);
                horizontalBarModel.addSeries(contr);
                horizontalBarModel.setSeriesColors(hex1 + "," + hex0);
            }
        }
        System.out.println("Count of itereation: " + i);
        horizontalBarModel.setExtender("horizontalBarExt");
    }

    private void createReportPayment() {
        List<Object> lst = getReportService().getReportPayment(getSeason(), getRegion(), getCulture());
        modelPayment.clear();
        isModelPayment = !lst.isEmpty();
        if (isModelPayment) {
            LineChartSeries contr = new LineChartSeries();
            contr.setLabel(getBundle().getString("mng.chart2.name0"));
            LineChartSeries payed = new LineChartSeries();
            payed.setLabel(getBundle().getString("mng.chart2.name1"));
            LineChartSeries shiped = new LineChartSeries();
            shiped.setLabel(getBundle().getString("mng.chart2.name2"));

            for (Object r : lst) {
                Object[] row = (Object[]) r;
                long l = ((Timestamp) row[0]).getTime();
                contr.set(l, (BigDecimal) row[1]);
                payed.set(l, (BigDecimal) row[2]);
                shiped.set(l, (BigDecimal) row[3]);
            }

            modelPayment.addSeries(contr);
            modelPayment.addSeries(payed);
            modelPayment.addSeries(shiped);
            modelPayment.setExtender("modelPaymentExt");
        }
    }

    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }

    public void setHorizontalBarModel(HorizontalBarChartModel horizontalBarModel) {
        this.horizontalBarModel = horizontalBarModel;
    }

    public List<Object> getDataManagers() {
        return dataManagers;
    }

    public void setDataManagers(List<Object> dataManagers) {
        this.dataManagers = dataManagers;
    }

    public List<Object> getDataClient() {
        return dataClient;
    }

    public void setDataClient(List<Object> dataClient) {
        this.dataClient = dataClient;
    }

    public LineChartModel getModelPayment() {
        return modelPayment;
    }

    public void setModelPayment(LineChartModel modelPayment) {
        this.modelPayment = modelPayment;
    }

    public Boolean getIsHorizontalBar() {
        return isHorizontalBar;
    }

    public void setIsHorizontalBar(Boolean isHorizontalBar) {
        this.isHorizontalBar = isHorizontalBar;
    }

    public Boolean getIsModelPayment() {
        return isModelPayment;
    }

    public void setIsModelPayment(Boolean isModelPayment) {
        this.isModelPayment = isModelPayment;
    }
}