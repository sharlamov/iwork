package com.reporting.bean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "homeBean")
@ViewScoped
public class HomeBean extends AbstractReportBean {

    private List<Object> reportList;

    @PostConstruct
    public void init() {
        applyFilters();
    }

    @Override
    public void applyFilters() {
        reportList = getReportService().getFirstLevelMenu();
    }

    public List<Object> getReportList() {
        return reportList;
    }

    public void getTest() {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish("/notify", new FacesMessage("Привет", "Тестовое уведомление"));
    }

    public List<Object> getDetailList(BigDecimal pid) {
        return getReportService().getChildrenMenu(pid.intValue());
    }

    public void setReportList(List<Object> reportList) {
        this.reportList = reportList;
    }

  /*  public List<Object[]> getReportList() {
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
    }*/

}
