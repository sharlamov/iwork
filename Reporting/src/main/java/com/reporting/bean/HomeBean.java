package com.reporting.bean;

import com.reporting.util.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "homeBean")
@ApplicationScoped
public class HomeBean {

    private List<Object[]> reportList;
    private volatile int managersCount;
    private volatile int elevatorsCount;
    private volatile int regionsCount;

    @PostConstruct
    public void init() {
        reportList = new ArrayList<>();
    }

    public String increment(String rep) {
        switch (rep) {
            case "managers.xhtml":
                managersCount++;
                break;
            case "elevators.xhtml":
                elevatorsCount++;
                break;
            case "regions.xhtml":
                regionsCount++;
                break;
        }
        return rep + "?faces-redirect=true";
    }

    public int count(String rep) {
        switch (rep) {
            case "managers.xhtml":
                return managersCount;
            case "elevators.xhtml":
                return elevatorsCount;
            case "regions.xhtml":
                return regionsCount;
        }
        return 0;
    }

    public List<Object[]> getReportList() {
        AuthBean authBean = (AuthBean) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("authBean");
        reportList.clear();
        reportList.add(new Object[]{"managers.xhtml", "fa-line-chart", WebUtil.toText(authBean.getLocaleCode(), "Payments", "Plăți", "Платежи")});
        reportList.add(new Object[]{"elevators.xhtml", "fa-briefcase", WebUtil.toText(authBean.getLocaleCode(), "Remaining stock", "Solduri", "Остатки")});
        reportList.add(new Object[]{"regions.xhtml", "fa-bar-chart", WebUtil.toText(authBean.getLocaleCode(), "Regions", "Regiuni", "Регионы")});
        return reportList;
    }

    public void setReportList(List<Object[]> reportList) {
        this.reportList = reportList;
    }
}
