package com.reporting.bean;

import org.primefaces.context.RequestContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;


@ManagedBean(name = "regionsBean")
@ViewScoped
public class RegionsBean extends AbstractReportBean {

    private List<Object> pageDataList;
    private String contextPath;

    @Override
    public void init() {
        contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

        setSeasons(getReportService().getSeasons(getLoggedUser()));
        setSeason(Integer.valueOf(getSeasons().get(getSeasons().size() - 1).getLabel()));

        setCultures(getReportService().getCultures());
        setCulture(getCultures().get(0));

        pageDataList = getReportService().getContractByDistrict(getSeason(), getCulture().getId().intValue());

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("renderMap('" + contextPath + "', " + getSeason() + ", " + getCulture().getId().intValue() + ");");
    }

    @Override
    public void applyFilters() {
        pageDataList = getReportService().getContractByDistrict(getSeason(), getCulture().getId().intValue());

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("updateDataMap('" + contextPath + "', " + getSeason() + ", " + getCulture().getId().intValue() + ", " + arrayToStr(getSelectedPoints()) + ");");
    }

    public String arrayToStr(String[] array){
        StringBuilder res = new StringBuilder("[");
        if(array != null) {
            for (String str : array) {
                if (res.length() > 1) {
                    res.append(',');
                }
                res.append(str);
            }
        }
        res.append("]");
        return res.toString();
    }

    public List<Object> getPageDataList() {
        return pageDataList;
    }

    public void setPageDataList(List<Object> pageDataList) {
        this.pageDataList = pageDataList;
    }
}