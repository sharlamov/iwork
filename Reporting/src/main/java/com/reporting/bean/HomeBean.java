package com.reporting.bean;

import com.reporting.util.WebUtil;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "homeBean")
@ApplicationScoped
public class HomeBean extends AbstractBean {


    private static final long serialVersionUID = 4915047588553578520L;

    private List<Object[]> reportList;
    private AuthBean authBean;
    private volatile int managersCount;
    private volatile int elevatorsCount;
    private volatile int regionsCount;

    @Override
    public void init() {
        reportList = new ArrayList<>();
    }

    public String increment(String rep){
        if(rep.equals("managers.xhtml")) {
            managersCount++;
        }else if (rep.equals("elevators.xhtml")){
            elevatorsCount++;
        }else if (rep.equals("regions.xhtml")){
            regionsCount++;
        }
        return rep + "?faces-redirect=true";
    }

    public int count(String rep){
        if(rep.equals("managers.xhtml")) {
            return managersCount;
        }else if (rep.equals("elevators.xhtml")){
            return elevatorsCount;
        }else if (rep.equals("regions.xhtml")){
            return regionsCount;
        }
        return 0;
    }

    public List<Object[]> getReportList() {
        authBean = (AuthBean) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("authBean");
        reportList.clear();
        reportList.add(new Object[]{"managers.xhtml", "fa-line-chart", WebUtil.toText(authBean.getLocale(), "Payments", "Plăți", "Платежи")});
        reportList.add(new Object[]{"elevators.xhtml", "fa-briefcase", WebUtil.toText(authBean.getLocale(), "Remaining stock", "Solduri", "Остатки")});
        reportList.add(new Object[]{"regions.xhtml", "fa-bar-chart", WebUtil.toText(authBean.getLocale(), "Regions", "Regiuni", "Регионы")});
        return reportList;
    }

    public void setReportList(List<Object[]> reportList) {
        this.reportList = reportList;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }
}
